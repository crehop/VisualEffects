package com.example.visualeffects.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import com.example.visualeffects.SnowEffect;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SnowCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("snow")
                        .then(literal("set")
                                .then(argument("minSize", DoubleArgumentType.doubleArg(0.01, 10000))
                                        .then(argument("maxSize", DoubleArgumentType.doubleArg(0.01, 100000))
                                                .then(argument("count", IntegerArgumentType.integer(1, 100000))
                                                        .then(argument("radius", DoubleArgumentType.doubleArg(1.0, 1000.0))
                                                                .then(argument("spinSpeed", DoubleArgumentType.doubleArg(0.0, 10.0))
                                                                        .then(argument("fallSpeed", DoubleArgumentType.doubleArg(0.0, 100.0))
                                                                                .then(argument("angleVariance", DoubleArgumentType.doubleArg(0.0, 180.0))
                                                                                        .then(argument("shimmyStrength", DoubleArgumentType.doubleArg(0.0, 100.0))
                                                                                                .then(argument("isSphereShape", BoolArgumentType.bool())
                                                                                                        .then(argument("renderDistance", DoubleArgumentType.doubleArg(1.0, 1000.0))
                                                                                                                .then(argument("affectedByLight", BoolArgumentType.bool())
                                                                                                                        .then(argument("particleEffects", BoolArgumentType.bool())
                                                                                                                                .executes(context -> executeSet(context.getSource(),
                                                                                                                                        DoubleArgumentType.getDouble(context, "minSize"),
                                                                                                                                        DoubleArgumentType.getDouble(context, "maxSize"),
                                                                                                                                        IntegerArgumentType.getInteger(context, "count"),
                                                                                                                                        DoubleArgumentType.getDouble(context, "radius"),
                                                                                                                                        DoubleArgumentType.getDouble(context, "spinSpeed"),
                                                                                                                                        DoubleArgumentType.getDouble(context, "fallSpeed"),
                                                                                                                                        DoubleArgumentType.getDouble(context, "angleVariance"),
                                                                                                                                        DoubleArgumentType.getDouble(context, "shimmyStrength"),
                                                                                                                                        BoolArgumentType.getBool(context, "isSphereShape"),
                                                                                                                                        DoubleArgumentType.getDouble(context, "renderDistance"),
                                                                                                                                        BoolArgumentType.getBool(context, "affectedByLight"),
                                                                                                                                        BoolArgumentType.getBool(context, "particleEffects")
                                                                                                                                ))))))))))))))).then(literal("toggle")
                        .executes(context -> executeToggle(context.getSource()))));
    }

    private static int executeSet(ServerCommandSource source, double minSize, double maxSize, int count, double radius,
                                  double spinSpeed, double fallSpeed, double angleVariance, double shimmyStrength,
                                  boolean isSphereShape, double renderDistance, boolean affectedByLight,
                                  boolean particleEffects) {
        SnowEffect.setParameters(minSize, maxSize, count, radius, spinSpeed, fallSpeed, angleVariance, shimmyStrength,
                isSphereShape, renderDistance, affectedByLight, particleEffects);
        source.sendFeedback(() -> Text.literal("Snow parameters updated"), false);
        return 1;
    }

    private static int executeToggle(ServerCommandSource source) {
        SnowEffect.toggle();
        source.sendFeedback(() -> Text.literal("Snow effect toggled: " + (SnowEffect.isActive() ? "ON" : "OFF")), false);
        return 1;
    }
}