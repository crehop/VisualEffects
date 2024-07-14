package com.example.visualeffects.renderers;

import com.example.visualeffects.FogEffect;
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
public class FogRenderer {
    private static final Identifier FOG_TEXTURE = new Identifier("modid", "textures/environment/fog.png");
    private static final Random RANDOM = new Random();
    private static final int MAX_FOG_PARTICLES = 10000;
    private static FogParticle[] fogParticles;

    private static class FogParticle {
        double x, y, z;
        double size;
        double moveSpeed;

        FogParticle(double radius, boolean isSphere) {
            resetPosition(radius, isSphere);
            this.size = RANDOM.nextDouble() * (FogEffect.getMaxSize() - FogEffect.getMinSize()) + FogEffect.getMinSize();
            this.moveSpeed = calculateMoveSpeed(this.size);
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
            y -= moveSpeed * FogEffect.getMoveSpeed() * deltaTime;

            if (WindEffect.isActive()) {
                double windStrength = WindEffect.getStrength();
                double windDirection = WindEffect.getDirection();
                x += Math.cos(windDirection) * windStrength * deltaTime;
                z += Math.sin(windDirection) * windStrength * deltaTime;
            }

            if (y < -radius) {
                resetPosition(radius, isSphere);
            }

            // Apply swirl effect
            double swirl = Math.sin(y * 0.1) * FogEffect.getSwirlStrength();
            x += swirl * deltaTime;
        }

        private double calculateMoveSpeed(double size) {
            // Larger fog particles move slower
            return size / FogEffect.getMaxSize() * FogEffect.getMoveSpeed();
        }
    }

    public static void render(MatrixStack matrices, float tickDelta) {
        if (!FogEffect.isActive()) {
            System.out.println("Fog effect is not active.");
            return;
        }

        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            System.out.println("Player not found.");
            return;
        }

        Vec3d playerPos = player.getPos();
        System.out.println("Rendering fog effect at player position: " + playerPos);

        if (fogParticles == null || fogParticles.length != FogEffect.getCount()) {
            initializeFogParticles();
        }

        System.out.println("Setting shader and texture...");
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, FOG_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        for (FogParticle fogParticle : fogParticles) {
            fogParticle.update(tickDelta, FogEffect.getRadius(), FogEffect.isSphereShape());
            renderFogParticle(matrices, bufferBuilder, fogParticle, playerPos);
        }

        BufferRenderer.draw(bufferBuilder.end());

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static void initializeFogParticles() {
        System.out.println("Initializing fog particles...");
        fogParticles = new FogParticle[FogEffect.getCount()];
        for (int i = 0; i < fogParticles.length; i++) {
            fogParticles[i] = new FogParticle(FogEffect.getRadius(), FogEffect.isSphereShape());
        }
    }

    private static void renderFogParticle(MatrixStack matrices, BufferBuilder bufferBuilder, FogParticle fogParticle, Vec3d playerPos) {
        matrices.push();
        matrices.translate(fogParticle.x - playerPos.x, fogParticle.y - playerPos.y, fogParticle.z - playerPos.z);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float size = (float) fogParticle.size / 2;

        int light = getLightLevel(fogParticle, playerPos);
        int alpha = FogEffect.isAffectedByLight() ? light : 255;

        bufferBuilder.vertex(matrix, -size, -size, 0).texture(0, 1).color(255, 255, 255, alpha).next();
        bufferBuilder.vertex(matrix, size, -size, 0).texture(1, 1).color(255, 255, 255, alpha).next();
        bufferBuilder.vertex(matrix, size, size, 0).texture(1, 0).color(255, 255, 255, alpha).next();
        bufferBuilder.vertex(matrix, -size, size, 0).texture(0, 0).color(255, 255, 255, alpha).next();

        matrices.pop();
    }

    private static int getLightLevel(FogParticle fogParticle, Vec3d playerPos) {
        // This is a placeholder. You'll need to implement actual light level calculation based on the world.
        return 15;
    }
}
