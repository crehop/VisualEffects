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
                        .executes(context -> executeToggle(context.getSource(), 0.1f, 1000, 16f, 0.1f, 0.1f, 0.5f, 0.05f)))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("size", FloatArgumentType.floatArg(0.01f, 100f))
                                .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 1000000))
                                        .then(CommandManager.argument("radius", FloatArgumentType.floatArg(1f, 100f))
                                                .then(CommandManager.argument("spinSpeed", FloatArgumentType.floatArg(0f, 10f))
                                                        .then(CommandManager.argument("fallSpeed", FloatArgumentType.floatArg(0.01f, 1f))
                                                                .then(CommandManager.argument("sizeVariation", FloatArgumentType.floatArg(0f, 1f))
                                                                        .then(CommandManager.argument("driftSpeed", FloatArgumentType.floatArg(0f, 1f))
                                                                                .executes(context -> executeToggle(
                                                                                        context.getSource(),
                                                                                        FloatArgumentType.getFloat(context, "size"),
                                                                                        IntegerArgumentType.getInteger(context, "count"),
                                                                                        FloatArgumentType.getFloat(context, "radius"),
                                                                                        FloatArgumentType.getFloat(context, "spinSpeed"),
                                                                                        FloatArgumentType.getFloat(context, "fallSpeed"),
                                                                                        FloatArgumentType.getFloat(context, "sizeVariation"),
                                                                                        FloatArgumentType.getFloat(context, "driftSpeed")
                                                                                ))
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int executeToggle(ServerCommandSource source, float size, int count, float radius, float spinSpeed, float fallSpeed, float sizeVariation, float driftSpeed) {
        boolean isSnowing = SnowEffect.toggleSnow(size, count, radius, spinSpeed, fallSpeed, sizeVariation, driftSpeed);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(isSnowing);
        buf.writeFloat(size);
        buf.writeInt(count);
        buf.writeFloat(radius);
        buf.writeFloat(spinSpeed);
        buf.writeFloat(fallSpeed);
        buf.writeFloat(sizeVariation);
        buf.writeFloat(driftSpeed);
        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, SNOW_TOGGLE_PACKET, buf);
        }

        source.sendFeedback(() -> Text.literal("Toggled snow effect (Size: " + size + ", Count: " + count + ", Radius: " + radius +
                ", Spin Speed: " + spinSpeed + ", Fall Speed: " + fallSpeed + ", Size Variation: " + sizeVariation + ", Drift Speed: " + driftSpeed + ")"), false);
        return 1;
    }
}