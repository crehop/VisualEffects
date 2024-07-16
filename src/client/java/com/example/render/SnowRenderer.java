package com.example.render;

import com.example.SnowEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class SnowRenderer {
    private static final Identifier SNOW_TEXTURE = new Identifier("modid", "textures/particle/snow.png");
    private static boolean textureLoaded = false;
    private static boolean shaderLoaded = false;
    private static Vector3f[] snowflakes;
    private static float[] snowflakeSizes;
    private static Vector3f[] snowflakeDrift;
    private static Random random = new Random();
    private static Vec3d lastPlayerPos = Vec3d.ZERO;

    public static void render(MatrixStack matrices, float tickDelta) {
        if (!SnowEffect.isSnowing()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        if (!textureLoaded || !shaderLoaded) {
            if (!loadTextureAndShader(client)) return;
        }

        RenderSystem.setShaderTexture(0, SNOW_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.enableDepthTest();

        matrices.push();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float size = SnowEffect.getSnowflakeSize();
        int count = SnowEffect.getSnowflakeCount();
        float radius = SnowEffect.getSnowRadius();
        float fallSpeed = SnowEffect.getFallSpeed();

        if (snowflakes == null || snowflakes.length != count) {
            initializeSnowflakes(count, radius, cameraPos);
        }

        Frustum frustum = new Frustum(matrices.peek().getPositionMatrix(), RenderSystem.getProjectionMatrix());
        frustum.setPosition(cameraPos.x, cameraPos.y, cameraPos.z);

        Vec3d playerMovement = cameraPos.subtract(lastPlayerPos);
        lastPlayerPos = cameraPos;

        for (int i = 0; i < count; i++) {
            updateSnowflake(i, cameraPos, radius, fallSpeed, tickDelta, client, playerMovement);

            Vector3f flake = snowflakes[i];
            Box flakeBox = new Box(
                    flake.x - size, flake.y - size, flake.z - size,
                    flake.x + size, flake.y + size, flake.z + size
            );

            if (!frustum.isVisible(flakeBox)) {
                continue;
            }

            float distanceFromCenter = (float) Math.sqrt(
                    Math.pow(flake.x - cameraPos.x, 2) +
                            Math.pow(flake.z - cameraPos.z, 2)
            );
            float alpha = Math.max(0, Math.min(1, (radius - distanceFromCenter) / (radius * 0.2f)));

            renderSnowflake(matrices, bufferBuilder, flake, snowflakeSizes[i] * size, alpha);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrices.pop();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
    }

    private static boolean loadTextureAndShader(MinecraftClient client) {
        if (!textureLoaded) {
            try {
                client.getTextureManager().getTexture(SNOW_TEXTURE);
                textureLoaded = true;
            } catch (Exception e) {
                return false;
            }
        }

        if (!shaderLoaded) {
            try {
                RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
                shaderLoaded = true;
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    private static void initializeSnowflakes(int count, float radius, Vec3d playerPos) {
        snowflakes = new Vector3f[count];
        snowflakeSizes = new float[count];
        snowflakeDrift = new Vector3f[count];
        float sizeVariation = SnowEffect.getSizeVariation();
        float driftSpeed = SnowEffect.getDriftSpeed();
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * (float) Math.PI * 2;
            float distance = (float) Math.sqrt(random.nextFloat()) * radius;
            snowflakes[i] = new Vector3f(
                    (float) (Math.cos(angle) * distance + playerPos.x),
                    (float) (random.nextFloat() * radius * 2 + playerPos.y - radius),
                    (float) (Math.sin(angle) * distance + playerPos.z)
            );
            snowflakeSizes[i] = 1f - (random.nextFloat() * sizeVariation);
            snowflakeDrift[i] = new Vector3f(
                    (random.nextFloat() - 0.5f) * driftSpeed,
                    0,
                    (random.nextFloat() - 0.5f) * driftSpeed
            );
        }
    }

    private static void updateSnowflake(int index, Vec3d cameraPos, float radius, float fallSpeed, float tickDelta, MinecraftClient client, Vec3d playerMovement) {
        Vector3f flake = snowflakes[index];
        Vector3f drift = snowflakeDrift[index];

        flake.add(
                (float) (drift.x * tickDelta - playerMovement.x),
                (float) (-fallSpeed * tickDelta - playerMovement.y),
                (float) (drift.z * tickDelta - playerMovement.z)
        );

        // Wrap snowflakes around the render area
        float distanceFromPlayer = (float) Math.sqrt(
                Math.pow(flake.x - cameraPos.x, 2) +
                        Math.pow(flake.z - cameraPos.z, 2)
        );

        if (distanceFromPlayer > radius || flake.y < cameraPos.y - radius || flake.y > cameraPos.y + radius) {
            resetSnowflake(index, cameraPos, radius);
        }

        // Check for terrain collision
        BlockPos blockPos = new BlockPos((int)flake.x, (int)flake.y, (int)flake.z);
        int surfaceY = client.world.getChunk(blockPos).getHeightmap(Heightmap.Type.WORLD_SURFACE).get(blockPos.getX() & 15, blockPos.getZ() & 15);
        if (flake.y <= surfaceY) {
            resetSnowflake(index, cameraPos, radius);
        }
    }

    private static void resetSnowflake(int index, Vec3d cameraPos, float radius) {
        float angle = random.nextFloat() * (float) Math.PI * 2;
        float distance = (float) Math.sqrt(random.nextFloat()) * radius;
        snowflakes[index].set(
                (float) (Math.cos(angle) * distance + cameraPos.x),
                (float) (cameraPos.y + radius),
                (float) (Math.sin(angle) * distance + cameraPos.z)
        );
    }

    private static void renderSnowflake(MatrixStack matrices, BufferBuilder bufferBuilder, Vector3f flake, float size, float alpha) {
        matrices.push();
        matrices.translate(flake.x, flake.y, flake.z);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        int color = (int)(alpha * 255) << 24 | 0xFFFFFF;

        bufferBuilder.vertex(matrix, -size, -size, 0).texture(0, 1).color(color).next();
        bufferBuilder.vertex(matrix, size, -size, 0).texture(1, 1).color(color).next();
        bufferBuilder.vertex(matrix, size, size, 0).texture(1, 0).color(color).next();
        bufferBuilder.vertex(matrix, -size, size, 0).texture(0, 0).color(color).next();

        matrices.pop();
    }

    public static void reset() {
        textureLoaded = false;
        shaderLoaded = false;
        snowflakes = null;
        snowflakeSizes = null;
        snowflakeDrift = null;
    }
}