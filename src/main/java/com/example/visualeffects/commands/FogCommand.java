package com.example.visualeffects.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import com.example.visualeffects.FogEffect;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FogCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("fog")
                .then(literal("set")
                        .then(argument("strength", DoubleArgumentType.doubleArg(0.0, 1.0))
                                .then(argument("density", DoubleArgumentType.doubleArg(0.0, 1.0))
                                        .then(argument("radius", DoubleArgumentType.doubleArg(1.0, 100.0))
                                                .then(argument("isSphereShape", BoolArgumentType.bool())
                                                        .then(argument("renderDistance", DoubleArgumentType.doubleArg(1.0, 256.0))
                                                                .then(argument("affectedByLight", BoolArgumentType.bool())
                                                                        .then(argument("swirlingStrength", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                                                .then(argument("layeringStrength", DoubleArgumentType.doubleArg(0.0, 1.0))
                                                                                        .then(argument("affectsSnowVisibility", BoolArgumentType.bool())
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
                                                                                                )))))))))))
                        .then(literal("toggle")
                                .executes(context -> executeToggle(context.getSource())))));
    }

    private static int executeSet(ServerCommandSource source, double strength, double density, double radius,
                                  boolean isSphereShape, double renderDistance, boolean affectedByLight,
                                  double swirlingStrength, double layeringStrength, boolean affectsSnowVisibility) {
        FogEffect.setParameters(strength, density, radius, isSphereShape, renderDistance, affectedByLight,
                swirlingStrength, layeringStrength, affectsSnowVisibility);
        source.sendFeedback(() -> Text.literal("Fog parameters updated"), false);
        return 1;
    }

    private static int executeToggle(ServerCommandSource source) {
        FogEffect.toggle();
        source.sendFeedback(() -> Text.literal("Fog effect toggled: " + (FogEffect.isActive() ? "ON" : "OFF")), false);
        return 1;
    }
}
