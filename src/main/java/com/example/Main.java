package com.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Visual Effects Mod");

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				SnowCommand.register(dispatcher));
	}
}