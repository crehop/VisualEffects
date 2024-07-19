package com.example;

public class SnowEffect {
    private static boolean isSnowing = false;
    private static float snowflakeSize = 0.1f;
    private static int snowflakeCount = 1000;
    private static float snowRadius = 16f;
    private static int spinSpeed = 1;
    private static float fallSpeed = 0.1f;
    private static float fallAngle = 0f;
    private static boolean hasChanged = false;

    public static boolean toggleSnow() {
        isSnowing = !isSnowing;
        hasChanged = true;
        return isSnowing;
    }

    public static void setSnowflakeSize(float size) {
        snowflakeSize = size;
        hasChanged = true;
    }

    public static void setSnowflakeCount(int count) {
        snowflakeCount = count;
        hasChanged = true;
    }

    public static void setSnowRadius(float radius) {
        snowRadius = radius;
        hasChanged = true;
    }

    public static void setSpinSpeed(int speed) {
        spinSpeed = speed;
        hasChanged = true;
    }

    public static void setFallSpeed(float speed) {
        fallSpeed = speed;
        hasChanged = true;
    }

    public static void setFallAngle(float angle) {
        fallAngle = angle;
        hasChanged = true;
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

    public static float getFallSpeed() {
        return fallSpeed;
    }

    public static float getFallAngle() {
        return fallAngle;
    }

    public static boolean hasChanged() {
        if (hasChanged) {
            hasChanged = false;
            return true;
        }
        return false;
    }
    public static void setSnowing(boolean snowing) {
        isSnowing = snowing;
        hasChanged = true;
    }
    public static String getState() {
        return String.format("Snow: %s, Size: %.2f, Count: %d, Radius: %.2f, Spin: %d, Fall Speed: %.2f, Angle: %.2f",
                isSnowing, snowflakeSize, snowflakeCount, snowRadius, spinSpeed, fallSpeed, fallAngle);
    }
}