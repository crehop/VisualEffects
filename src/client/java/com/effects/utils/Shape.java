package com.effects.utils;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public abstract class Shape {
    protected final BlockPos position;
    protected final Identifier texture;
    protected final int rows;
    protected final int columns;
    protected final int totalFrames;
    protected final int animationSpeed;
    protected final long creationTime;

    public Shape(BlockPos position, Identifier texture, int rows, int columns, int totalFrames, int animationSpeed) {
        this.position = position;
        this.texture = texture;
        this.rows = rows;
        this.columns = columns;
        this.totalFrames = totalFrames;
        this.animationSpeed = animationSpeed;
        this.creationTime = System.currentTimeMillis();
    }

    public abstract void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, Camera camera);

    public long getCreationTime() {
        return creationTime;
    }
}