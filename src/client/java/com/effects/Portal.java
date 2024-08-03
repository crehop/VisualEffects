package com.effects;

import com.effects.utils.AnimatedTextureRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.random.Random;

public class Portal {
    private static final String TEXTURE_NAMESPACE = "effects";
    private static final String TEXTURE_PATH = "textures/portal/portal_animation.png";
    private static final float PORTAL_WIDTH = 14f;
    private static final float PORTAL_HEIGHT = 10f;
    private static final int COLUMNS = 5;
    private static final int ROWS = 12;
    private static final int TOTAL_FRAMES = 60;

    private static final int PORTAL_CENTER_EFFECT_COUNT = 5;
    private static final float PORTAL_CENTER_EFFECT_RADIUS = 0.5f;
    private static final int PORTAL_EMISSIVE_PARTICLES_RADIUS = 3;
    private static final float PORTAL_EMISSIVE_PARTICLES_COUNT = 4f;

    private final BlockPos position;
    private long creationTime;
    private final int animationSpeed;

    public enum ParticleDistribution {
        UNIFORM,
        GAUSSIAN
    }

    public Portal(BlockPos position, int animationSpeed) {
        this.position = position;
        this.creationTime = System.currentTimeMillis();
        this.animationSpeed = animationSpeed;
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, Camera camera) {
        Vec3d cameraPos = camera.getPos();
        Vec3d portalPos = new Vec3d(position.getX() + 0.5, position.getY() + 2, position.getZ() + 0.5);

//        AnimatedTextureRenderer.renderAnimatedElipse(
//                matrixStack,
//                TEXTURE_NAMESPACE,
//                TEXTURE_PATH,
//                portalPos,
//                cameraPos,
//                2,
//                4,
//                60,
//                COLUMNS,
//                ROWS,
//                TOTAL_FRAMES,
//                animationSpeed,
//                creationTime,
//                true
//        );
        //RENDER TORUS
//        AnimatedTextureRenderer.renderAnimatedTorus(
//                matrixStack,
//                TEXTURE_NAMESPACE,
//                TEXTURE_PATH,
//                portalPos,
//                cameraPos,
//                PORTAL_WIDTH,
//                PORTAL_WIDTH / 2,
//                COLUMNS,
//                ROWS,
//                TOTAL_FRAMES,
//                animationSpeed,
//                creationTime
//        );
//        //RENDER CONE
//        AnimatedTextureRenderer.renderAnimatedCone(
//                matrixStack,
//                TEXTURE_NAMESPACE,
//                TEXTURE_PATH,
//                portalPos,
//                cameraPos,
//                PORTAL_WIDTH,
//                PORTAL_HEIGHT,
//                COLUMNS,
//                ROWS,
//                TOTAL_FRAMES,
//                animationSpeed,
//                creationTime
//        );

        //RENDER PYRAMID
//        AnimatedTextureRenderer.renderAnimatedPyramid(
//                matrixStack,
//                TEXTURE_NAMESPACE,
//                TEXTURE_PATH,
//                portalPos,
//                cameraPos,
//                PORTAL_WIDTH,
//                PORTAL_WIDTH,
//                PORTAL_HEIGHT,
//                COLUMNS,
//                ROWS,
//                TOTAL_FRAMES,
//                animationSpeed,
//                creationTime
//        );

        //  Render SPHERE
//        AnimatedTextureRenderer.renderAnimatedSphere(
//                matrixStack,
//                TEXTURE_NAMESPACE,
//                TEXTURE_PATH,
//                portalPos,
//                cameraPos,
//                PORTAL_WIDTH,
//                COLUMNS,
//                ROWS,
//                TOTAL_FRAMES,
//                animationSpeed,
//                creationTime
//        );

        //         RENDER CUBE
        AnimatedTextureRenderer.renderAnimatedCube(
                matrixStack,
                TEXTURE_NAMESPACE,
                TEXTURE_PATH,
                portalPos,
                cameraPos,
                1,
                1,
                1,
                COLUMNS,
                ROWS,
                TOTAL_FRAMES,
                animationSpeed,
                creationTime,
                true,
                true,
                30f,  // Total rotation around X-axis over the entire animation
                10f // Total rotation around Y-axis over the entire animation
        );

        //RENDER PLANE
//        AnimatedTextureRenderer.renderAnimatedPlane(
//                matrixStack,
//                TEXTURE_NAMESPACE,
//                TEXTURE_PATH,
//                portalPos,
//                cameraPos,
//                PORTAL_WIDTH,
//                PORTAL_HEIGHT,
//                COLUMNS,
//                ROWS,
//                TOTAL_FRAMES,
//                animationSpeed,
//                creationTime
//        );

        // Spawn particles
    }

    private void spawnParticles(ParticleDistribution distribution) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        Vec3d center = new Vec3d(position.getX() + 0.5, position.getY() + 2, position.getZ() + 0.5);
        Random random = client.world.random;

        // Spawn squid ink particles
        for (int i = 0; i < PORTAL_CENTER_EFFECT_COUNT; i++) {
            double offsetX = getOffset(distribution, PORTAL_CENTER_EFFECT_RADIUS, random);
            double offsetY = getOffset(distribution, PORTAL_CENTER_EFFECT_RADIUS, random);
            double offsetZ = getOffset(distribution, PORTAL_CENTER_EFFECT_RADIUS, random);
            client.world.addParticle(ParticleTypes.BUBBLE_POP,
                    center.x + offsetX,
                    center.y + offsetY,
                    center.z + offsetZ,
                    0, 0, 0);
        }

        // Spawn witch spell particles in a sphere around the portal
        for (int i = 0; i < PORTAL_EMISSIVE_PARTICLES_RADIUS; i++) {
            double offsetX = getOffset(distribution, PORTAL_EMISSIVE_PARTICLES_COUNT, random);
            double offsetY = getOffset(distribution, PORTAL_EMISSIVE_PARTICLES_COUNT, random);
            double offsetZ = getOffset(distribution, PORTAL_EMISSIVE_PARTICLES_COUNT, random);
            client.world.addParticle(ParticleTypes.PORTAL,
                    center.x + offsetX,
                    center.y + offsetY,
                    center.z + offsetZ,
                    0, 0, 0);
        }
    }

    private double getOffset(ParticleDistribution distribution, float radius, Random random) {
        switch (distribution) {
            case UNIFORM:
                return (random.nextDouble() - 0.5) * 2 * radius;
            case GAUSSIAN:
                return random.nextGaussian() * radius / 3; // 99.7% of values will be within 3 standard deviations
            default:
                return 0;
        }
    }

    public long getCreationTime() {
        return creationTime;
    }
}