package com.effects;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class Portal {
    private static final Identifier PORTAL_TEXTURE = Identifier.of("effects", "textures/portal/portal_animation.png");
    private static final Identifier TEST_TEXTURE = Identifier.of("minecraft", "textures/block/stone.png");
    private static final float PORTAL_WIDTH = 4f;
    private static final float PORTAL_HEIGHT = 4f;
    private static final int COLUMNS = 5;
    private static final int ROWS = 6;
    private static final int TOTAL_FRAMES = COLUMNS * (ROWS - 1) + 4; // Last row has only 4 items

    private final BlockPos position;
    private final long creationTime;
    private final int animationSpeed;

    public Portal(BlockPos position, int animationSpeed) {
        this.position = position;
        this.creationTime = System.currentTimeMillis();
        this.animationSpeed = animationSpeed;
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, Camera camera) {
        Vec3d cameraPos = camera.getPos();
        Vec3d portalPos = new Vec3d(position.getX() + 0.5, position.getY() + 1.5, position.getZ() + 0.5);

        long currentTime = System.currentTimeMillis();
        int frame = (int) ((currentTime - creationTime) / (1000f / animationSpeed)) % TOTAL_FRAMES;
        int col = frame % COLUMNS;
        int row = frame / COLUMNS;

        float minU = col * (1f / COLUMNS);
        float maxU = (col + 1) * (1f / COLUMNS);
        float minV = row * (1f / ROWS);
        float maxV = (row + 1) * (1f / ROWS);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, PORTAL_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        matrixStack.push();
        matrixStack.translate(portalPos.x - cameraPos.x, portalPos.y - cameraPos.y, portalPos.z - cameraPos.z);

        Vec3d lookVec = cameraPos.subtract(portalPos).normalize();
        float yaw = (float) Math.atan2(-lookVec.x, -lookVec.z);
        matrixStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotation(yaw));

        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        float halfWidth = PORTAL_WIDTH / 2;
        float halfHeight = PORTAL_HEIGHT / 2;

        bufferBuilder.vertex(matrix, -halfWidth, -halfHeight, 0).texture(minU, maxV);
        bufferBuilder.vertex(matrix, halfWidth, -halfHeight, 0).texture(maxU, maxV);
        bufferBuilder.vertex(matrix, halfWidth, halfHeight, 0).texture(maxU, minV);
        bufferBuilder.vertex(matrix, -halfWidth, halfHeight, 0).texture(minU, minV);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrixStack.pop();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }

    public long getCreationTime() {
        return creationTime;
    }
}