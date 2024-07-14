package com.example.visualeffects;

public class FogEffect {
    private static boolean isActive = false;
    private static double strength = 0.5;
    private static double density = 0.5;
    private static double radius = 32.0;
    private static boolean isSphereShape = true;
    private static double renderDistance = 64.0;
    private static boolean affectedByLight = false;
    private static double swirlingStrength = 0.5;
    private static double layeringStrength = 0.5;
    private static boolean affectsSnowVisibility = false;

    // Additional parameters for fog particle sizes and move speed
    private static double minSize = 0.1;
    private static double maxSize = 1.0;
    private static double moveSpeed = 0.1;

    public static void setParameters(double strength, double density, double radius, boolean isSphereShape,
                                     double renderDistance, boolean affectedByLight, double swirlingStrength,
                                     double layeringStrength, boolean affectsSnowVisibility) {
        FogEffect.strength = strength;
        FogEffect.density = density;
        FogEffect.radius = radius;
        FogEffect.isSphereShape = isSphereShape;
        FogEffect.renderDistance = renderDistance;
        FogEffect.affectedByLight = affectedByLight;
        FogEffect.swirlingStrength = swirlingStrength;
        FogEffect.layeringStrength = layeringStrength;
        FogEffect.affectsSnowVisibility = affectsSnowVisibility;
        System.out.println("Fog parameters updated");
    }

    public static void toggle() {
        isActive = !isActive;
        System.out.println("Fog effect toggled: " + (isActive ? "ON" : "OFF"));
    }

    // Getters
    public static boolean isActive() { return isActive; }
    public static double getStrength() { return strength; }
    public static double getDensity() { return density; }
    public static double getRadius() { return radius; }
    public static boolean isSphereShape() { return isSphereShape; }
    public static double getRenderDistance() { return renderDistance; }
    public static boolean isAffectedByLight() { return affectedByLight; }
    public static double getSwirlStrength() { return swirlingStrength; }
    public static double getLayeringStrength() { return layeringStrength; }
    public static boolean affectsSnowVisibility() { return affectsSnowVisibility; }
    public static double getMinSize() { return minSize; }
    public static double getMaxSize() { return maxSize; }
    public static double getMoveSpeed() { return moveSpeed; }

    public static int getCount() {
        // Example calculation for the number of fog particles
        return (int) (radius * density * 10);
    }

}
