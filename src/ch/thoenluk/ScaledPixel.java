package ch.thoenluk;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScaledPixel {
    private final List<Colour> pixels;

    public ScaledPixel(final Colour[] imagePixels, final int imageWidth, final int scalingFactor, final int topleftX, final int topleftY) {
        final int topleftIndex = topleftY * imageWidth + topleftX;
        final List<Colour> pixels = new LinkedList<>();
        for (int y = 0; y < scalingFactor; y++) {
            for (int x = 0; x < scalingFactor; x++) {
                pixels.add(imagePixels[topleftIndex + y * imageWidth + x]);
            }
        }
        this.pixels = pixels;
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
