package com.effects;

import com.effects.packets.PortalPacketS2C;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.literal;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("template-mod");
	private static final String MOD_ID = "effects";

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		PayloadTypeRegistry.playS2C().register(PortalPacketS2C.ID, PortalPacketS2C.CODEC);

		registerCommands();
	}

	private void registerCommands() {
		CommandRegistrationCallback.EVENT.register(this::registerPortalCommand);
	}

	private void registerPortalCommand(CommandDispatcher<ServerCommandSource> dispatcher,
									   CommandRegistryAccess registryAccess,
									   CommandManager.RegistrationEnvironment environment) {
		dispatcher.register(literal("portal")
				.then(literal("open")
						.executes(this::executePortalOpen)
				)
		);
	}

	private int executePortalOpen(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		ServerPlayerEntity player = source.getPlayer();

		if (player != null) {
			String portalId = "portal_" + player.getUuid().toString();
			BlockPos playerPos = player.getBlockPos();
			int animationSpeed = 10; // Default value, you can make this configurable

			PortalPacketS2C packet = new PortalPacketS2C(portalId, playerPos, animationSpeed);
			ServerPlayNetworking.send(player, packet);

			source.sendFeedback(() -> Text.literal("Portal opened!"), false);
			return 1;
		} else {
			source.sendError(Text.literal("This command can only be executed by a player."));
			return 0;
		}
	}
}