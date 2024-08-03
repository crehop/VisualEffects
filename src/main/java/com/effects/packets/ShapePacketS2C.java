package com.effects.packets;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public class ShapePacketS2C implements CustomPayload {
    public static final CustomPayload.Id<ShapePacketS2C> ID = CustomPayload.id("effects_shape");
    public static final PacketCodec<? super RegistryByteBuf, ShapePacketS2C> CODEC = new Codec();

    private final String shapeType;
    private final String shapeName;
    private final String gifUrl;
    private final BlockPos position;
    private final int animationSpeed;
    private final boolean overwrite;
    private final float width;
    private final float height;
    private final float length;
    private final float rotationSpeedX;
    private final float rotationSpeedY;
    private final float radius;
    private final float baseWidth;
    private final float baseLength;
    private final float majorRadius;
    private final float minorRadius;
    private final boolean faceCamera;

    public ShapePacketS2C(String shapeType, String shapeName, String gifUrl, BlockPos position, int animationSpeed, boolean overwrite,
                          float width, float height, float length, float rotationSpeedX, float rotationSpeedY, float radius,
                          float baseWidth, float baseLength, float majorRadius, float minorRadius, boolean faceCamera) {
        this.shapeType = shapeType;
        this.shapeName = shapeName;
        this.gifUrl = gifUrl;
        this.position = position;
        this.animationSpeed = animationSpeed;
        this.overwrite = overwrite;
        this.width = width;
        this.height = height;
        this.length = length;
        this.rotationSpeedX = rotationSpeedX;
        this.rotationSpeedY = rotationSpeedY;
        this.radius = radius;
        this.baseWidth = baseWidth;
        this.baseLength = baseLength;
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
        this.faceCamera = faceCamera;
    }

    @Override
    public CustomPayload.Id<?> getId() {
        return ID;
    }

    // Getters for all fields
    public String getShapeType() { return shapeType; }
    public String getShapeName() { return shapeName; }
    public String getGifUrl() { return gifUrl; }
    public BlockPos getPosition() { return position; }
    public int getAnimationSpeed() { return animationSpeed; }
    public boolean getOverwrite() { return overwrite; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getLength() { return length; }
    public float getRotationSpeedX() { return rotationSpeedX; }
    public float getRotationSpeedY() { return rotationSpeedY; }
    public float getRadius() { return radius; }
    public float getBaseWidth() { return baseWidth; }
    public float getBaseLength() { return baseLength; }
    public float getMajorRadius() { return majorRadius; }
    public float getMinorRadius() { return minorRadius; }
    public boolean getFaceCamera() { return faceCamera; }

    public void write(PacketByteBuf buf) {
        buf.writeString(shapeType);
        buf.writeString(shapeName);
        buf.writeString(gifUrl);
        buf.writeBlockPos(position);
        buf.writeInt(animationSpeed);
        buf.writeBoolean(overwrite);
        buf.writeFloat(width);
        buf.writeFloat(height);
        buf.writeFloat(length);
        buf.writeFloat(rotationSpeedX);
        buf.writeFloat(rotationSpeedY);
        buf.writeFloat(radius);
        buf.writeFloat(baseWidth);
        buf.writeFloat(baseLength);
        buf.writeFloat(majorRadius);
        buf.writeFloat(minorRadius);
        buf.writeBoolean(faceCamera);
    }

    public static ShapePacketS2C read(PacketByteBuf buf) {
        return new ShapePacketS2C(
                buf.readString(),
                buf.readString(),
                buf.readString(),
                buf.readBlockPos(),
                buf.readInt(),
                buf.readBoolean(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean()
        );
    }

    private static class Codec implements PacketCodec<RegistryByteBuf, ShapePacketS2C> {
        @Override
        public void encode(RegistryByteBuf buf, ShapePacketS2C packet) {
            packet.write(buf);
        }

        @Override
        public ShapePacketS2C decode(RegistryByteBuf buf) {
            return ShapePacketS2C.read(buf);
        }
    }
}