package ch.thoenluk;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Image {

    private final String path;
    private final BufferedImage image;
    private final Colour[] pixels;

    public Image(final String path) throws IOException {
        this.path = path;
        final File input = new File(path);
        image = ImageIO.read(input);
        pixels = parsePixels();
    }

    public Cluster[] cluster(final int numberOfClusters) {
        final Cluster[] clusters = getRandomStartingClusters(numberOfClusters);
        iterateClusters(clusters);
        return clusters;
    }

    private Cluster[] getRandomStartingClusters(final int numberOfClusters) {
        final List<Cluster> result = new LinkedList<>();
        final Random random = new Random();
        final Set<Integer> indices = new HashSet<>();
        for (int i = 0; i < numberOfClusters; i++) {
            int index;
            do {
                index = random.nextInt(pixels.length);
            } while (indices.contains(index));
            indices.add(index);
        }
        for (final Integer index : indices) {
            result.add(new Cluster(pixels[index]));
        }
        return result.toArray(new Cluster[0]);
    }

    private void iterateClusters(final Cluster[] clusters) {
        double distance = 0;
        double previousDistance;
        do {
            previousDistance = distance;
            for (final Colour pixel : pixels) {
                pixel.addToNearestCluster(clusters);
            }
            distance = 0;
            for (final Cluster cluster : clusters) {
                distance += cluster.setToMeanOfElements();
            }
        } while (Math.abs(previousDistance - distance) > distance * 0.01);
    }

    public void writeToDefaultOutput() throws IOException {
        final WritableRaster raster = image.getRaster();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final int dataElements = raster.getNumDataElements();
        final byte[] pixelData = new byte[pixels.length * dataElements];

        for (int i = 0; i < pixels.length; i++) {
            System.arraycopy(pixels[i].asRgb(), 0, pixelData, i * dataElements, dataElements);
        }
        raster.setDataElements(0, 0, width, height, pixelData);
        final String[] parts = path.split("\\.");
        final String outputPath = parts[0] + "_out." + parts[1];
        final File output = new File(outputPath);
        ImageIO.write(image, parts[1], output);
    }

    private Colour[] parsePixels() {
        final WritableRaster raster = image.getRaster();
        final int dataElements = raster.getNumDataElements();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final byte[] pixelData = new byte[width * height * dataElements];
        final Colour[] parsedPixels = new Colour[width * height];
        raster.getDataElements(0, 0, width, height, pixelData);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int start = y * width * dataElements + x * dataElements;
                final byte[] rgb = Arrays.copyOfRange(pixelData, start, start + dataElements);
                parsedPixels[y * width + x] = new Colour(rgb);
            }
        }
        return parsedPixels;
    }

    public void mapPixelsToClusters(final Cluster[] colours) {
        for (final Colour pixel : pixels) {
            final Colour nearestColour = pixel.findNearestCluster(colours);
            pixel.setCielabValuesToColour(nearestColour);
        }
    }
}
