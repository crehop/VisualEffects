package com.example;

public class SnowEffect {
    private static boolean isSnowing = false;
    private static float snowflakeSize = 0.1f;
    private static int snowflakeCount = 1000;
    private static float snowRadius = 16f;
    private static int spinSpeed = 0;

    public static boolean toggleSnow(float size, int count, float radius, int spin) {
        isSnowing = !isSnowing;
        snowflakeSize = size;
        snowflakeCount = count;
        snowRadius = radius;
        spinSpeed = spin;
        System.out.println("Snow effect toggled: " + isSnowing + " (Size: " + size + ", Count: " + count + ", Radius: " + radius + ", Spin Speed: " + spin + ")");
        return isSnowing;
    }

    public static void setSnowing(boolean snowing, float size, int count, float radius, int spin) {
        isSnowing = snowing;
        snowflakeSize = size;
        snowflakeCount = count;
        snowRadius = radius;
        spinSpeed = spin;
        System.out.println("Snow effect set to: " + isSnowing + " (Size: " + size + ", Count: " + count + ", Radius: " + radius + ", Spin Speed: " + spin + ")");
    }

    public static boolean isSnowing() {
        return isSnowing;
    }

    public static float getSnowflakeSize() {
        return snowflakeSize;
    }

    public static int getSnowflakeCount() {
        return snowflakeCount;
    }

    public static float getSnowRadius() {
        return snowRadius;
    }

    public static int getSpinSpeed() {
        return spinSpeed;
    }
}