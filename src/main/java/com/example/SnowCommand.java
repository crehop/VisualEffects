package com.example;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SnowCommand {
    private static final Identifier SNOW_UPDATE_PACKET = new Identifier("modid", "snow_update");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("snow")
                .then(CommandManager.literal("toggle")
                        .executes(context -> executeToggle(context.getSource())))
                .then(CommandManager.literal("size")
                        .then(CommandManager.argument("size", FloatArgumentType.floatArg(0.01f, 100f))
                                .executes(context -> executeSetSize(context.getSource(), FloatArgumentType.getFloat(context, "size")))))
                .then(CommandManager.literal("count")
                        .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 1000000))
                                .executes(context -> executeSetCount(context.getSource(), IntegerArgumentType.getInteger(context, "count")))))
                .then(CommandManager.literal("radius")
                        .then(CommandManager.argument("radius", FloatArgumentType.floatArg(1f, 100f))
                                .executes(context -> executeSetRadius(context.getSource(), FloatArgumentType.getFloat(context, "radius")))))
                .then(CommandManager.literal("spin")
                        .then(CommandManager.argument("speed", FloatArgumentType.floatArg(0f, 10f))
                                .executes(context -> executeSetSpin(context.getSource(), FloatArgumentType.getFloat(context, "speed")))))
                .then(CommandManager.literal("fallspeed")
                        .then(CommandManager.argument("speed", FloatArgumentType.floatArg(0f, 10f))
                                .executes(context -> executeSetFallSpeed(context.getSource(), FloatArgumentType.getFloat(context, "speed")))))
                .then(CommandManager.literal("angle")
                        .then(CommandManager.argument("angle", FloatArgumentType.floatArg(-90f, 90f))
                                .executes(context -> executeSetAngle(context.getSource(), FloatArgumentType.getFloat(context, "angle"))))));
    }

    private static int executeToggle(ServerCommandSource source) {
        boolean isSnowing = SnowEffect.toggleSnow();
        sendUpdatePacket(source);
        source.sendFeedback(() -> Text.literal(SnowEffect.getState()), true);
        return 1;
    }

    private static int executeSetSize(ServerCommandSource source, float size) {
        SnowEffect.setSnowflakeSize(size);
        sendUpdatePacket(source);
        source.sendFeedback(() -> Text.literal(SnowEffect.getState()), true);
        return 1;
    }

    private static int executeSetCount(ServerCommandSource source, int count) {
        SnowEffect.setSnowflakeCount(count);
        sendUpdatePacket(source);
        source.sendFeedback(() -> Text.literal(SnowEffect.getState()), true);
        return 1;
    }

    private static int executeSetRadius(ServerCommandSource source, float radius) {
        SnowEffect.setSnowRadius(radius);
        sendUpdatePacket(source);
        source.sendFeedback(() -> Text.literal(SnowEffect.getState()), true);
        return 1;
    }

    private static int executeSetSpin(ServerCommandSource source, float speed) {
        SnowEffect.setSpinSpeed(speed);
        sendUpdatePacket(source);
        source.sendFeedback(() -> Text.literal(SnowEffect.getState()), true);
        return 1;
    }

    private static int executeSetFallSpeed(ServerCommandSource source, float speed) {
        SnowEffect.setFallSpeed(speed);
        sendUpdatePacket(source);
        source.sendFeedback(() -> Text.literal(SnowEffect.getState()), true);
        return 1;
    }

    private static int executeSetAngle(ServerCommandSource source, float angle) {
        SnowEffect.setFallAngle(angle);
        sendUpdatePacket(source);
        source.sendFeedback(() -> Text.literal(SnowEffect.getState()), true);
        return 1;
    }

    private static void sendUpdatePacket(ServerCommandSource source) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(SnowEffect.isSnowing());
        buf.writeFloat(SnowEffect.getSnowflakeSize());
        buf.writeInt(SnowEffect.getSnowflakeCount());
        buf.writeFloat(SnowEffect.getSnowRadius());
        buf.writeFloat(SnowEffect.getSpinSpeed());
        buf.writeFloat(SnowEffect.getFallSpeed());
        buf.writeFloat(SnowEffect.getFallAngle());

        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, SNOW_UPDATE_PACKET, buf);
        }
    }
}