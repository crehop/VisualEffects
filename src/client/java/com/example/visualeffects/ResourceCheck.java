package com.example.visualeffects;

import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourceCheck implements SimpleResourceReloadListener<Void> {

    private static final Identifier SNOW_TEXTURE = new Identifier("visualeffects", "textures/environment/snow.png");

    @Override
    public Identifier getFabricId() {
        return new Identifier("visualeffects", "resource_check");
    }

    @Override
    public CompletableFuture<Void> load(ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            try {
                resourceManager.getResource(SNOW_TEXTURE);
                System.out.println("Snow texture loaded successfully.");
            } catch (Exception e) {
                System.out.println("Failed to load snow texture: " + e.getMessage());
            }
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Void data, ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return CompletableFuture.completedFuture(null);
    }
}
