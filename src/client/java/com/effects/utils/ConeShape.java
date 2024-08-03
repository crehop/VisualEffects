package com.effects.utils;

import com.effects.utils.AnimatedTextureRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ConeShape extends Shape {
    private final float radius;
    private final float height;

    public ConeShape(BlockPos position, Identifier texture, int rows, int columns, int totalFrames, int animationSpeed,
                     float radius, float height) {
        super(position, texture, rows, columns, totalFrames, animationSpeed);
        this.radius = radius;
        this.height = height;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, Camera camera) {
        Vec3d cameraPos = camera.getPos();
        Vec3d shapePos = new Vec3d(position.getX() + 0.5, position.getY() + 2, position.getZ() + 0.5);

        AnimatedTextureRenderer.renderAnimatedCone(
                matrixStack,
                texture.getNamespace(),
                texture.getPath(),
                shapePos,
                cameraPos,
                radius,
                height,
                columns,
                rows,
                totalFrames,
                animationSpeed,
                creationTime
        );
    }
}