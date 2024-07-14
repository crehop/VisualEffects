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
    private static final Identifier SNOW_TOGGLE_PACKET = new Identifier("modid", "snow_toggle");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("snow")
                .then(CommandManager.literal("toggle")
                        .executes(context -> executeToggle(context.getSource(), 0.1f, 1000, 16f, 0)))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("size", FloatArgumentType.floatArg(0.01f, 100f))
                                .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 1000000))
                                        .then(CommandManager.argument("radius", FloatArgumentType.floatArg(1f, 100f))
                                                .then(CommandManager.argument("spinSpeed", IntegerArgumentType.integer(0, 1000))
                                                        .executes(context -> executeToggle(
                                                                context.getSource(),
                                                                FloatArgumentType.getFloat(context, "size"),
                                                                IntegerArgumentType.getInteger(context, "count"),
                                                                FloatArgumentType.getFloat(context, "radius"),
                                                                IntegerArgumentType.getInteger(context, "spinSpeed")
                                                        ))))))));
    }

    private static int executeToggle(ServerCommandSource source, float size, int count, float radius, int spinSpeed) {
        boolean isSnowing = SnowEffect.toggleSnow(size, count, radius, spinSpeed);
        System.out.println("Server: Snow effect toggled: " + isSnowing + " (Size: " + size + ", Count: " + count + ", Radius: " + radius + ", Spin Speed: " + spinSpeed + ")");

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(isSnowing);
        buf.writeFloat(size);
        buf.writeInt(count);
        buf.writeFloat(radius);
        buf.writeInt(spinSpeed);
        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, SNOW_TOGGLE_PACKET, buf);
        }

        source.sendFeedback(() -> Text.literal("Toggled snow effect (Size: " + size + ", Count: " + count + ", Radius: " + radius + ", Spin Speed: " + spinSpeed + ")"), false);
        return 1;
    }
}