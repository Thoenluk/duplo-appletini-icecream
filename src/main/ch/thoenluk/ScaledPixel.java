package main.ch.thoenluk;

import java.util.*;

public class ScaledPixel {
    private final List<Colour> pixels;

    private ScaledPixel(final List<Colour> pixels) {
        this.pixels = pixels;
    }

    public static ScaledPixel createRectangle(final Colour[] imagePixels, final int imageWidth, final int height, final int width, final int topLeftY, final int topLeftX) {
        final int topLeftIndex = topLeftY * imageWidth + topLeftX;
        final List<Colour> pixels = new LinkedList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                addPixelIfInImage(imagePixels, pixels, topLeftIndex, y, imageWidth, x);
            }
        }
        return new ScaledPixel(pixels);
    }

    public static ScaledPixel createSquare(final Colour[] imagePixels, final int imageWidth, final int scalingFactor, final int topLeftY, final int topLeftX) {
        return createRectangle(imagePixels, imageWidth, scalingFactor, scalingFactor, topLeftY, topLeftX);
    }

    public static ScaledPixel createLtrSlope(final Colour[] imagePixels, final int imageWidth, final int height, final int width, final int topLeftY, final int topLeftX, final int diameter) {
        return createSlope(imagePixels, imageWidth, height, width, topLeftY, topLeftX, diameter, false);
    }

    public static ScaledPixel createRtlSlope(final Colour[] imagePixels, final int imageWidth, final int height, final int width, final int topLeftY, final int topLeftX, final int diameter) {
        return createSlope(imagePixels, imageWidth, height, width, topLeftY, topLeftX, diameter, true);
    }

    public static ScaledPixel createSlope(final Colour[] imagePixels, final int imageWidth, final int height, final int width, final int topLeftY, final int topLeftX, final int diameter, final boolean goUp) {
        final int topLeftIndex = topLeftY * imageWidth + topLeftX;
        final List<Colour> pixels = new LinkedList<>();
        final float ratio = ((float) height) / width;
        for (int x = 0; x < width; x++) {
            final int y = goUp ? height - Math.round(x * ratio) : Math.round(x * ratio);
            addPixelIfInImage(imagePixels, pixels, topLeftIndex, y, imageWidth, x);
            for (int d = 1; d < diameter; d++) {
                addPixelIfInImage(imagePixels, pixels, topLeftIndex, y, imageWidth, x + d);
            }
        }
        return new ScaledPixel(pixels);
    }

    private static void addPixelIfInImage(final Colour[] imagePixels,
                                          final List<Colour> pixels,
                                          final int topLeftIndex,
                                          final int y,
                                          final int imageWidth,
                                          final int x) {
        final int pixelIndex = topLeftIndex + y * imageWidth + x;
        if (pixelIndex >= imagePixels.length) {
            return;
        }
        pixels.add(imagePixels[pixelIndex]);
    }

    public void setPixelsCielabValuesToNearestColourOfAverage(final Cluster[] availableClusters) {
        final int numberOfElements = pixels.size();
        double averageL = 0;
        double averageA = 0;
        double averageB = 0;
        for (final Colour pixel : pixels) {
            averageL += pixel.getL() / numberOfElements;
            averageA += pixel.getA() / numberOfElements;
            averageB += pixel.getB() / numberOfElements;
        }
        final Colour average = new Colour(averageL, averageA, averageB);
        final Cluster newColour = average.findNearestCluster(availableClusters);
        for (final Colour pixel : pixels) {
            pixel.setCielabValuesToColour(newColour);
        }
    }

    // Because a simple plurality always fixes everything.
    public void setPixelsCielabValuesToNearestColourByFirstPastThePost(final Cluster[] availableClusters) {
        final Map<Cluster, Integer> occurences = new HashMap<>();
        for (final Colour pixel : pixels) {
            final Cluster newColour = pixel.findNearestCluster(availableClusters);
            occurences.putIfAbsent(newColour, 0);
            occurences.put(newColour, occurences.get(newColour) + 1);
        }
        int mostOccurences = 0;
        Cluster primeMinister = availableClusters[0];
        for (Map.Entry<Cluster, Integer> entry : occurences.entrySet()) {
            if (entry.getValue() > mostOccurences) {
                mostOccurences = entry.getValue();
                primeMinister = entry.getKey();
            }
        }
        for (Colour pixel : pixels) {
            pixel.setCielabValuesToColour(primeMinister);
        }
    }
}
