package ch.thoenluk;

import java.util.Arrays;

import static ch.thoenluk.ColourConverter.*;

public class Colour {
    private final double[] cielab;

    public Colour(final byte red, final byte green, final byte blue) {
        this(new byte[]{red, green, blue});
    }

    public Colour(final byte[] rgb) {
        cielab = ColourConverter.rgbToCielab(rgb);
    }

    public void setCielabValues(final double l, final double a, final double b) {
        cielab[L] = l;
        cielab[A] = a;
        cielab[B] = b;
    }

    public void setCielabValuesToColour(final Colour colour) {
        setCielabValues(
                colour.getL(),
                colour.getA(),
                colour.getB()
        );
    }

    public double getL() {
        return cielab[L];
    }

    public double getA() {
        return cielab[A];
    }

    public double getB() {
        return cielab[B];
    }

    public double getDistanceFrom(final Colour other) {
        return Math.sqrt(
            Math.pow(getL() - other.getL(), 2) +
            Math.pow(getA() - other.getA(), 2) +
            Math.pow(getB() - other.getB(), 2)
        );
    }

    public Colour findNearestColour(final Colour[] colours) {
        Colour nearest = colours[0];
        double nearestDistance = getDistanceFrom(nearest);
        for (int i = 1; i < colours.length; i++) {
            final double colourDistance = getDistanceFrom(colours[i]);
            if (colourDistance < nearestDistance) {
                nearest = colours[i];
                nearestDistance = colourDistance;
            }
        }
        return nearest;
    }

    public byte[] asRgb() {
        return ColourConverter.cielabToRgb(cielab);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(cielab);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof final Colour pixel)) {
            return false;
        }
        return Arrays.equals(cielab, pixel.cielab);
    }
}
