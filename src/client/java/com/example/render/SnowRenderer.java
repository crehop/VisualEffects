package com.example.render;

import com.example.SnowEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SnowRenderer {
    private static final Identifier SNOW_TEXTURE = new Identifier("modid", "textures/particle/snow.png");
    private static boolean lastSnowState = false;
    private static float lastSize = 0;
    private static int lastCount = 0;
    private static float lastRadius = 0;
    private static int lastSpinSpeed = 0;
    private static boolean textureLoaded = false;
    private static boolean shaderLoaded = false;

    public static void render(MatrixStack matrices, float tickDelta) {
        boolean currentSnowState = SnowEffect.isSnowing();
        float currentSize = SnowEffect.getSnowflakeSize();
        int currentCount = SnowEffect.getSnowflakeCount();
        float currentRadius = SnowEffect.getSnowRadius();
        int currentSpinSpeed = SnowEffect.getSpinSpeed();

        // Check if snow state or parameters have changed
        if (currentSnowState != lastSnowState ||
                currentSize != lastSize ||
                currentCount != lastCount ||
                currentRadius != lastRadius ||
                currentSpinSpeed != lastSpinSpeed) {

            if (currentSnowState) {
                System.out.println("Snow effect activated: Size: " + currentSize +
                        ", Count: " + currentCount +
                        ", Radius: " + currentRadius +
                        ", Spin Speed: " + currentSpinSpeed);
            } else {
                System.out.println("Snow effect deactivated");
            }

            // Update last known state
            lastSnowState = currentSnowState;
            lastSize = currentSize;
            lastCount = currentCount;
            lastRadius = currentRadius;
            lastSpinSpeed = currentSpinSpeed;

            // Reset texture and shader load flags
            textureLoaded = false;
            shaderLoaded = false;
        }

        if (!currentSnowState) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        // Check if texture is loaded
        if (!textureLoaded) {
            try {
                client.getTextureManager().getTexture(SNOW_TEXTURE);
                System.out.println("Snow texture loaded successfully");
                textureLoaded = true;
            } catch (Exception e) {
                System.out.println("Failed to load snow texture: " + e.getMessage());
                return;
            }
        }

        // Check if shader is loaded
        if (!shaderLoaded) {
            try {
                RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
                System.out.println("Snow shader loaded successfully");
                shaderLoaded = true;
            } catch (Exception e) {
                System.out.println("Failed to load snow shader: " + e.getMessage());
                return;
            }
        }

        RenderSystem.setShaderTexture(0, SNOW_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);

        matrices.push();

        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        long gameTime = client.world.getTime();
        float rotation = (gameTime % 360) * currentSpinSpeed / 10f;

        for (int i = 0; i < currentCount; i++) {
            double x = (Math.random() - 0.5) * currentRadius * 2;
            double y = (Math.random() - 0.5) * currentRadius * 2;
            double z = (Math.random() - 0.5) * currentRadius * 2;

            x += cameraPos.x;
            y += cameraPos.y;
            z += cameraPos.z;

            matrices.push();
            matrices.translate(x, y, z);
            matrices.multiply(new Quaternionf().rotateY(rotation));

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            bufferBuilder.vertex(matrix, -currentSize, -currentSize, 0).texture(0, 1).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(matrix, currentSize, -currentSize, 0).texture(1, 1).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(matrix, currentSize, currentSize, 0).texture(1, 0).color(255, 255, 255, 255).next();
            bufferBuilder.vertex(matrix, -currentSize, currentSize, 0).texture(0, 0).color(255, 255, 255, 255).next();

            matrices.pop();
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrices.pop();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();

        System.out.println("Snow rendered successfully");
    }
}
