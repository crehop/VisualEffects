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
	private static final Identifier SNOW_PACKET = new Identifier("modid", "snow");
	private static final Identifier FOG_PACKET = new Identifier("modid", "fog");
	private static final Identifier WIND_PACKET = new Identifier("modid", "wind");

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.END.register(context -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world != null && client.player != null) {
				SnowRenderer.render(context.matrixStack(), context.tickDelta(), client.gameRenderer.getCamera().getPos());
				FogRenderer.render(context.matrixStack(), context.tickDelta(), client.gameRenderer.getCamera().getPos());

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
				SnowEffect.setParameters(minSize, maxSize, count, radius, fallSpeed, shimmyStrength, isSphereShape, affectedByLight);
				if (isActive) SnowEffect.toggle();
				System.out.println("Client received snow update");
			});
		});
	}

	private void registerFogPacketReceiver() {
		ClientPlayNetworking.registerGlobalReceiver(FOG_PACKET, (client, handler, buf, responseSender) -> {
			boolean isActive = buf.readBoolean();
			double strength = buf.readDouble();
			double density = buf.readDouble();
			double radius = buf.readDouble();
			boolean isSphereShape = buf.readBoolean();
			double renderDistance = buf.readDouble();
			boolean affectedByLight = buf.readBoolean();
			double swirlingStrength = buf.readDouble();
			double layeringStrength = buf.readDouble();
			boolean affectsSnowVisibility = buf.readBoolean();

			client.execute(() -> {
				FogEffect.setParameters(strength, density, radius, isSphereShape, renderDistance,
						affectedByLight, swirlingStrength, layeringStrength, affectsSnowVisibility);
				if (isActive) FogEffect.toggle();
				System.out.println("Client received fog update");
			});
		});
	}

	private void registerWindPacketReceiver() {
		ClientPlayNetworking.registerGlobalReceiver(WIND_PACKET, (client, handler, buf, responseSender) -> {
			boolean isActive = buf.readBoolean();
			double strength = buf.readDouble();
			double direction = buf.readDouble();

			client.execute(() -> {
				WindEffect.setParameters(strength, direction);
				if (isActive) WindEffect.toggle();
				System.out.println("Client received wind update");
			});
		});
	}
}