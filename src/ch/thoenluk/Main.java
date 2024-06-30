package ch.thoenluk;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        final RunParameters runParameters = interrogateUser();
        final Image image = new Image(runParameters.fileName());
        final Cluster[] clusters = Optional.ofNullable(runParameters.spectrum())
                .orElseGet(() -> image.cluster(runParameters.numberOfColours(), runParameters.numberOfIterations));
        System.out.println("Converged on:");
        Arrays.stream(clusters).forEach(c -> System.out.println(Arrays.toString(c.asRgb())));
        image.mapPixelsToClusters(clusters, runParameters.scalingFactor());
        System.out.println("All done mapping pixels!");
        image.writeToDefaultOutput();
    }

    private static RunParameters interrogateUser() {
        final Scanner input = new Scanner(System.in);
        final File folder = new File(".");
        final String[] images = folder.list((file, fn) -> fn.endsWith(".png") || fn.endsWith(".jpg"));
        System.out.println("Found the following images:");
        for (int i = 0; i < Objects.requireNonNull(images).length; i++) {
            System.out.printf("%d: %s%n", i, images[i]);
        }
        final String fileName = images[input.nextInt()];
        System.out.printf("Selected %s. Which spectrum should be used?%n", fileName);
        System.out.println("0: Grayscale");
        System.out.println("1: Grayscale without black");
        System.out.println("2: Rainbow");
        System.out.println("3: Auto-detect");
        final Cluster[] spectrum = switch (input.nextInt()) {
            case 0 -> Cluster.GRAYSCALE;
            case 1 -> Cluster.GRAYSCALE_NO_BLACK;
            case 2 -> Cluster.PRIDE;
            case 3 -> null;
            default -> throw new IllegalArgumentException("Clown detected. Shutting down.");
        };
        final int numberOfColours;
        final int numberOfIterations;
        if (spectrum == null) {
            System.out.println("Auto-detecting colours. How many distinct colours should there be?");
            numberOfColours = input.nextInt();
            System.out.println("How many times to cluster for best result? More = slower, but probably better.");
            numberOfIterations = input.nextInt();
        }
        else {
            numberOfColours = 0;
            numberOfIterations = 0;
        }
        System.out.println("Enter the scaling factor, or any number < 2 to not scale.");
        final int scalingFactor = Math.max(1, input.nextInt());
        return new RunParameters(fileName, spectrum, numberOfColours, numberOfIterations, scalingFactor);
    }

    private record RunParameters(String fileName, Cluster[] spectrum, int numberOfColours, int numberOfIterations, int scalingFactor) {}
}
