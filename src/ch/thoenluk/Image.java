package ch.thoenluk;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static ch.thoenluk.ColourConverter.*;

public class Image {

    private final String path;
    private final BufferedImage image;

    public Image(final String path) throws IOException {
        this.path = path;
        final File input = new File(path);
        image = ImageIO.read(input);
    }

    public void writeToDefaultOutput() throws IOException {
        final WritableRaster raster = image.getRaster();
        final int dataElements = raster.getNumDataElements();
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final byte[] pixels = new byte[width * height * dataElements];
        raster.getDataElements(0, 0, width, height, pixels);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int start = y * width * dataElements + x * dataElements;
                final byte[] rgb = Arrays.copyOfRange(pixels, start, start + dataElements);
                final double[] cielab = ColourConverter.rgbToCielab(rgb);
                cielab[L] *= 2;
                final byte[] result = ColourConverter.cielabToRgb(cielab);
                System.arraycopy(result, 0, pixels, start, dataElements);
            }
        }
        raster.setDataElements(0, 0, width, height, pixels);
        final String[] parts = path.split("\\.");
        final String outputPath = parts[0] + "_out." + parts[1];
        final File output = new File(outputPath);
        ImageIO.write(image, parts[1], output);
    }
}
