package com.effects;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;


public class AnimatedTextureRenderer {

    public static void renderAnimatedPlane(
            MatrixStack matrixStack,
            String namespace,
            String texturePath,
            Vec3d position,
            Vec3d cameraPos,
            float width,
            float height,
            int columns,
            int rows,
            int totalFrames,
            int animationSpeed,
            long creationTime
    ) {
        Identifier texture = Identifier.of(namespace, texturePath);
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - creationTime;
        int frame = (int) ((elapsedTime / (1000f / animationSpeed)) % totalFrames) + 1;

        int col = (frame - 1) % columns;
        int row = (frame - 1) / columns;

        float frameWidth = 1f / columns;
        float frameHeight = 1f / rows;

        float minU = col * frameWidth;
        float maxU = (col + 1) * frameWidth;
        float minV = row * frameHeight;
        float maxV = (row + 1) * frameHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        matrixStack.push();
        matrixStack.translate(position.x - cameraPos.x, position.y - cameraPos.y, position.z - cameraPos.z);

        Vec3d lookVec = cameraPos.subtract(position).normalize();
        float yaw = (float) Math.atan2(-lookVec.x, -lookVec.z);
        matrixStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotation(yaw));

        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        float halfWidth = width / 2;
        float halfHeight = height / 2;

