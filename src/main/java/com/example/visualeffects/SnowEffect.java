package com.example.visualeffects;

public class SnowEffect {
    private static boolean isActive = false;
    private static double minSize = 0.1;
    private static double maxSize = 0.5;
    private static int count = 1000;
    private static double radius = 32.0;
    private static double spinSpeed = 1.0;
    private static double fallSpeed = 1.0;
    private static double angleVariance = 15.0;
    private static double shimmyStrength = 0.1;
    private static boolean isSphereShape = false;
    private static double renderDistance = 64.0;
    private static boolean affectedByLight = false;
    private static boolean particleEffects = false;

    public static void setParameters(double minSize, double maxSize, int count, double radius, double spinSpeed,
                                     double fallSpeed, double angleVariance, double shimmyStrength, boolean isSphereShape,
                                     double renderDistance, boolean affectedByLight, boolean particleEffects) {
        SnowEffect.minSize = minSize;
        SnowEffect.maxSize = maxSize;
        SnowEffect.count = count;
        SnowEffect.radius = radius;
        SnowEffect.spinSpeed = spinSpeed;
        SnowEffect.fallSpeed = fallSpeed;
        SnowEffect.angleVariance = angleVariance;
        SnowEffect.shimmyStrength = shimmyStrength;
        SnowEffect.isSphereShape = isSphereShape;
        SnowEffect.renderDistance = renderDistance;
        SnowEffect.affectedByLight = affectedByLight;
        SnowEffect.particleEffects = particleEffects;
    }

    public static void toggle() {
        isActive = !isActive;
    }

    // Getters
    public static boolean isActive() { return isActive; }
    public static double getMinSize() { return minSize; }
    public static double getMaxSize() { return maxSize; }
    public static int getCount() { return count; }
    public static double getRadius() { return radius; }
    public static double getSpinSpeed() { return spinSpeed; }
    public static double getFallSpeed() { return fallSpeed; }
    public static double getAngleVariance() { return angleVariance; }
    public static double getShimmyStrength() { return shimmyStrength; }
    public static boolean isSphereShape() { return isSphereShape; }
    public static double getRenderDistance() { return renderDistance; }
    public static boolean isAffectedByLight() { return affectedByLight; }
    public static boolean hasParticleEffects() { return particleEffects; }
}