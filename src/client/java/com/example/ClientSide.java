package com.example;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class ClientSide {
    public static void init() {
        registerClientPacketReceiver();
    }

    private static void registerClientPacketReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(Main.HEALTH_UPDATE_PACKET, (client, handler, buf, responseSender) -> {
            UUID playerUuid = buf.readUuid();
            float health = buf.readFloat();
            float maxHealth = buf.readFloat();
            float lastHealth = buf.readFloat();

            client.execute(() -> {
                updatePlayerHealthDisplay(playerUuid, health, maxHealth, lastHealth);
            });
        });
    }

    private static void updatePlayerHealthDisplay(UUID playerUuid, float health, float maxHealth, float lastHealth) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.world.getPlayerByUuid(playerUuid);
        if (player != null) {
            // Implement your client-side rendering logic here
            // For example, you could create a custom renderer for health bars above players' heads
            float healthChange = health - lastHealth;
            System.out.println("Updating health display for player " + playerUuid + ": " + health + " / " + maxHealth + " (change: " + healthChange + ")");
            // TODO: Add actual rendering code here
        }
    }
}