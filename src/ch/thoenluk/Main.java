package ch.thoenluk;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        final Image image = new Image("brekfest redrawn.png");
        final Colour[] colours = new Colour[]{
            new Colour(b(255), b(255), b(255)),
            new Colour(b(158), b(153), b(157)),
            new Colour(b(175), b(171), b(182)),
            new Colour(b(143), b(142), b(157)),
            new Colour(b(40), b(13), b(7)),
            new Colour(b(178), b(115), b(72)),
            new Colour(b(232), b(195), b(150)),
            new Colour(b(250), b(241), b(230)),
            new Colour(b(211), b(170), b(138)),
            new Colour(b(216), b(169), b(140)),
            new Colour(b(150), b(108), b(95)),
            new Colour(b(204), b(159), b(132)),
            new Colour(b(165), b(119), b(105))
        };
        image.mapPixelsToColours(colours);
        image.writeToDefaultOutput();
    }

    private static byte b(final int i) {
        return (byte) i;
    }
}
