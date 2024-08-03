package com.effects;

import com.effects.packets.ShapePacketS2C;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("effects-mod");
	private static final String MOD_ID = "effects";
	public static final Identifier SHAPE_PACKET_ID = Identifier.of("effects", "shape");

	@Override
	public void onInitialize() {
		LOGGER.info("Effects Mod Initialized!");
		registerCommands();
		PayloadTypeRegistry.playS2C().register(ShapePacketS2C.ID, ShapePacketS2C.CODEC);
	}

	private void registerCommands() {
		CommandRegistrationCallback.EVENT.register(this::registerShapeCommand);
	}

	private void registerShapeCommand(CommandDispatcher<ServerCommandSource> dispatcher,
									  CommandRegistryAccess registryAccess,
									  CommandManager.RegistrationEnvironment environment) {
		dispatcher.register(literal("shape")
				.then(literal("cube")
						.then(argument("name", StringArgumentType.word())
								.then(argument("width", FloatArgumentType.floatArg(0.1f, 1000.0f))
										.then(argument("height", FloatArgumentType.floatArg(0.1f, 1000.0f))
												.then(argument("length", FloatArgumentType.floatArg(0.1f, 1000.0f))
														.then(argument("rotationSpeedX", FloatArgumentType.floatArg(0.0f, 360.0f))
																.then(argument("rotationSpeedY", FloatArgumentType.floatArg(0.0f, 360.0f))
																		.then(argument("gifUrl", StringArgumentType.greedyString())
																				.executes(this::executeShapeCommand)
																				.then(literal("overwrite")
																						.executes(this::executeShapeCommandWithOverwrite))))))))))
				.then(literal("sphere")
						.then(argument("name", StringArgumentType.word())
								.then(argument("radius", FloatArgumentType.floatArg(0.1f, 1000.0f))
										.then(argument("gifUrl", StringArgumentType.greedyString())
												.executes(this::executeShapeCommand)
												.then(literal("overwrite")
														.executes(this::executeShapeCommandWithOverwrite))))))
				.then(literal("pyramid")
						.then(argument("name", StringArgumentType.word())
								.then(argument("baseWidth", FloatArgumentType.floatArg(0.1f, 1000.0f))
										.then(argument("baseLength", FloatArgumentType.floatArg(0.1f, 1000.0f))
												.then(argument("height", FloatArgumentType.floatArg(0.1f, 1000.0f))
														.then(argument("gifUrl", StringArgumentType.greedyString())
																.executes(this::executeShapeCommand)
																.then(literal("overwrite")
																		.executes(this::executeShapeCommandWithOverwrite))))))))
				.then(literal("cone")
						.then(argument("name", StringArgumentType.word())
								.then(argument("radius", FloatArgumentType.floatArg(0.1f, 1000.0f))
										.then(argument("height", FloatArgumentType.floatArg(0.1f, 1000.0f))
												.then(argument("gifUrl", StringArgumentType.greedyString())
														.executes(this::executeShapeCommand)
														.then(literal("overwrite")
																.executes(this::executeShapeCommandWithOverwrite)))))))
				.then(literal("torus")
						.then(argument("name", StringArgumentType.word())
								.then(argument("majorRadius", FloatArgumentType.floatArg(0.1f, 1000.0f))
										.then(argument("minorRadius", FloatArgumentType.floatArg(0.1f, 1000.0f))
												.then(argument("gifUrl", StringArgumentType.greedyString())
														.executes(this::executeShapeCommand)
														.then(literal("overwrite")
																.executes(this::executeShapeCommandWithOverwrite)))))))
				.then(literal("ellipse")
						.then(argument("name", StringArgumentType.word())
								.then(argument("width", FloatArgumentType.floatArg(0.1f, 1000.0f))
										.then(argument("height", FloatArgumentType.floatArg(0.1f, 1000.0f))
												.then(argument("gifUrl", StringArgumentType.greedyString())
														.executes(this::executeShapeCommand)
														.then(literal("overwrite")
																.executes(this::executeShapeCommandWithOverwrite)))))))
				.then(literal("plane")
						.then(argument("name", StringArgumentType.word())
								.then(argument("width", FloatArgumentType.floatArg(0.1f, 1000.0f))
										.then(argument("height", FloatArgumentType.floatArg(0.1f, 1000.0f))
												.then(argument("faceCamera", IntegerArgumentType.integer(0, 1))
														.then(argument("gifUrl", StringArgumentType.greedyString())
																.executes(this::executeShapeCommand)
																.then(literal("overwrite")
																		.executes(this::executeShapeCommandWithOverwrite)))))))));
	}

	private int executeShapeCommand(CommandContext<ServerCommandSource> context) {
		return executeShapeCommandInternal(context, false);
	}

	private int executeShapeCommandWithOverwrite(CommandContext<ServerCommandSource> context) {
		return executeShapeCommandInternal(context, true);
	}

	private int executeShapeCommandInternal(CommandContext<ServerCommandSource> context, boolean overwrite) {
		ServerCommandSource source = context.getSource();
		ServerPlayerEntity player = source.getPlayer();

		if (player != null) {
			String shapeType = context.getNodes().get(1).getNode().getName();
			String gifUrl = StringArgumentType.getString(context, "gifUrl");
			String baseName = StringArgumentType.getString(context, "name").toLowerCase();
			BlockPos playerPos = player.getBlockPos();
			int animationSpeed = 10; // Default value

			float width = 0f, height = 0f, length = 0f, rotationSpeedX = 0f, rotationSpeedY = 0f, radius = 0f;
			float baseWidth = 0f, baseLength = 0f, majorRadius = 0f, minorRadius = 0f;
			boolean faceCamera = false;

			// Set shape-specific parameters
			switch (shapeType) {
				case "cube":
					width = FloatArgumentType.getFloat(context, "width");
					height = FloatArgumentType.getFloat(context, "height");
					length = FloatArgumentType.getFloat(context, "length");
					rotationSpeedX = FloatArgumentType.getFloat(context, "rotationSpeedX");
					rotationSpeedY = FloatArgumentType.getFloat(context, "rotationSpeedY");
					break;
				case "sphere":
					radius = FloatArgumentType.getFloat(context, "radius");
					break;
				case "pyramid":
					baseWidth = FloatArgumentType.getFloat(context, "baseWidth");
					baseLength = FloatArgumentType.getFloat(context, "baseLength");
					height = FloatArgumentType.getFloat(context, "height");
					break;
				case "cone":
					radius = FloatArgumentType.getFloat(context, "radius");
					height = FloatArgumentType.getFloat(context, "height");
					break;
				case "torus":
					majorRadius = FloatArgumentType.getFloat(context, "majorRadius");
					minorRadius = FloatArgumentType.getFloat(context, "minorRadius");
					break;
				case "ellipse":
				case "plane":
					width = FloatArgumentType.getFloat(context, "width");
					height = FloatArgumentType.getFloat(context, "height");
					if (shapeType.equals("plane")) {
						faceCamera = IntegerArgumentType.getInteger(context, "faceCamera") == 1;
					}
					break;
			}

			ShapePacketS2C packet = new ShapePacketS2C(shapeType, baseName, gifUrl, playerPos, animationSpeed, overwrite,
					width, height, length, rotationSpeedX, rotationSpeedY, radius, baseWidth, baseLength, majorRadius, minorRadius, faceCamera);
			ServerPlayNetworking.send(player, packet);

			source.sendFeedback(() -> Text.literal("Creating " + shapeType + " shape with base name " + baseName +
					(overwrite ? " (overwriting if exists)" : "") + "..."), false);
			return 1;
		} else {
			source.sendError(Text.literal("This command can only be executed by a player."));
			return 0;
		}
	}
}