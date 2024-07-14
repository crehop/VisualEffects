package com.example;

import com.example.visualeffects.commands.FogCommand;
import com.example.visualeffects.commands.SnowCommand;
import com.example.visualeffects.commands.WindCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
	public static final String MOD_ID = "visualeffects";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Visual Effects Mod");

		// Register commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			SnowCommand.register(dispatcher);
			FogCommand.register(dispatcher);
			WindCommand.register(dispatcher);
		});

		LOGGER.info("Visual Effects Mod initialized");
	}
}