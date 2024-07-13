package com.example;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	public static final String MOD_ID = "visualeffects";
	public static final Identifier HEALTH_UPDATE_PACKET = new Identifier(MOD_ID, "health_update");

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Visual Effects Mod");
		ClientSide.init();
		ServerSide.init();
	}
}