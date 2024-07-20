package com.example.render;

import com.example.SnowEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Quaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class SnowRenderer {
    private static final Identifier SNOW_TEXTURE = new Identifier("modid", "textures/particle/snow.png");
    private static final Random RANDOM = new Random();
    private static final List<Snowflake> snowflakes = new ArrayList<>();
    private static boolean initialized = false;

    private static class Snowflake {
        Vec3d position;
        float size;
        Vector3f spinAxis;
        float spinSpeed;
        float fallSpeed;
        Quaternionf rotation;

        Snowflake(Vec3d pos, float size, float spinSpeed, float fallSpeed) {
            this.position = pos;
            this.size = size;
            this.spinAxis = new Vector3f(RANDOM.nextFloat() - 0.5f, RANDOM.nextFloat() - 0.5f, RANDOM.nextFloat() - 0.5f).normalize();
            this.spinSpeed = spinSpeed;
            this.fallSpeed = fallSpeed;
            this.rotation = new Quaternionf().rotateAxis(RANDOM.nextFloat() * 360f, RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat());
        }
    }

    public static void render(MatrixStack matrices, float tickDelta) {
        if (!SnowEffect.isSnowing()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        if (!initialized || SnowEffect.hasChanged()) {
            initializeSnowflakes(cameraPos);
        }

        // Update snowflakes and check bounds
        for (Snowflake snowflake : snowflakes) {
            updateSnowflake(snowflake, cameraPos, tickDelta);
            if (isOutOfBounds(snowflake.position, cameraPos)) {
                respawnSnowflake(snowflake, cameraPos);
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, SNOW_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        matrices.push();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        for (Snowflake snowflake : snowflakes) {
            if (isInFrustum(camera, snowflake.position, snowflake.size)) {
                renderSnowflake(matrices, bufferBuilder, snowflake, cameraPos);
            }
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrices.pop();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static boolean isInFrustum(Camera camera, Vec3d position, float size) {
        Vec3d cameraPos = camera.getPos();
        Vector3f viewVector = camera.getHorizontalPlane();
        Vec3d toSnowflake = position.subtract(cameraPos);

        double dotProduct = toSnowflake.x * viewVector.x + toSnowflake.y * viewVector.y + toSnowflake.z * viewVector.z;
        double distanceSq = toSnowflake.lengthSquared();

        if (dotProduct < 0 && distanceSq > size * size) {
            return false;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        double fov = Math.toRadians(client.options.getFov().getValue());
        double aspectRatio = (double) client.getWindow().getWidth() / client.getWindow().getHeight();

        double halfFovTanVertical = Math.tan(fov / 2);
        double halfFovTanHorizontal = halfFovTanVertical * aspectRatio * 1.5; // Increase horizontal FOV by 50%

        // Project the snowflake onto the view plane
        Vec3d projectedSnowflake = new Vec3d(
                toSnowflake.x - viewVector.x * dotProduct,
                toSnowflake.y - viewVector.y * dotProduct,
                toSnowflake.z - viewVector.z * dotProduct
        );

        double horizontalDistance = Math.sqrt(projectedSnowflake.x * projectedSnowflake.x + projectedSnowflake.z * projectedSnowflake.z);
        double verticalDistance = Math.abs(projectedSnowflake.y);

        // Check if the snowflake is within the rectangular frustum
        return (horizontalDistance / dotProduct <= halfFovTanHorizontal) && (verticalDistance / dotProduct <= halfFovTanVertical);
    }

    private static void initializeSnowflakes(Vec3d cameraPos) {
        snowflakes.clear();
        float radius = SnowEffect.getSnowRadius(); // Increase initial spawn radius by 1%
        int count = SnowEffect.getSnowflakeCount();
        float baseSize = SnowEffect.getSnowflakeSize();
        float baseSpinSpeed = SnowEffect.getSpinSpeed();
        float baseFallSpeed = SnowEffect.getFallSpeed();

        for (int i = 0; i < count; i++) {
            double angle = RANDOM.nextDouble() * Math.PI * 2;
            double r = Math.sqrt(RANDOM.nextDouble()) * radius;
            double x = cameraPos.x + Math.cos(angle) * r;
            double z = cameraPos.z + Math.sin(angle) * r;
            double y = cameraPos.y + RANDOM.nextDouble() * radius * 2 - radius;

            float size = baseSize * (1 + (RANDOM.nextFloat() - 0.5f) * 0.3f); // ±15% variation
            float spinSpeed = baseSpinSpeed * (1 + (RANDOM.nextFloat() - 0.5f) * 0.2f);
            float fallSpeed = baseFallSpeed * (1 + (RANDOM.nextFloat() - 0.5f) * 0.2f);

            snowflakes.add(new Snowflake(new Vec3d(x, y, z), size, spinSpeed, fallSpeed));
        }

        initialized = true;
    }

    private static void updateSnowflake(Snowflake snowflake, Vec3d cameraPos, float tickDelta) {
        float fallAngle = (float) Math.toRadians(SnowEffect.getFallAngle());
        float horizontalSpeed = snowflake.fallSpeed * (float) Math.sin(fallAngle);
        float verticalSpeed = snowflake.fallSpeed * (float) Math.cos(fallAngle);

        snowflake.position = snowflake.position.add(
                horizontalSpeed * tickDelta,
                -verticalSpeed * tickDelta,
                0
        );

        snowflake.rotation.rotateAxis(snowflake.spinSpeed * tickDelta, snowflake.spinAxis);
    }

    private static boolean isOutOfBounds(Vec3d position, Vec3d cameraPos) {
        float radius = SnowEffect.getSnowRadius();
        double dx = position.x - cameraPos.x;
        double dz = position.z - cameraPos.z;
        return Math.sqrt(dx*dx + dz*dz) > radius || position.y < cameraPos.y - radius;
    }

    private static boolean isInsideBlock(Vec3d position) {
        BlockPos blockPos = new BlockPos((int)position.x, (int)position.y, (int)position.z);
        return MinecraftClient.getInstance().world.getBlockState(blockPos).isOpaque();
    }

    private static void respawnSnowflake(Snowflake snowflake, Vec3d cameraPos) {
        float radius = SnowEffect.getSnowRadius() * 1.01f; // Increase spawn radius by 1%
        double angle = RANDOM.nextDouble() * Math.PI * 2;
        double r = Math.sqrt(RANDOM.nextDouble()) * radius;
        double x = Math.cos(angle) * r;
        double z = Math.sin(angle) * r;

        // Determine if snowflake should spawn above or around
        if (RANDOM.nextDouble() < 0.7) { // 70% chance to spawn above
            snowflake.position = new Vec3d(
                    cameraPos.x + x,
                    cameraPos.y + radius,
                    cameraPos.z + z
            );
        } else { // 30% chance to spawn around
            double y = RANDOM.nextDouble() * radius * 2 - radius;
            snowflake.position = new Vec3d(
                    cameraPos.x + x,
                    cameraPos.y + y,
                    cameraPos.z + z
            );
        }
    }

    private static void renderSnowflake(MatrixStack matrices, BufferBuilder bufferBuilder, Snowflake snowflake, Vec3d cameraPos) {
        matrices.push();
        matrices.translate(
                snowflake.position.x - cameraPos.x,
                snowflake.position.y - cameraPos.y,
                snowflake.position.z - cameraPos.z
        );
        matrices.multiply(snowflake.rotation);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float halfSize = snowflake.size / 2;

        bufferBuilder.vertex(matrix, -halfSize, -halfSize, 0).texture(0, 1).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(matrix, halfSize, -halfSize, 0).texture(1, 1).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(matrix, halfSize, halfSize, 0).texture(1, 0).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(matrix, -halfSize, halfSize, 0).texture(0, 0).color(255, 255, 255, 255).next();

        matrices.pop();
    }
}