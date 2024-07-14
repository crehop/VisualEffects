package com.example.visualeffects;

public class WindEffect {
    private static boolean isActive = false;
    private static double strength = 1.0;
    private static double direction = 0.0; // In radians, 0 is North, PI/2 is East, etc.

    public static void setParameters(double strength, double direction) {
        WindEffect.strength = strength;
        WindEffect.direction = direction;
    }

    public static void toggle() {
        isActive = !isActive;
    }

    // Getters
    public static boolean isActive() { return isActive; }
    public static double getStrength() { return strength; }
    public static double getDirection() { return direction; }
}