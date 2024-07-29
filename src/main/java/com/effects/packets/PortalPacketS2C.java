package com.effects.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class PortalPacketS2C implements CustomPayload {
    private final String portalId;
    private final BlockPos position;
    private final int animationSpeed;
    private static final String MOD_ID = "portalpacket";

    public static final CustomPayload.Id<PortalPacketS2C> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "portal_open"));

    public static final PacketCodec<RegistryByteBuf, PortalPacketS2C> CODEC = PacketCodec.of(PortalPacketS2C::write, PortalPacketS2C::create);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public PortalPacketS2C(String portalId, BlockPos position, int animationSpeed) {
        this.portalId = portalId;
        this.position = position;
        this.animationSpeed = animationSpeed;
    }

    private static PortalPacketS2C create(RegistryByteBuf buf) {
        return new PortalPacketS2C(buf.readString(), buf.readBlockPos(), buf.readInt());
    }

    private void write(RegistryByteBuf buf) {
        buf.writeString(portalId);
        buf.writeBlockPos(position);
        buf.writeInt(animationSpeed);
    }

    public String getPortalId() {
        return portalId;
    }

    public BlockPos getPosition() {
        return position;
    }

    public int getAnimationSpeed() {
        return animationSpeed;
    }
}