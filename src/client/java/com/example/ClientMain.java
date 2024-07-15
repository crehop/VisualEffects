package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import com.example.visualeffects.renderers.SnowRenderer;
import com.example.visualeffects.renderers.FogRenderer;
import com.example.visualeffects.SnowEffect;
import com.example.visualeffects.FogEffect;
import com.example.visualeffects.WindEffect;
import com.example.visualeffects.SoundManager;
import net.minecraft.client.MinecraftClient;

public class ClientMain implements ClientModInitializer {
	private static final Identifier SNOW_PACKET = new Identifier("visualeffects", "snow");
	private static final Identifier FOG_PACKET = new Identifier("visualeffects", "fog");
	private static final Identifier WIND_PACKET = new Identifier("visualeffects", "wind");

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.END.register(context -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world != null && client.player != null) {
				SnowRenderer.render(context.matrixStack(), context.tickDelta());
				FogRenderer.render(context.matrixStack(), context.tickDelta());

				// Play sounds
				SoundManager.playSnowSound(client.player.getPos());
				SoundManager.playWindSound(client.player.getPos());
			}
		});

		registerSnowPacketReceiver();
		registerFogPacketReceiver();
		registerWindPacketReceiver();
	}

	private void registerSnowPacketReceiver() {
		ClientPlayNetworking.registerGlobalReceiver(SNOW_PACKET, (client, handler, buf, responseSender) -> {
			boolean isActive = buf.readBoolean();
			double minSize = buf.readDouble();
			double maxSize = buf.readDouble();
			int count = buf.readInt();
			double radius = buf.readDouble();
			double fallSpeed = buf.readDouble();
			double shimmyStrength = buf.readDouble();
			boolean isSphereShape = buf.readBoolean();
			boolean affectedByLight = buf.readBoolean();

			client.execute(() -> {
				SnowEffect.setActive(isActive);
				SnowEffect.setParameters(minSize, maxSize, count, radius, fallSpeed, shimmyStrength, isSphereShape, affectedByLight);
				System.out.println("Client received snow update: Active=" + isActive + ", Count=" + count + ", SphereShape=" + isSphereShape);
			});
		});
	}

	private void registerFogPacketReceiver() {
		// ... Fog packet handling ...
	}

	private void registerWindPacketReceiver() {
		// ... Wind packet handling ...
	}
}
