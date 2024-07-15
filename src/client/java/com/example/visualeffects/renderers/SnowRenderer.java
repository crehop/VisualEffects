package com.example.visualeffects.renderers;

import com.example.visualeffects.SnowEffect;
import com.example.visualeffects.WindEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.particle.ParticleTypes;

@Environment(EnvType.CLIENT)
public class SnowRenderer {
    private static final Identifier SNOW_TEXTURE = new Identifier("visualeffects", "textures/environment/snow.png");
    private static final Random RANDOM = Random.create();
    private static final int MAX_SNOWFLAKES = 10000;

    public static void render(MatrixStack matrices, float tickDelta) {
        if (!SnowEffect.isActive()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) {
            return;
        }

        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        double x = client.player.getX();
        double y = client.player.getY();
        double z = client.player.getZ();

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, SNOW_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        for (int i = 0; i < SnowEffect.getCount(); i++) {
            double snowX = x + RANDOM.nextGaussian() * SnowEffect.getRadius();
            double snowY = y + RANDOM.nextDouble() * SnowEffect.getRadius();
            double snowZ = z + RANDOM.nextGaussian() * SnowEffect.getRadius();

            // Apply wind effect
            if (WindEffect.isActive()) {
                double windStrength = WindEffect.getStrength();
                double windDirection = WindEffect.getDirection();
                snowX += Math.cos(windDirection) * windStrength;
                snowZ += Math.sin(windDirection) * windStrength;
            }

            // Render snow particle
            renderSnowflake(matrices, bufferBuilder, snowX - cameraPos.x, snowY - cameraPos.y, snowZ - cameraPos.z, SnowEffect.getMaxSize());

            // Render debug particle
            if (i % 100 == 0) {  // Render fewer debug particles
                MinecraftClient.getInstance().particleManager.addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        snowX, snowY + 1, snowZ  + 1,
                        0.0, 0.0, 0.0
                );            }
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }

    private static void renderSnowflake(MatrixStack matrices, BufferBuilder bufferBuilder, double x, double y, double z, double size) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float halfSize = (float) size / 2;

        bufferBuilder.vertex(matrix, (float) (x - halfSize), (float) (y - halfSize), (float) z).texture(0, 1).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(matrix, (float) (x + halfSize), (float) (y - halfSize), (float) z).texture(1, 1).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(matrix, (float) (x + halfSize), (float) (y + halfSize), (float) z).texture(1, 0).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(matrix, (float) (x - halfSize), (float) (y + halfSize), (float) z).texture(0, 0).color(255, 255, 255, 255).next();
    }
}