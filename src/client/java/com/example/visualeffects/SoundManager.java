package com.example.visualeffects;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.registry.Registries;

public class SoundManager {
    private static final Identifier SNOW_SOUND_ID = new Identifier("minecraft", "weather.rain");
    private static final Identifier WIND_SOUND_ID = new Identifier("minecraft", "weather.rain");

    public static void playSnowSound(Vec3d pos) {
        if (SnowEffect.isActive()) {
            SoundEvent snowSound = Registries.SOUND_EVENT.get(SNOW_SOUND_ID);
            if (snowSound != null) {
                MinecraftClient.getInstance().world.playSound(
                        pos.x, pos.y, pos.z,
                        snowSound,
                        SoundCategory.WEATHER,
                        0.2f,
                        1.0f,
                        false
                );
            }
        }
    }

    public static void playWindSound(Vec3d pos) {
        if (WindEffect.isActive()) {
            SoundEvent windSound = Registries.SOUND_EVENT.get(WIND_SOUND_ID);
            if (windSound != null) {
                float pitch = (float) (1.0 + WindEffect.getStrength() * 0.1);
                MinecraftClient.getInstance().world.playSound(
                        pos.x, pos.y, pos.z,
                        windSound,
                        SoundCategory.WEATHER,
                        (float) WindEffect.getStrength() * 0.2f,
                        pitch,
                        false
                );
            }
        }
    }
}
