package com.example.visualeffects;

public class SnowEffect {
    private static boolean isActive = false;
    private static double minSize = 0.1;
    private static double maxSize = 1.0;
    private static int count = 1000;
    private static double radius = 50.0;
    private static double fallSpeed = 0.1;
    private static double shimmyStrength = 0.1;
    private static boolean isSphereShape = true;
    private static boolean affectedByLight = false;

    public static void setParameters(double minSize, double maxSize, int count, double radius,
                                     double fallSpeed, double shimmyStrength, boolean isSphereShape, boolean affectedByLight) {
        SnowEffect.minSize = minSize;
        SnowEffect.maxSize = maxSize;
        SnowEffect.count = count;
        SnowEffect.radius = radius;
        SnowEffect.fallSpeed = fallSpeed;
        SnowEffect.shimmyStrength = shimmyStrength;
        SnowEffect.isSphereShape = isSphereShape;
        SnowEffect.affectedByLight = affectedByLight;
        System.out.println("Snow parameters updated");
    }

    public static void toggle() {
        isActive = !isActive;
        System.out.println("Snow effect toggled: " + (isActive ? "ON" : "OFF"));
    }

    // Getters
    public static boolean isActive() { return isActive; }
    public static double getMinSize() { return minSize; }
    public static double getMaxSize() { return maxSize; }
    public static int getCount() { return count; }
    public static double getRadius() { return radius; }
    public static double getFallSpeed() { return fallSpeed; }
    public static double getShimmyStrength() { return shimmyStrength; }
    public static boolean isSphereShape() { return isSphereShape; }
    public static boolean isAffectedByLight() { return affectedByLight; }
}
