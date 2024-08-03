package com.effects.utils;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GifSplitter {

    @SuppressWarnings("deprecation")
    public static List<BufferedImage> splitGif(InputStream inputStream) throws IOException {
        List<BufferedImage> frames = new ArrayList<>();

        try (ImageInputStream is = ImageIO.createImageInputStream(inputStream)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(is);
            if (!readers.hasNext()) {
                throw new IOException("No image reader found for GIF format");
            }

            ImageReader reader = readers.next();
            reader.setInput(is);

            int numFrames = reader.getNumImages(true);

            for (int i = 0; i < numFrames; i++) {
                BufferedImage frame = reader.read(i);
                frames.add(frame);
            }
        }

        return frames;
    }

    public static File saveAsSingleImage(List<BufferedImage> frames, String outputDir, String baseName) throws IOException {
        if (frames.isEmpty()) {
            throw new IllegalArgumentException("No frames to process");
        }

        int columns = 5; // Always 5 columns
        int totalFrames = frames.size();
        int rows = (int) Math.ceil((double) totalFrames / columns);

        int frameWidth = frames.get(0).getWidth();
        int frameHeight = frames.get(0).getHeight();

        BufferedImage result = new BufferedImage(frameWidth * columns, frameHeight * rows, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = result.createGraphics();

        for (int i = 0; i < totalFrames; i++) {
            int x = (i % columns) * frameWidth;
            int y = (i / columns) * frameHeight;
            g2d.drawImage(frames.get(i), x, y, frameWidth, frameHeight, null);
        }

        g2d.dispose();

        File dir = new File(outputDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Failed to create output directory");
        }

        String fileName = String.format("%d_%d_%d_%s.png", rows, columns, totalFrames, baseName.toLowerCase());
        File outputFile = new File(dir, fileName);
        ImageIO.write(result, "png", outputFile);

        System.out.println("GIF successfully processed into a single image: " + fileName);
        return outputFile;
    }

    public static File processGifFromUrl(String gifUrl, String outputDir, String baseName) {
        HttpURLConnection connection = null;
        File output;
        try {
            URL url = new URL(gifUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            try (InputStream inputStream = connection.getInputStream()) {
                List<BufferedImage> frames = splitGif(inputStream);
                output = saveAsSingleImage(frames, outputDir, baseName);
                return output;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("GIF URL INVALID OR PROCESSING FAILED. TRY AGAIN");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}