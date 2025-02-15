package main.ch.thoenluk;

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
    private final Set<Colour> uniqueColours = new HashSet<>();

    public Image(final String path) throws IOException {
        this.path = path;
        final File input = new File(path);
        image = ImageIO.read(input);
        pixels = parsePixels();
    }

    public Cluster[] cluster(final int numberOfClusters, final int numberOfIterations) {
        double bestDistance = Double.MAX_VALUE;
        Cluster[] bestClusters = null;
        uniqueColours.clear();
        uniqueColours.addAll(Arrays.asList(pixels));
        for (int i = 0; i < numberOfIterations; i++) {
            final Cluster[] clusters = getRandomStartingClusters(numberOfClusters);
            iterateClusters(clusters);
            final double distance = Arrays.stream(clusters).map(Cluster::getDistance).reduce(Double::sum).orElseThrow();
            if (distance < bestDistance) {
                bestDistance = distance;
                bestClusters = clusters;
            }
        }
        return bestClusters;
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
            for (final Colour uniqueColour : uniqueColours) {
                uniqueColour.addToNearestCluster(clusters);
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
        Arrays.fill(pixelData, (byte) 255);

        for (int i = 0; i < pixels.length; i++) {
            System.arraycopy(pixels[i].asRgb(), 0, pixelData, i * dataElements, 3);
        }
        raster.setDataElements(0, 0, width, height, pixelData);
        final String[] parts = path.split("\\.");
        final File outputFolder = new File("output");
        if (!outputFolder.exists()) {
            outputFolder.mkdir();
        }
        final String outputPath = "output/" + parts[0] + "_out." + parts[1];
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

    public void mapPixelsToClusters(final Cluster[] colours, final ScalingType scalingType, final int height, final int width, final int diameter) {
        switch (scalingType) {
            case NO_SCALING -> mapPixelsDirectly(colours);
            case SQUARE, RECTANGLE -> scalePixelsRectangular(colours, scalingType, height, width);
            case LTR_SLOPE, RTL_SLOPE -> scalePixelsSloped(colours, scalingType, height, width, diameter);
        }
    }

    private void mapPixelsDirectly(final Cluster[] colours) {
        for (final Colour pixel : pixels) {
            final Colour nearestColour = pixel.findNearestCluster(colours);
            pixel.setCielabValuesToColour(nearestColour);
        }
    }

    private void scalePixelsRectangular(final Cluster[] colours, final ScalingType scalingType, final int height, final int width) {
        final WritableRaster raster = image.getRaster();
        final int imageWidth = raster.getWidth();
        final int imageHeight = raster.getHeight();
        for (int y = 0; y < imageHeight; y += height) {
            for (int x = 0; x < imageWidth; x += width) {
                final ScaledPixel scaledPixel = switch (scalingType) {
                    case SQUARE -> ScaledPixel.createSquare(pixels, imageWidth, height, y, x);
                    case RECTANGLE -> ScaledPixel.createRectangle(pixels, imageWidth, height, width, y, x);
                    default -> throw new IllegalStateException("Unexpected value: " + scalingType);
                };
                scaledPixel.setPixelsCielabValuesToNearestColourByFirstPastThePost(colours);
            }
        }
    }

    private void scalePixelsSloped(final Cluster[] colours, final ScalingType scalingType, final int height, final int width, final int diameter) {
        final WritableRaster raster = image.getRaster();
        final int imageWidth = raster.getWidth();
        final int imageHeight = raster.getHeight();
        for (int y = 0; y < imageHeight; y += height) {
            for (int x = 0; x < imageWidth; x += diameter) {
                final ScaledPixel scaledPixel = switch (scalingType) {
                    case LTR_SLOPE -> ScaledPixel.createLtrSlope(pixels, imageWidth, height, width, y, x, diameter);
                    case RTL_SLOPE -> ScaledPixel.createRtlSlope(pixels, imageWidth, height, width, y, x, diameter);
                    default -> throw new IllegalStateException("Unexpected value: " + scalingType);
                };
                scaledPixel.setPixelsCielabValuesToNearestColourByFirstPastThePost(colours);
            }
        }
    }
}
