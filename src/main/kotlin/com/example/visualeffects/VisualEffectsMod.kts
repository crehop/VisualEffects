package com.example.visualeffects

import net.fabricmc.api.ModInitializer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import java.util.UUID

class VisualEffectsMod : ModInitializer, ClientModInitializer, DedicatedServerModInitializer {

    private val HEALTH_UPDATE_PACKET = ResourceLocation(MOD_ID, "health_update")
    private val lastHealthMap = mutableMapOf<String, Float>()

    override fun onInitialize() {
        println("Initializing Visual Effects Mod")
    }
    override fun onInitializeClient() {
        println("Initializing Visual Effects Client")
        registerClientPacketReceiver()
    }
    override fun onInitializeServer() {
        println("Initializing Visual Effects Server")
        registerServerEvents()
    }

    private fun registerServerEvents() {
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
        val uuid = player.uuid.toString()
        val health = player.health
        val maxHealth = player.maxHealth
        val lastHealth = lastHealthMap[uuid] ?: health
        lastHealthMap[uuid] = health

        val buf = PacketByteBufs.create()
        buf.writeUUID(player.uuid)
        buf.writeFloat(health)
        buf.writeFloat(maxHealth)
        buf.writeFloat(lastHealth)

        player.server.playerList.players.forEach{ otherPlayer ->
            if (otherPlayer != player && otherPlayer.distanceToSqr(player) <= 64 * 64) { // 64 blocks range
                ServerPlayNetworking.send(otherPlayer, HEALTH_UPDATE_PACKET, buf)
            }
        }
    }

    private fun registerClientPacketReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(HEALTH_UPDATE_PACKET) { client, handler, buf, responseSender ->
            val playerUuid = buf.readUUID()
            val health = buf.readFloat()
            val maxHealth = buf.readFloat()
            val lastHealth = buf.readFloat()

            client.execute {
                updatePlayerHealthDisplay(playerUuid, health, maxHealth, lastHealth)
            }
        }
    }

    private fun updatePlayerHealthDisplay(playerUuid: UUID, health: Float, maxHealth: Float, lastHealth: Float) {
        // Implement your client-side rendering logic here
        // For example, you could create a custom renderer for health bars above players' heads
        val healthChange = health - lastHealth
        println("Updating health display for player $playerUuid: $health / $maxHealth (change: $healthChange)")
    }

    companion object {
        const val MOD_ID = "visualeffects"
    }
}