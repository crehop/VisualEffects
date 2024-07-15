package com.example.visualeffects.commands;

import com.example.visualeffects.SnowEffect;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SnowCommand {
    private static final Identifier SNOW_PACKET = new Identifier("visualeffects", "snow");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("snow")
                .then(literal("set")
                        .then(argument("minSize", DoubleArgumentType.doubleArg(0.01, 10000))
                                .then(argument("maxSize", DoubleArgumentType.doubleArg(0.01, 100000))
                                        .then(argument("count", IntegerArgumentType.integer(1, 100000))
                                                .then(argument("radius", DoubleArgumentType.doubleArg(1.0, 1000.0))
                                                        .then(argument("fallSpeed", DoubleArgumentType.doubleArg(0.0, 100.0))
                                                                .then(argument("shimmyStrength", DoubleArgumentType.doubleArg(0.0, 100.0))
                                                                        .then(argument("isSphereShape", BoolArgumentType.bool())
                                                                                .then(argument("affectedByLight", BoolArgumentType.bool())
                                                                                        .executes(context -> executeSet(
                                                                                                context.getSource(),
                                                                                                DoubleArgumentType.getDouble(context, "minSize"),
                                                                                                DoubleArgumentType.getDouble(context, "maxSize"),
                                                                                                IntegerArgumentType.getInteger(context, "count"),
                                                                                                DoubleArgumentType.getDouble(context, "radius"),
                                                                                                DoubleArgumentType.getDouble(context, "fallSpeed"),
                                                                                                DoubleArgumentType.getDouble(context, "shimmyStrength"),
                                                                                                BoolArgumentType.getBool(context, "isSphereShape"),
                                                                                                BoolArgumentType.getBool(context, "affectedByLight")
                                                                                        )))))))))))
                .then(literal("toggle")
                        .executes(context -> executeToggle(context.getSource()))));
    }

    private static int executeSet(ServerCommandSource source, double minSize, double maxSize, int count, double radius,
                                  double fallSpeed, double shimmyStrength, boolean isSphereShape, boolean affectedByLight) {
        SnowEffect.setParameters(minSize, maxSize, count, radius, fallSpeed, shimmyStrength, isSphereShape, affectedByLight);
        sendSnowUpdateToClients(source.getServer());
        source.sendFeedback(() -> Text.literal("Snow parameters updated"), false);
        return 1;
    }

    private static int executeToggle(ServerCommandSource source) {
        SnowEffect.toggle();
        sendSnowUpdateToClients(source.getServer());
        source.sendFeedback(() -> Text.literal("Snow effect toggled: " + (SnowEffect.isActive() ? "ON" : "OFF")), false);
        return 1;
    }

    private static void sendSnowUpdateToClients(MinecraftServer server) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(SnowEffect.isActive());
        buf.writeDouble(SnowEffect.getMinSize());
        buf.writeDouble(SnowEffect.getMaxSize());
        buf.writeInt(SnowEffect.getCount());
        buf.writeDouble(SnowEffect.getRadius());
        buf.writeDouble(SnowEffect.getFallSpeed());
        buf.writeDouble(SnowEffect.getShimmyStrength());
        buf.writeBoolean(SnowEffect.isSphereShape());
        buf.writeBoolean(SnowEffect.isAffectedByLight());

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, SNOW_PACKET, buf);
            System.out.println("SENT UPDATE PACKET TO:" + player.getName());
        }
    }
}


