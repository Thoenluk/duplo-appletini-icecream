package ch.thoenluk;

import java.util.HashSet;
import java.util.Set;

public class Cluster extends Colour {
    public static final Cluster[] GRAYSCALE = new Cluster[]{
            new Cluster(0, 0, 0),
            new Cluster(60, 60, 60),
            new Cluster(118, 117, 117),
            new Cluster(158, 155, 156),
            new Cluster(218, 218, 217),
            new Cluster(255, 255, 255)
    };
    public static final Cluster[] PRIDE = new Cluster[]{
            new Cluster(255, 0, 0),
            new Cluster(255, 127, 0),
            new Cluster(255, 255, 0),
            new Cluster(127, 255, 0),
            new Cluster(0, 255, 0),
            new Cluster(0, 255, 127),
            new Cluster(0, 255, 255),
            new Cluster(0, 127, 255),
            new Cluster(0, 0, 255),
            new Cluster(127, 0, 255),
            new Cluster(255, 0, 255),
            new Cluster(255, 0, 127)
    };
    private final Set<Colour> colours = new HashSet<>();

    public Cluster(byte red, byte green, byte blue) {
        super(red, green, blue);
    }

    public Cluster(int red, int green, int blue) {
        super(red, green, blue);
    }

    public Cluster(byte[] rgb) {
        super(rgb);
    }

    public Cluster(final Colour colour) {
        super(colour.asRgb());
    }

    public void addColour(final Colour colour) {
        colours.add(colour);
    }

    public void removeColour(final Colour colour) {
        colours.remove(colour);
    }

    public double setToMeanOfElements() {
        final int numberOfElements = colours.size();
        double newL = 0;
        double newA = 0;
        double newB = 0;
        for (final Colour element : colours) {
            newL += element.getL() / numberOfElements;
            newA += element.getA() / numberOfElements;
            newB += element.getB() / numberOfElements;
        }
        final double result = Math.abs(getL() - newL) + Math.abs(getA() - newA) + Math.abs(getB() - newB);
        setCielabValues(newL, newA, newB);
        return result;
    }

    public double getDistance() {
        return colours.stream().map(c -> c.getSquaredDistanceFrom(this)).reduce(Double::sum).orElse(0D);
    }
}
