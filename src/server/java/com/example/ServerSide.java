package com.example;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;

public class ServerSide {
    private static final Map<String, Float> lastHealthMap = new HashMap<>();

    public static void init() {
        registerServerEvents();
    }

    private static void registerServerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                updateAndSendHealthInfo(player);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            updateAndSendHealthInfo(handler.player);
        });
    }

    private static void updateAndSendHealthInfo(ServerPlayerEntity player) {
        String uuid = player.getUuidAsString();
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float lastHealth = lastHealthMap.getOrDefault(uuid, health);
        lastHealthMap.put(uuid, health);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(player.getUuid());
        buf.writeFloat(health);
        buf.writeFloat(maxHealth);
        buf.writeFloat(lastHealth);

        for (ServerPlayerEntity otherPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            if (otherPlayer != player && otherPlayer.squaredDistanceTo(player) <= 64 * 64) { // 64 blocks range
                ServerPlayNetworking.send(otherPlayer, Main.HEALTH_UPDATE_PACKET, buf);
            }
        }
    }
}
