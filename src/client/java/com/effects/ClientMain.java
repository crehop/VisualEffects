package com.effects;

import com.effects.packets.ShapePacketS2C;
import com.effects.utils.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClientMain implements ClientModInitializer {
	private static final List<Shape> activeShapes = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		registerClientPacketReceivers();
		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null || client.player == null) return;

			MatrixStack matrixStack = context.matrixStack();
			VertexConsumerProvider vertexConsumers = context.consumers();
			Camera camera = context.camera();

			if (matrixStack != null && vertexConsumers != null) {
				activeShapes.removeIf(shape -> System.currentTimeMillis() - shape.getCreationTime() > 45000);
				for (Shape shape : activeShapes) {
					shape.render(matrixStack, vertexConsumers, camera);
				}
			}
		});
	}

	private void registerClientPacketReceivers() {
		ClientPlayNetworking.registerGlobalReceiver(ShapePacketS2C.ID, (payload, context) -> {
			if (payload instanceof ShapePacketS2C shapePacket) {
				context.client().execute(() -> {
					handleShapeCreation(shapePacket.getShapeType(), shapePacket.getShapeName(), shapePacket.getGifUrl(),
							shapePacket.getPosition(), shapePacket.getAnimationSpeed(), shapePacket.getOverwrite(),
							shapePacket.getWidth(), shapePacket.getHeight(), shapePacket.getLength(),
							shapePacket.getRotationSpeedX(), shapePacket.getRotationSpeedY(), shapePacket.getRadius(),
							shapePacket.getBaseWidth(), shapePacket.getBaseLength(),
							shapePacket.getMajorRadius(), shapePacket.getMinorRadius(), shapePacket.getFaceCamera());
				});
			}
		});
	}
	private void handleShapeCreation(String shapeType, String baseName, String gifUrl, BlockPos position, int animationSpeed, boolean overwrite,
									 float width, float height, float length, float rotationSpeedX, float rotationSpeedY, float radius,
									 float baseWidth, float baseLength, float majorRadius, float minorRadius, boolean faceCamera) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			String outputDir = "resources/assets/effects/textures/shape/";
			File[] existingFiles = new File(outputDir).listFiles((dir, name) -> name.contains("_" + baseName + ".png"));

			File outputFile;
			if (existingFiles != null && existingFiles.length > 0) {
				if (overwrite) {
					outputFile = existingFiles[0];
					outputFile.delete();
					outputFile = GifSplitter.processGifFromUrl(gifUrl, outputDir, baseName);
				} else {
					outputFile = existingFiles[0];
					System.out.println("Using existing file: " + outputFile.getName());
				}
			} else {
				outputFile = GifSplitter.processGifFromUrl(gifUrl, outputDir, baseName);
			}

			if (outputFile != null) {
				String fileName = outputFile.getName().toLowerCase();
				System.out.println("textures/shape/" + fileName);
				String[] parts = fileName.split("_");
				int rows = Integer.parseInt(parts[0]);
				int columns = Integer.parseInt(parts[1]);
				int totalFrames = Integer.parseInt(parts[2]);

				// Use TextureLoader to load the texture dynamically
				Identifier texture = TextureLoader.loadTexture(fileName);

				if (texture != null) {
					Shape shape = createShape(shapeType, position, texture, rows, columns, totalFrames, animationSpeed,
							width, height, length, rotationSpeedX, rotationSpeedY, radius,
							baseWidth, baseLength, majorRadius, minorRadius, faceCamera);
					if (shape != null) {
						activeShapes.add(shape);
						client.player.sendMessage(Text.literal("§a" + shapeType + " created: animation length: " +
								totalFrames + " frames, at: " + position.toShortString()), false);
					} else {
						client.player.sendMessage(Text.literal("§cFailed to create " + shapeType + " shape."), false);
					}
				} else {
					client.player.sendMessage(Text.literal("§cFailed to load texture for " + shapeType + " shape."), false);
				}
			} else {
				client.player.sendMessage(Text.literal("§cFailed to process GIF."), false);
			}
		}
	}

	private Shape createShape(String shapeType, BlockPos position, Identifier texture, int rows, int columns, int totalFrames, int animationSpeed,
							  float width, float height, float length, float rotationSpeedX, float rotationSpeedY, float radius,
							  float baseWidth, float baseLength, float majorRadius, float minorRadius, boolean faceCamera) {
		switch (shapeType) {
			case "cube":
				return new CubeShape(position, texture, rows, columns, totalFrames, animationSpeed,
						width, height, length, rotationSpeedX, rotationSpeedY);
			case "sphere":
				return new SphereShape(position, texture, rows, columns, totalFrames, animationSpeed, radius);
			case "pyramid":
				return new PyramidShape(position, texture, rows, columns, totalFrames, animationSpeed,
						baseWidth, baseLength, height);
			case "cone":
				return new ConeShape(position, texture, rows, columns, totalFrames, animationSpeed, radius, height);
			case "torus":
				return new TorusShape(position, texture, rows, columns, totalFrames, animationSpeed,
						majorRadius, minorRadius);
			case "ellipse":
				return new EllipseShape(position, texture, rows, columns, totalFrames, animationSpeed, width, height);
			case "plane":
				return new PlaneShape(position, texture, rows, columns, totalFrames, animationSpeed,
						width, height, faceCamera);
			default:
				return null;
		}
	}
}