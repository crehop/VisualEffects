package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
			int spinSpeed = buf.readInt();
			client.execute(() -> {
				SnowEffect.setSnowing(isSnowing, size, count, radius, spinSpeed);
				System.out.println("Client received snow toggle: " + isSnowing + " (Size: " + size + ", Count: " + count + ", Radius: " + radius + ", Spin Speed: " + spinSpeed + ")");
			});
		});
	}
}