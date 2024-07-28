package com.effects.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
public class PortalPacketS2C implements CustomPayload {

    public String getPortalId() {
        return portalId;
    }

    private final String portalId;
    private static final String MOD_ID = "portalpacket";

    public static final CustomPayload.Id<PortalPacketS2C> ID = new CustomPayload.Id<>(Identifier.of(MOD_ID, "item_spells"));

    public static final PacketCodec<RegistryByteBuf, PortalPacketS2C> CODEC = PacketCodec.of(PortalPacketS2C::write, PortalPacketS2C::create);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public PortalPacketS2C(String portalId) {
        this.portalId = portalId;
    }

    private static PortalPacketS2C create(RegistryByteBuf buf) {
        return new PortalPacketS2C(buf.readString());
    }

    private void write(RegistryByteBuf buf) {
        buf.writeString(portalId);
    }

}
