package com.example.visualeffects.renderers;

import com.example.visualeffects.SnowEffect;
import com.example.visualeffects.WindEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class SnowRenderer {
    private static final Identifier SNOW_TEXTURE = new Identifier("modid", "textures/enviornment/snow.png");
    private static final Random RANDOM = new Random();
    private static final int MAX_SNOWFLAKES = 10000;
    private static Snowflake[] snowflakes;

    private static class Snowflake {
        double x, y, z;
        double size;
        double fallSpeed;

        Snowflake(double radius, boolean isSphere) {
            resetPosition(radius, isSphere);
            this.size = RANDOM.nextDouble() * (SnowEffect.getMaxSize() - SnowEffect.getMinSize()) + SnowEffect.getMinSize();
            this.fallSpeed = calculateFallSpeed(this.size);
        }

        void resetPosition(double radius, boolean isSphere) {
            if (isSphere) {
                double theta = RANDOM.nextDouble() * 2 * Math.PI;
                double phi = Math.acos(2 * RANDOM.nextDouble() - 1);
                this.x = radius * Math.sin(phi) * Math.cos(theta);
                this.y = radius * Math.sin(phi) * Math.sin(theta);
                this.z = radius * Math.cos(phi);
            } else {
                this.x = (RANDOM.nextDouble() - 0.5) * radius * 2;
                this.y = RANDOM.nextDouble() * radius;
                this.z = (RANDOM.nextDouble() - 0.5) * radius * 2;
            }
        }

        void update(double deltaTime, double radius, boolean isSphere) {
            y -= fallSpeed * SnowEffect.getFallSpeed() * deltaTime;

            if (WindEffect.isActive()) {
                double windStrength = WindEffect.getStrength();
                double windDirection = WindEffect.getDirection();
                x += Math.cos(windDirection) * windStrength * deltaTime;
                z += Math.sin(windDirection) * windStrength * deltaTime;
            }

            if (y < -radius) {
                resetPosition(radius, isSphere);
            }

            // Apply shimmy effect
            double shimmy = Math.sin(y * 0.1) * SnowEffect.getShimmyStrength();
            x += shimmy * deltaTime;
        }

        private double calculateFallSpeed(double size) {
            // Larger snowflakes fall faster
            return size / SnowEffect.getMaxSize() * SnowEffect.getFallSpeed();
        }
    }

    public static void render(MatrixStack matrices, float tickDelta) {
        if (!SnowEffect.isActive()) {
            System.out.println("Snow effect is not active.");
            return;
        }

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            System.out.println("Player not found.");
            return;
        }

        Vec3d playerPos = player.getPos();
        System.out.println("Rendering snow effect at player position: " + playerPos);

        if (snowflakes == null || snowflakes.length != SnowEffect.getCount()) {
            initializeSnowflakes();
        }

        System.out.println("Setting shader and texture...");
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, SNOW_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        for (Snowflake snowflake : snowflakes) {
            snowflake.update(tickDelta, SnowEffect.getRadius(), SnowEffect.isSphereShape());
            renderSnowflake(matrices, bufferBuilder, snowflake, playerPos);
        }

        BufferRenderer.draw(bufferBuilder.end());

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static void initializeSnowflakes() {
        System.out.println("Initializing snowflakes...");
        snowflakes = new Snowflake[SnowEffect.getCount()];
        for (int i = 0; i < snowflakes.length; i++) {
            snowflakes[i] = new Snowflake(SnowEffect.getRadius(), SnowEffect.isSphereShape());
        }
    }

    private static void renderSnowflake(MatrixStack matrices, BufferBuilder bufferBuilder, Snowflake snowflake, Vec3d playerPos) {
        matrices.push();
        matrices.translate(snowflake.x - playerPos.x, snowflake.y - playerPos.y, snowflake.z - playerPos.z);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float size = (float) snowflake.size / 2;

        int light = getLightLevel(snowflake, playerPos);
        int alpha = SnowEffect.isAffectedByLight() ? light : 255;

        bufferBuilder.vertex(matrix, -size, -size, 0).texture(0, 1).color(255, 255, 255, alpha).next();
        bufferBuilder.vertex(matrix, size, -size, 0).texture(1, 1).color(255, 255, 255, alpha).next();
        bufferBuilder.vertex(matrix, size, size, 0).texture(1, 0).color(255, 255, 255, alpha).next();
        bufferBuilder.vertex(matrix, -size, size, 0).texture(0, 0).color(255, 255, 255, alpha).next();

        matrices.pop();
    }

    private static int getLightLevel(Snowflake snowflake, Vec3d playerPos) {
        // This is a placeholder. You'll need to implement actual light level calculation based on the world.
        return 15;
    }
}
