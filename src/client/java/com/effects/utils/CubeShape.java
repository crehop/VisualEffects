package com.effects.utils;

import com.effects.utils.AnimatedTextureRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CubeShape extends Shape {
    private final float width;
    private final float height;
    private final float length;
    private final float rotationSpeedX;
    private final float rotationSpeedY;

    public CubeShape(BlockPos position, Identifier texture, int rows, int columns, int totalFrames, int animationSpeed,
                     float width, float height, float length, float rotationSpeedX, float rotationSpeedY) {
        super(position, texture, rows, columns, totalFrames, animationSpeed);
        this.width = width;
        this.height = height;
        this.length = length;
        this.rotationSpeedX = rotationSpeedX;
        this.rotationSpeedY = rotationSpeedY;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, Camera camera) {
        Vec3d cameraPos = camera.getPos();
        Vec3d shapePos = new Vec3d(position.getX() + 0.5, position.getY() + 2, position.getZ() + 0.5);

        AnimatedTextureRenderer.renderAnimatedCube(
                matrixStack,
                texture.getNamespace(),
                texture.getPath(),
                shapePos,
                cameraPos,
                width,
                height,
                length,
                columns,
                rows,
                totalFrames,
                animationSpeed,
                creationTime,
                true,
                true,
                rotationSpeedX,
                rotationSpeedY
        );
    }
}