package com.effects.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TextureLoader {

    public static Identifier loadTexture(String texturePath) {
        MinecraftClient client = MinecraftClient.getInstance();
        File file = new File("resources/assets/effects/textures/shape/", texturePath);

        if (!file.exists()) {
            System.out.println("Texture file not found: " + file.getAbsolutePath());
            return null;
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            NativeImage image = NativeImage.read(inputStream);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(image);

            Identifier textureId = Identifier.of("effects", "textures/shape/" + texturePath);

            client.execute(() -> {
                client.getTextureManager().registerTexture(textureId, texture);
            });

            return textureId;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}