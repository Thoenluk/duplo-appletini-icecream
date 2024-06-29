package ch.thoenluk;

import java.util.HashSet;
import java.util.Set;

public class Cluster extends Colour {
    private final Set<Colour> colours = new HashSet<>();

    public Cluster(byte red, byte green, byte blue) {
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
}
