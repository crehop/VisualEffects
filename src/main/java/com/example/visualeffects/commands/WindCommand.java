package com.example.visualeffects.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import com.example.visualeffects.WindEffect;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WindCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("wind")
                .then(literal("set")
                        .then(argument("strength", DoubleArgumentType.doubleArg(0.0, 10.0))
                                .then(argument("direction", DoubleArgumentType.doubleArg(0.0, 360.0))
                                        .executes(context -> executeSet(context.getSource(),
                                                DoubleArgumentType.getDouble(context, "strength"),
                                                DoubleArgumentType.getDouble(context, "direction")
                                        )))))
                .then(literal("toggle")
                        .executes(context -> executeToggle(context.getSource()))));
    }

    private static int executeSet(ServerCommandSource source, double strength, double direction) {
        // Convert direction from degrees to radians
        double directionRadians = Math.toRadians(direction);
        WindEffect.setParameters(strength, directionRadians);
        source.sendFeedback(() -> Text.literal("Wind parameters updated"), false);
        return 1;
    }

    private static int executeToggle(ServerCommandSource source) {
        WindEffect.toggle();
        source.sendFeedback(() -> Text.literal("Wind effect toggled: " + (WindEffect.isActive() ? "ON" : "OFF")), false);
        return 1;
    }
}