package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import com.example.render.SnowRenderer;

public class ClientMain implements ClientModInitializer {
	private static final Identifier SNOW_UPDATE_PACKET = new Identifier("modid", "snow_update");

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.END.register(context -> {
			SnowRenderer.render(context.matrixStack(), context.tickDelta());
		});

		ClientPlayNetworking.registerGlobalReceiver(SNOW_UPDATE_PACKET, (client, handler, buf, responseSender) -> {
			boolean isSnowing = buf.readBoolean();
			float size = buf.readFloat();
			int count = buf.readInt();
			float radius = buf.readFloat();
			float spinSpeed = buf.readFloat();
			float fallSpeed = buf.readFloat();
			float fallAngle = buf.readFloat();

			client.execute(() -> {
				SnowEffect.setSnowing(isSnowing);
				SnowEffect.setSnowflakeSize(size);
				SnowEffect.setSnowflakeCount(count);
				SnowEffect.setSnowRadius(radius);
				SnowEffect.setSpinSpeed(spinSpeed);
				SnowEffect.setFallSpeed(fallSpeed);
				SnowEffect.setFallAngle(fallAngle);

				System.out.println("Client received snow update: " + SnowEffect.getState());
			});
		});
	}
}