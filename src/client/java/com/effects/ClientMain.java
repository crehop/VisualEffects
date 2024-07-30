package com.effects;

import com.effects.packets.PortalPacketS2C;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientMain implements ClientModInitializer {
	private static final List<Portal> activePortals = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		registerClientPacketReceivers();
		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) return;

            MatrixStack matrixStack = context.matrixStack();
			VertexConsumerProvider vertexConsumers = context.consumers();
			Camera camera = context.camera();

			if (matrixStack != null && vertexConsumers != null) {
				activePortals.removeIf(portal -> System.currentTimeMillis() - portal.getCreationTime() > 45000);
				for (Portal portal : activePortals) {
					portal.render(matrixStack, vertexConsumers, camera);
				}
			}
		});

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			client.player.sendMessage(Text.literal("Portal textures loaded successfully!"), false);
		}
	}

	private void registerClientPacketReceivers() {
		ClientPlayNetworking.registerGlobalReceiver(PortalPacketS2C.ID, (payload, context) -> {
			context.client().execute(() -> {
				handlePortalOpen(payload.getPortalId(), payload.getPosition(), payload.getAnimationSpeed());
			});
		});
	}

	private void handlePortalOpen(String portalId, BlockPos position, int animationSpeed) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			client.player.sendMessage(Text.literal("A portal with ID " + portalId + " has been opened!"), false);
			activePortals.add(new Portal(position, animationSpeed));
		}
	}
}
