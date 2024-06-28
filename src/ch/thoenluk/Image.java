package ch.thoenluk;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    public void mapPixelsToColours(final Colour[] colours) {
        final Map<Colour, Colour> nearestColours = new HashMap<>();
        for (final Colour pixel : pixels) {
            final Colour nearestColour = nearestColours.computeIfAbsent(pixel, p -> p.findNearestColour(colours));
            pixel.setCielabValuesToColour(nearestColour);
        }
    }
}
