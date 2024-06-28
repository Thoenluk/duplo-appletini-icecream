package ch.thoenluk;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Image {
    final String path;
    final BufferedImage image;

    private static float[] rgbToXyz(final byte[] rgb) {

        return null;
    }

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
            for (int y = 0; y < 90; y++) {
                for (int dataElement = 0; dataElement < dataElements; dataElement++) {
                    pixels[y * width + x * dataElements + dataElement] = (byte) 0xff;
                }
            }
        }
        raster.setDataElements(0, 0, width, height, pixels);
        final String[] parts = path.split("\\.");
        final String outputPath = parts[0] + "_out." + parts[1];
        final File output = new File(outputPath);
        ImageIO.write(image, parts[1], output);
    }
}
