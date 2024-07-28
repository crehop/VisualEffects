package com.effects;

import com.effects.packets.PortalPacketS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.text.Text;

public class ClientMain implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		registerClientPacketReceivers();

	}

	private void registerClientPacketReceivers() {
		ClientPlayNetworking.registerGlobalReceiver(PortalPacketS2C.ID, (payload,context) -> {
			// Read the packet data
			// Handle the packet on the main client thread
			context.client().execute(() -> {
				handlePortalOpen(payload.getPortalId());
			});
		});
	}

	private void handlePortalOpen(String portalId) {
		// This method is called when a portal is opened
		// You can implement your portal opening logic here
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			client.player.sendMessage(Text.literal("A portal with ID " + portalId + " has been opened!"), false);
		}

		// Add your custom portal opening logic here
		// For example:
		// - Display a special effect
		// - Play a sound
		// - Change the player's game state
		// - Open a custom GUI
		// etc.
	}
}