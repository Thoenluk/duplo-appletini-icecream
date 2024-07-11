package main.ch.thoenluk;

import java.util.Arrays;

import static main.ch.thoenluk.ColourConverter.*;

public class Colour {


    private final double[] cielab;
    private Cluster cluster = null;

    public Colour(final byte red, final byte green, final byte blue) {
        this(new byte[]{red, green, blue});
    }

    public Colour(final int red, final int green, final int blue) {
        this((byte) red, (byte) green, (byte) blue);
    }

    public Colour(final double l, final double a, final double b) {
        cielab = new double[]{l, a, b};
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

    public double getSquaredDistanceFrom(final Colour other) {
        return Math.pow(getL() - other.getL(), 2) +
            Math.pow(getA() - other.getA(), 2) +
            Math.pow(getB() - other.getB(), 2);
    }

    public Cluster findNearestCluster(final Cluster[] clusters) {
        Cluster nearest = clusters[0];
        double nearestDistance = getSquaredDistanceFrom(nearest);
        for (int i = 1; i < clusters.length; i++) {
            final double colourDistance = getSquaredDistanceFrom(clusters[i]);
            if (colourDistance < nearestDistance) {
                nearest = clusters[i];
                nearestDistance = colourDistance;
            }
        }
        return nearest;
    }

    public void addToNearestCluster(final Cluster[] clusters) {
        final Cluster nearestCluster = findNearestCluster(clusters);
        if (cluster == nearestCluster) {
            return;
        }
        if (cluster != null) {
            cluster.removeColour(this);
        }
        cluster = nearestCluster;
        nearestCluster.addColour(this);
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

    @Override
    public String toString() {
        return Arrays.toString(cielab);
    }
}
