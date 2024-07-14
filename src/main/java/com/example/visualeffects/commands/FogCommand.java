package com.example.visualeffects.commands;

import com.example.visualeffects.FogEffect;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.server.command.CommandManager;

public class FogCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("fog")
                .then(CommandManager.literal("toggle")
                        .executes(context -> executeToggle(context.getSource())))
                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("strength", DoubleArgumentType.doubleArg(0.0, 1000.0))
                                .then(CommandManager.argument("density", DoubleArgumentType.doubleArg(0.0, 1000.0))
                                        .then(CommandManager.argument("radius", DoubleArgumentType.doubleArg(1.0, 1000.0))
                                                .then(CommandManager.argument("isSphereShape", BoolArgumentType.bool())
                                                        .then(CommandManager.argument("renderDistance", DoubleArgumentType.doubleArg(1.0, 2000.0))
                                                                .then(CommandManager.argument("affectedByLight", BoolArgumentType.bool())
                                                                        .then(CommandManager.argument("swirlingStrength", DoubleArgumentType.doubleArg(0.0, 1000.0))
                                                                                .then(CommandManager.argument("layeringStrength", DoubleArgumentType.doubleArg(0.0, 1000.0))
                                                                                        .then(CommandManager.argument("affectsSnowVisibility", BoolArgumentType.bool())
                                                                                                .executes(context -> executeSet(context.getSource(),
                                                                                                        DoubleArgumentType.getDouble(context, "strength"),
                                                                                                        DoubleArgumentType.getDouble(context, "density"),
                                                                                                        DoubleArgumentType.getDouble(context, "radius"),
                                                                                                        BoolArgumentType.getBool(context, "isSphereShape"),
                                                                                                        DoubleArgumentType.getDouble(context, "renderDistance"),
                                                                                                        BoolArgumentType.getBool(context, "affectedByLight"),
                                                                                                        DoubleArgumentType.getDouble(context, "swirlingStrength"),
                                                                                                        DoubleArgumentType.getDouble(context, "layeringStrength"),
                                                                                                        BoolArgumentType.getBool(context, "affectsSnowVisibility")
                                                                                                )))))))))))));
    }

    private static int executeToggle(ServerCommandSource source) {
        FogEffect.toggle();
        source.sendFeedback(() -> Text.literal("Fog effect toggled: " + (FogEffect.isActive() ? "ON" : "OFF")), false);
        return 1;
    }

    private static int executeSet(ServerCommandSource source, double strength, double density, double radius,
                                  boolean isSphereShape, double renderDistance, boolean affectedByLight,
                                  double swirlingStrength, double layeringStrength, boolean affectsSnowVisibility) {
        FogEffect.setParameters(strength, density, radius, isSphereShape, renderDistance, affectedByLight,
                swirlingStrength, layeringStrength, affectsSnowVisibility);
        source.sendFeedback(() -> Text.literal("Fog parameters updated"), false);
        return 1;
    }
}
