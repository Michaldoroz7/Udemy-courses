package org.example.Performance;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

// latency = time taken to execute a task
// This class will contain examples of multithreading for performance improvement
public class LatencyMultithreading {
    public static final String SOURCE_CLASSPATH = "XXXXXXXXXX";
    public static final String DESTINATION_FILE = "XXXXXXXXXXX";


    public static void main(String[] args) throws IOException {

        // Load image
        // Create output image
        BufferedImage image = ImageIO.read(new File(SOURCE_CLASSPATH));
        BufferedImage resultImage = new BufferedImage(
                image.getWidth(),
                image.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        // Recolor image
        // Measure time taken
        // Single threaded
        // Multi threaded
        Long startTime = System.currentTimeMillis();
//        recolorSingleThreaded(image, resultImage);
        recolorMultiThreaded(image, resultImage, 4);
        Long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        System.out.println("Single thread duration: " + duration + " ms");

        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(resultImage, "jpg", outputFile);

    }

    // Recolor image using multiple threads
    public static void recolorMultiThreaded(BufferedImage image, BufferedImage resultImage, int numberOfThreads) {
        List<Thread> threads = new java.util.ArrayList<>();
        int width = image.getWidth();
        int height = image.getHeight() / numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadMultiplier = i;
            Thread thread = new Thread(() -> {
                int topCorner = threadMultiplier * height;
                recolorImage(image, resultImage, 0, topCorner, width, height);
            });
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Recolor image using a single thread
    public static void recolorSingleThreaded(BufferedImage image, BufferedImage resultImage) {
        recolorImage(image, resultImage, 0, 0, image.getWidth(), image.getHeight());
    }

    // Recolor a portion of the image
    public static void recolorImage(BufferedImage image, BufferedImage resultImage, int leftCorner, int topCorner, int width, int height) {
        for (int x = leftCorner; x < leftCorner + width && x < image.getWidth(); x++) {
            for (int y = topCorner; y < topCorner + height && y < image.getHeight(); y++) {
                recolorPixes(image, resultImage, x, y);
            }
        }
    }

    // Recolor a single pixel
    public static void recolorPixes(BufferedImage image, BufferedImage resultImage, int x, int y) {
        int rgb = image.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;

        if (isShadeOfGray(red, green, blue)) {
            newRed = Math.min(255, red + 10);
            newGreen = Math.max(0, green - 80);
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }

        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    // Set RGB value of a pixel in BufferedImage
    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }

    // Check if the color is a shade of gray
    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs(green - blue) < 30;
    }

    // Method to create an integer RGB value from individual color components
    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;

        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000; // Alpha channel set to 255 (opaque)

        return rgb;
    }

    // Methods to extract RGB components from an integer
    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return (rgb & 0x000000FF);
    }
}
