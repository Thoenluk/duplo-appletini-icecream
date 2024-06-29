package ch.thoenluk;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        final Image image = new Image("Cinnamon Stick reference.png");
        final Cluster[] clusters = image.cluster(5);
        System.out.println("Converged on:");
        Arrays.stream(clusters).forEach(c -> System.out.println(Arrays.toString(c.asRgb())));
        image.mapPixelsToClusters(clusters);
        System.out.println("All done mapping pixels!");
        image.writeToDefaultOutput();
    }
}
