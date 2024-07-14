package com.example.visualeffects.renderers;

import com.example.visualeffects.FogEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class FogRenderer {
    private static final Identifier FOG_TEXTURE = new Identifier("textures/environment/fog.png");

    public static void render(MatrixStack matrices, float tickDelta, Vec3d cameraPos) {
        if (!FogEffect.isActive()) return;

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, FOG_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        renderFogLayer(matrices, bufferBuilder, cameraPos);

        BufferRenderer.draw(bufferBuilder.end());

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static void renderFogLayer(MatrixStack matrices, BufferBuilder bufferBuilder, Vec3d cameraPos) {
        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        double radius = FogEffect.getRadius();
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        int segments = 32;
        float textureScale = 1.0f;

        for (int i = 0; i < segments; i++) {
            double angle1 = (double) i / segments * Math.PI * 2;
            double angle2 = (double) (i + 1) / segments * Math.PI * 2;

            double x1 = Math.sin(angle1) * radius;
            double z1 = Math.cos(angle1) * radius;
            double x2 = Math.sin(angle2) * radius;
            double z2 = Math.cos(angle2) * radius;

            float u1 = (float) i / segments * textureScale;
            float u2 = (float) (i + 1) / segments * textureScale;

            int alpha = (int) (FogEffect.getStrength() * 255);
            int light = getLightLevel(cameraPos);

            // Bottom of the cylinder
            bufferBuilder.vertex(matrix, (float) x1, 0, (float) z1).texture(u1, 0).color(255, 255, 255, alpha).light(light).next();
            bufferBuilder.vertex(matrix, (float) x2, 0, (float) z2).texture(u2, 0).color(255, 255, 255, alpha).light(light).next();
            bufferBuilder.vertex(matrix, (float) x2, (float) (radius * 2), (float) z2).texture(u2, 1).color(255, 255, 255, alpha).light(light).next();
            bufferBuilder.vertex(matrix, (float) x1, (float) (radius * 2), (float) z1).texture(u1, 1).color(255, 255, 255, alpha).light(light).next();
        }

        matrices.pop();
    }

    private static int getLightLevel(Vec3d cameraPos) {
        // This is a placeholder. You'll need to implement actual light level calculation based on the world.
        return 15;
    }
}