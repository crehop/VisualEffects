package com.example;

public class SnowEffect {
    private static boolean isSnowing = false;
    private static float snowflakeSize = 0.1f;
    private static int snowflakeCount = 1000;
    private static float snowRadius = 16f;
    private static float spinSpeed = 0f;
    private static float fallSpeed = 0.1f;
    private static float sizeVariation = 0.5f;
    private static float driftSpeed = 0.05f;

    public static boolean toggleSnow(float size, int count, float radius, float spin, float fall, float sizeVar, float drift) {
        isSnowing = !isSnowing;
        setParameters(size, count, radius, spin, fall, sizeVar, drift);
        return isSnowing;
    }

    public static void setSnowing(boolean snowing, float size, int count, float radius, float spin, float fall, float sizeVar, float drift) {
        isSnowing = snowing;
        setParameters(size, count, radius, spin, fall, sizeVar, drift);
    }

    private static void setParameters(float size, int count, float radius, float spin, float fall, float sizeVar, float drift) {
        snowflakeSize = Math.max(0.01f, Math.min(size, 100f));
        snowflakeCount = Math.max(1, Math.min(count, 1000000));
        snowRadius = Math.max(1f, Math.min(radius, 100f));
        spinSpeed = Math.max(0f, Math.min(spin, 10f));
        fallSpeed = Math.max(0.01f, Math.min(fall, 1f));
        sizeVariation = Math.max(0f, Math.min(sizeVar, 1f));
        driftSpeed = Math.max(0f, Math.min(drift, 1f));
    }

    public static void reset() {
        isSnowing = false;
        snowflakeSize = 0.1f;
        snowflakeCount = 1000;
        snowRadius = 16f;
        spinSpeed = 0f;
        fallSpeed = 0.1f;
        sizeVariation = 0.5f;
        driftSpeed = 0.05f;
    }

    // Existing getters
    public static boolean isSnowing() { return isSnowing; }
    public static float getSnowflakeSize() { return snowflakeSize; }
    public static int getSnowflakeCount() { return snowflakeCount; }
    public static float getSnowRadius() { return snowRadius; }
    public static float getSpinSpeed() { return spinSpeed; }
    public static float getFallSpeed() { return fallSpeed; }

    // New getters
    public static float getSizeVariation() { return sizeVariation; }
    public static float getDriftSpeed() { return driftSpeed; }
}