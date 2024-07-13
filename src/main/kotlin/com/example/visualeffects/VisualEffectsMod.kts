package com.example.visualeffects

import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.level.ServerPlayer
import net.minecraft.resources.ResourceLocation
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.network.FriendlyByteBuf

class VisualEffectsServerMod : DedicatedServerModInitializer {

    private val LODESTONE_HEALTH_UPDATE_PACKET = ResourceLocation("lodestone", "custom_health_update")
    private val lastHealthMap = mutableMapOf<String, Float>()

    override fun onInitializeServer() {
        println("Initializing Visual Effects Server Mod")
        registerEvents()
    }

    private fun registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register { server ->
            server.playerList.players.forEach { player ->
                updateAndSendHealthInfo(player)
            }
        }

        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            updateAndSendHealthInfo(handler.player)
        }
    }

    private fun updateAndSendHealthInfo(player: ServerPlayer) {
        val health = player.health
        val maxHealth = player.maxHealth
        val lastHealth = lastHealthMap[player.stringUUID] ?: health
        lastHealthMap[player.stringUUID] = health

        val buf = PacketByteBufs.create()
        buf.writeUtf("health_bar") // Identifier for Lodestone to recognize this custom packet
        buf.writeUUID(player.uuid)
        buf.writeFloat(health)
        buf.writeFloat(maxHealth)
        buf.writeFloat(lastHealth)

        player.server.playerList.players.forEach { otherPlayer ->
            if (otherPlayer != player && otherPlayer.distanceToSqr(player) <= 64 * 64) { // 64 blocks range
                ServerPlayNetworking.send(otherPlayer, LODESTONE_HEALTH_UPDATE_PACKET, buf)
            }
        }
    }

    companion object {
        const val MOD_ID = "visualeffects"
    }
}