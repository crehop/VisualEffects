package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.util.Identifier;
import com.example.render.SnowRenderer;

public class ClientMain implements ClientModInitializer {
	private static final Identifier SNOW_TOGGLE_PACKET = new Identifier("modid", "snow_toggle");

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.END.register(context -> {
			SnowRenderer.render(context.matrixStack(), context.tickDelta());
		});

		ClientPlayNetworking.registerGlobalReceiver(SNOW_TOGGLE_PACKET, (client, handler, buf, responseSender) -> {
			boolean isSnowing = buf.readBoolean();
			float size = buf.readFloat();
			int count = buf.readInt();
			float radius = buf.readFloat();
			float spinSpeed = buf.readFloat();
			float fallSpeed = buf.readFloat();
			float sizeVariation = buf.readFloat();
			float driftSpeed = buf.readFloat();
			client.execute(() -> {
				SnowEffect.setSnowing(isSnowing, size, count, radius, spinSpeed, fallSpeed, sizeVariation, driftSpeed);
			});
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			SnowEffect.reset();
			SnowRenderer.reset();
		});
	}
}