        bufferBuilder.vertex(matrix, -halfWidth, -halfHeight, 0).texture(minU, maxV);
        bufferBuilder.vertex(matrix, halfWidth, -halfHeight, 0).texture(maxU, maxV);
        bufferBuilder.vertex(matrix, halfWidth, halfHeight, 0).texture(maxU, minV);
        bufferBuilder.vertex(matrix, -halfWidth, halfHeight, 0).texture(minU, minV);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrixStack.pop();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }


    public static void renderAnimatedCube(
            MatrixStack matrixStack,
            String namespace,
            String texturePath,
            Vec3d center,
            Vec3d cameraPos,
            float width,
            float height,
            float length,
            int columns,
            int rows,
            int totalFrames,
            int animationSpeed,
            long creationTime,
            boolean animateTop,
            boolean animateBottom,
            float rotationSpeedX,
            float rotationSpeedY) {
        Identifier texture = Identifier.of(namespace, texturePath);
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - creationTime;
        int frame = (int) ((elapsedTime / (1000f / animationSpeed)) % totalFrames) + 1;

        int col = (frame - 1) % columns;
        int row = (frame - 1) / columns;

        float frameWidth = 1f / columns;
        float frameHeight = 1f / rows;

        float minU = col * frameWidth;
        float maxU = (col + 1) * frameWidth;
        float minV = row * frameHeight;
        float maxV = (row + 1) * frameHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        matrixStack.push();
        matrixStack.translate(center.x - cameraPos.x, center.y - cameraPos.y, center.z - cameraPos.z);

        // Apply rotations
        float rotationX = (float) Math.toRadians((elapsedTime / 1000f) * rotationSpeedX);
        float rotationY = (float) Math.toRadians((elapsedTime / 1000f) * rotationSpeedY);

        Quaternionf quaternionY = new Quaternionf().rotationY(rotationY);
        Quaternionf quaternionX = new Quaternionf().rotationX(rotationX);

        matrixStack.multiply(quaternionY);
        matrixStack.multiply(quaternionX);

        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        float halfWidth = width / 2;
        float halfHeight = height / 2;
        float halfLength = length / 2;

        // Front face
        bufferBuilder.vertex(matrix, -halfWidth, -halfHeight, halfLength).texture(minU, maxV);
        bufferBuilder.vertex(matrix, halfWidth, -halfHeight, halfLength).texture(maxU, maxV);
        bufferBuilder.vertex(matrix, halfWidth, halfHeight, halfLength).texture(maxU, minV);
        bufferBuilder.vertex(matrix, -halfWidth, halfHeight, halfLength).texture(minU, minV);

        // Back face
        bufferBuilder.vertex(matrix, halfWidth, -halfHeight, -halfLength).texture(minU, maxV);
        bufferBuilder.vertex(matrix, -halfWidth, -halfHeight, -halfLength).texture(maxU, maxV);
        bufferBuilder.vertex(matrix, -halfWidth, halfHeight, -halfLength).texture(maxU, minV);
        bufferBuilder.vertex(matrix, halfWidth, halfHeight, -halfLength).texture(minU, minV);

        // Left face
        bufferBuilder.vertex(matrix, -halfWidth, -halfHeight, -halfLength).texture(minU, maxV);
        bufferBuilder.vertex(matrix, -halfWidth, -halfHeight, halfLength).texture(maxU, maxV);
        bufferBuilder.vertex(matrix, -halfWidth, halfHeight, halfLength).texture(maxU, minV);
        bufferBuilder.vertex(matrix, -halfWidth, halfHeight, -halfLength).texture(minU, minV);

        // Right face
        bufferBuilder.vertex(matrix, halfWidth, -halfHeight, halfLength).texture(minU, maxV);
        bufferBuilder.vertex(matrix, halfWidth, -halfHeight, -halfLength).texture(maxU, maxV);
        bufferBuilder.vertex(matrix, halfWidth, halfHeight, -halfLength).texture(maxU, minV);
        bufferBuilder.vertex(matrix, halfWidth, halfHeight, halfLength).texture(minU, minV);

        // Top face
        if (animateTop) {
            bufferBuilder.vertex(matrix, -halfWidth, halfHeight, halfLength).texture(minU, maxV);
            bufferBuilder.vertex(matrix, halfWidth, halfHeight, halfLength).texture(maxU, maxV);
            bufferBuilder.vertex(matrix, halfWidth, halfHeight, -halfLength).texture(maxU, minV);
            bufferBuilder.vertex(matrix, -halfWidth, halfHeight, -halfLength).texture(minU, minV);
        }

        // Bottom face
        if (animateBottom) {
            bufferBuilder.vertex(matrix, -halfWidth, -halfHeight, -halfLength).texture(minU, maxV);
            bufferBuilder.vertex(matrix, halfWidth, -halfHeight, -halfLength).texture(maxU, maxV);
            bufferBuilder.vertex(matrix, halfWidth, -halfHeight, halfLength).texture(maxU, minV);
            bufferBuilder.vertex(matrix, -halfWidth, -halfHeight, halfLength).texture(minU, minV);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrixStack.pop();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }
    public static void renderAnimatedSphere(
            MatrixStack matrixStack,
            String namespace,
            String texturePath,
            Vec3d center,
            Vec3d cameraPos,
            float radius,

            int columns,
            int rows,
            int totalFrames,
            int animationSpeed,
            long creationTime
    ) {
        int longitudeSegments = 32;
        int latitudeSegments= 16;
        Identifier texture = Identifier.of(namespace, texturePath);
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - creationTime;
        int frame = (int) ((elapsedTime / (1000f / animationSpeed)) % totalFrames) + 1;

        int col = (frame - 1) % columns;
        int row = (frame - 1) / columns;

        float frameWidth = 1f / columns;
        float frameHeight = 1f / rows;

        float minU = col * frameWidth;
        float maxU = (col + 1) * frameWidth;
        float minV = row * frameHeight;
        float maxV = (row + 1) * frameHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        matrixStack.push();
        matrixStack.translate(center.x - cameraPos.x, center.y - cameraPos.y, center.z - cameraPos.z);

        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        for (int lat = 0; lat < latitudeSegments; lat++) {
            float theta1 = (float) (lat * Math.PI / latitudeSegments);
            float theta2 = (float) ((lat + 1) * Math.PI / latitudeSegments);

            for (int lon = 0; lon < longitudeSegments; lon++) {
                float phi1 = (float) (lon * 2 * Math.PI / longitudeSegments);
                float phi2 = (float) ((lon + 1) * 2 * Math.PI / longitudeSegments);

                float x1 = (float) (radius * Math.sin(theta1) * Math.cos(phi1));
                float y1 = (float) (radius * Math.cos(theta1));
                float z1 = (float) (radius * Math.sin(theta1) * Math.sin(phi1));

                float x2 = (float) (radius * Math.sin(theta1) * Math.cos(phi2));
                float y2 = (float) (radius * Math.cos(theta1));
                float z2 = (float) (radius * Math.sin(theta1) * Math.sin(phi2));

                float x3 = (float) (radius * Math.sin(theta2) * Math.cos(phi2));
                float y3 = (float) (radius * Math.cos(theta2));
                float z3 = (float) (radius * Math.sin(theta2) * Math.sin(phi2));

                float x4 = (float) (radius * Math.sin(theta2) * Math.cos(phi1));
                float y4 = (float) (radius * Math.cos(theta2));
                float z4 = (float) (radius * Math.sin(theta2) * Math.sin(phi1));

                float u1 = minU + (maxU - minU) * (float) lon / longitudeSegments;
                float u2 = minU + (maxU - minU) * (float) (lon + 1) / longitudeSegments;
                float v1 = minV + (maxV - minV) * (float) lat / latitudeSegments;
                float v2 = minV + (maxV - minV) * (float) (lat + 1) / latitudeSegments;

                bufferBuilder.vertex(matrix, x1, y1, z1).texture(u1, v1);
                bufferBuilder.vertex(matrix, x2, y2, z2).texture(u2, v1);
                bufferBuilder.vertex(matrix, x3, y3, z3).texture(u2, v2);
                bufferBuilder.vertex(matrix, x4, y4, z4).texture(u1, v2);
            }
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrixStack.pop();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }
    public static void renderAnimatedPyramid(
            MatrixStack matrixStack,
            String namespace,
            String texturePath,
            Vec3d baseCenter,
            Vec3d cameraPos,
            float length,
            float width,
            float height,
            int columns,
            int rows,
            int totalFrames,
            int animationSpeed,
            long creationTime
    ) {
        Identifier texture = Identifier.of(namespace, texturePath);
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - creationTime;
        int frame = (int) ((elapsedTime / (1000f / animationSpeed)) % totalFrames) + 1;

        int col = (frame - 1) % columns;
        int row = (frame - 1) / columns;

        float frameWidth = 1f / columns;
        float frameHeight = 1f / rows;

        float minU = col * frameWidth;
        float maxU = (col + 1) * frameWidth;
        float minV = row * frameHeight;
        float maxV = (row + 1) * frameHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        matrixStack.push();
        matrixStack.translate(baseCenter.x - cameraPos.x, baseCenter.y - cameraPos.y, baseCenter.z - cameraPos.z);

        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        float halfLength = length / 2;
        float halfWidth = width / 2;

        // Base of the pyramid
        bufferBuilder.vertex(matrix, -halfLength, 0, -halfWidth).texture(minU, maxV);
        bufferBuilder.vertex(matrix, halfLength, 0, -halfWidth).texture(maxU, maxV);
        bufferBuilder.vertex(matrix, halfLength, 0, halfWidth).texture(maxU, minV);
        bufferBuilder.vertex(matrix, -halfLength, 0, halfWidth).texture(minU, minV);

        // Front face
        bufferBuilder.vertex(matrix, -halfLength, 0, halfWidth).texture(minU, maxV);
        bufferBuilder.vertex(matrix, halfLength, 0, halfWidth).texture(maxU, maxV);
        bufferBuilder.vertex(matrix, 0, height, 0).texture((minU + maxU) / 2, minV);
        bufferBuilder.vertex(matrix, 0, height, 0).texture((minU + maxU) / 2, minV);

        // Back face
        bufferBuilder.vertex(matrix, halfLength, 0, -halfWidth).texture(minU, maxV);
        bufferBuilder.vertex(matrix, -halfLength, 0, -halfWidth).texture(maxU, maxV);
        bufferBuilder.vertex(matrix, 0, height, 0).texture((minU + maxU) / 2, minV);
        bufferBuilder.vertex(matrix, 0, height, 0).texture((minU + maxU) / 2, minV);

        // Left face
        bufferBuilder.vertex(matrix, -halfLength, 0, -halfWidth).texture(minU, maxV);
        bufferBuilder.vertex(matrix, -halfLength, 0, halfWidth).texture(maxU, maxV);
        bufferBuilder.vertex(matrix, 0, height, 0).texture((minU + maxU) / 2, minV);
        bufferBuilder.vertex(matrix, 0, height, 0).texture((minU + maxU) / 2, minV);

        // Right face
        bufferBuilder.vertex(matrix, halfLength, 0, halfWidth).texture(minU, maxV);
        bufferBuilder.vertex(matrix, halfLength, 0, -halfWidth).texture(maxU, maxV);
        bufferBuilder.vertex(matrix, 0, height, 0).texture((minU + maxU) / 2, minV);
        bufferBuilder.vertex(matrix, 0, height, 0).texture((minU + maxU) / 2, minV);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrixStack.pop();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }
    public static void renderAnimatedCone(
            MatrixStack matrixStack,
            String namespace,
            String texturePath,
            Vec3d baseCenter,
            Vec3d cameraPos,
            float radius,
            float height,

            int columns,
            int rows,
            int totalFrames,
            int animationSpeed,
            long creationTime
    ) {
        int segments = 100;
        Identifier texture = Identifier.of(namespace, texturePath);
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - creationTime;
        int frame = (int) ((elapsedTime / (1000f / animationSpeed)) % totalFrames) + 1;

        int col = (frame - 1) % columns;
        int row = (frame - 1) / columns;

        float frameWidth = 1f / columns;
        float frameHeight = 1f / rows;

        float minU = col * frameWidth;
        float maxU = (col + 1) * frameWidth;
        float minV = row * frameHeight;
        float maxV = (row + 1) * frameHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        matrixStack.push();
        matrixStack.translate(baseCenter.x - cameraPos.x, baseCenter.y - cameraPos.y, baseCenter.z - cameraPos.z);


        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        for (int i = 0; i < segments; i++) {
            float angle1 = (float) (i * 2 * Math.PI / segments);
            float angle2 = (float) ((i + 1) * 2 * Math.PI / segments);

            float x1 = (float) (radius * Math.cos(angle1));
            float z1 = (float) (radius * Math.sin(angle1));
            float x2 = (float) (radius * Math.cos(angle2));
            float z2 = (float) (radius * Math.sin(angle2));

            float u1 = minU + (maxU - minU) * (float) i / segments;
            float u2 = minU + (maxU - minU) * (float) (i + 1) / segments;

            // Outside face
            bufferBuilder.vertex(matrix, x1, 0, z1).texture(u1, maxV);
            bufferBuilder.vertex(matrix, x2, 0, z2).texture(u2, maxV);
            bufferBuilder.vertex(matrix, 0, height, 0).texture(u2, minV);
            bufferBuilder.vertex(matrix, 0, height, 0).texture(u1, minV);

            // Inside face
            bufferBuilder.vertex(matrix, x2, 0, z2).texture(u2, maxV);
            bufferBuilder.vertex(matrix, x1, 0, z1).texture(u1, maxV);
            bufferBuilder.vertex(matrix, 0, height, 0).texture(u1, minV);
            bufferBuilder.vertex(matrix, 0, height, 0).texture(u2, minV);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrixStack.pop();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }
    public static void renderAnimatedTorus(
            MatrixStack matrixStack,
            String namespace,
            String texturePath,
            Vec3d center,
            Vec3d cameraPos,
            float majorRadius,
            float minorRadius,
            int columns,
            int rows,
            int totalFrames,
            int animationSpeed,
            long creationTime
    ) {
        int torusSegments = 32;
        int tubeSegments = 16;
        Identifier texture = Identifier.of(namespace, texturePath);
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - creationTime;
        int frame = (int) ((elapsedTime / (1000f / animationSpeed)) % totalFrames) + 1;

        int col = (frame - 1) % columns;
        int row = (frame - 1) / columns;

        float frameWidth = 1f / columns;
        float frameHeight = 1f / rows;

        float minU = col * frameWidth;
        float maxU = (col + 1) * frameWidth;
        float minV = row * frameHeight;
        float maxV = (row + 1) * frameHeight;

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        matrixStack.push();
        matrixStack.translate(center.x - cameraPos.x, center.y - cameraPos.y, center.z - cameraPos.z);

        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        for (int i = 0; i < torusSegments; i++) {
            float theta1 = (float) (i * 2 * Math.PI / torusSegments);
            float theta2 = (float) ((i + 1) * 2 * Math.PI / torusSegments);

            for (int j = 0; j < tubeSegments; j++) {
                float phi1 = (float) (j * 2 * Math.PI / tubeSegments);
                float phi2 = (float) ((j + 1) * 2 * Math.PI / tubeSegments);

                // Vertex 1
                float x1 = (float) ((majorRadius + minorRadius * Math.cos(phi1)) * Math.cos(theta1));
                float y1 = (float) (minorRadius * Math.sin(phi1));
                float z1 = (float) ((majorRadius + minorRadius * Math.cos(phi1)) * Math.sin(theta1));

                // Vertex 2
                float x2 = (float) ((majorRadius + minorRadius * Math.cos(phi1)) * Math.cos(theta2));
                float y2 = (float) (minorRadius * Math.sin(phi1));
                float z2 = (float) ((majorRadius + minorRadius * Math.cos(phi1)) * Math.sin(theta2));

                // Vertex 3
                float x3 = (float) ((majorRadius + minorRadius * Math.cos(phi2)) * Math.cos(theta2));
                float y3 = (float) (minorRadius * Math.sin(phi2));
                float z3 = (float) ((majorRadius + minorRadius * Math.cos(phi2)) * Math.sin(theta2));

                // Vertex 4
                float x4 = (float) ((majorRadius + minorRadius * Math.cos(phi2)) * Math.cos(theta1));
                float y4 = (float) (minorRadius * Math.sin(phi2));
                float z4 = (float) ((majorRadius + minorRadius * Math.cos(phi2)) * Math.sin(theta1));

                float u1 = minU + (maxU - minU) * i / torusSegments;
                float u2 = minU + (maxU - minU) * (i + 1) / torusSegments;
                float v1 = minV + (maxV - minV) * j / tubeSegments;
                float v2 = minV + (maxV - minV) * (j + 1) / tubeSegments;

                bufferBuilder.vertex(matrix, x1, y1, z1).texture(u1, v1);
                bufferBuilder.vertex(matrix, x2, y2, z2).texture(u2, v1);
                bufferBuilder.vertex(matrix, x3, y3, z3).texture(u2, v2);
                bufferBuilder.vertex(matrix, x4, y4, z4).texture(u1, v2);
            }
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrixStack.pop();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }
    public static void renderAnimatedElipse(
            MatrixStack matrixStack,
            String namespace,
            String texturePath,
            Vec3d center,
            Vec3d cameraPos,
            float width,
            float height,
            int segments,
            int columns,
            int rows,
            int totalFrames,
            int animationSpeed,
            long creationTime,
            boolean faceCamera
    ) {
        Identifier texture = Identifier.of(namespace, texturePath);
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - creationTime;
        int frame = (int) ((elapsedTime / (1000f / animationSpeed)) % totalFrames) + 1;

        int col = (frame - 1) % columns;
        int row = (frame - 1) / columns;

        float frameWidth = 1f / columns;
        float frameHeight = 1f / rows;

        float minU = col * frameWidth;
        float maxU = (col + 1) * frameWidth;
        float minV = row * frameHeight;
        float maxV = (row + 1) * frameHeight;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);

        matrixStack.push();
        matrixStack.translate(center.x - cameraPos.x, center.y - cameraPos.y, center.z - cameraPos.z);

        if (faceCamera) {
            Vec3d lookVec = cameraPos.subtract(center).normalize();
            float yaw = (float) Math.atan2(-lookVec.x, -lookVec.z);
            float pitch = (float) Math.asin(lookVec.y);
            matrixStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotation(yaw));
            matrixStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotation(pitch));
        } else {
            matrixStack.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_X.rotation((float) -Math.PI / 2));
        }


        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_TEXTURE);

        // Center vertex
        bufferBuilder.vertex(matrix, 0, 0, 0).texture((minU + maxU) / 2, (minV + maxV) / 2);

        for (int i = 0; i <= segments; i++) {
            double angle = i * 2 * Math.PI / segments;
            float x = (float) (width / 2 * Math.cos(angle));
            float y = (float) (height / 2 * Math.sin(angle));

            // Calculate texture coordinates using double precision
            double uDouble = minU + (maxU - minU) * (0.5 + 0.5 * Math.cos(angle));
            double vDouble = minV + (maxV - minV) * (0.5 + 0.5 * Math.sin(angle));

            // Clamp and cast to float
            float u = (float) Math.max(minU, Math.min(maxU, uDouble));
            float v = (float) Math.max(minV, Math.min(maxV, vDouble));

            bufferBuilder.vertex(matrix, x, y, 0).texture(u, v);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrixStack.pop();

        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
    }
}