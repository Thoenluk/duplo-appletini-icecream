package main.ch.thoenluk;

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
                .orElseGet(() -> image.cluster(runParameters.numberOfColours(), runParameters.numberOfIterations()));
        System.out.println("Converged on:");
        Arrays.stream(clusters).forEach(c -> System.out.println(Arrays.toString(c.asRgb())));
        image.mapPixelsToClusters(clusters, runParameters.scalingType(), runParameters.height(), runParameters.width(), runParameters.diameter());
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
        System.out.println("How to scale?");
        final ScalingType[] scalingTypes = ScalingType.values();
        Arrays.stream(scalingTypes).forEachOrdered(scalingType -> System.out.printf("%d: %s%n", scalingType.ordinal(), scalingType.getDescription()));
        final int ordinal = input.nextInt();
        if (ordinal < 0 || ordinal >= scalingTypes.length) {
            throw new IllegalArgumentException("Clown detected. Shutting down.");
        }
        final ScalingType scalingType = scalingTypes[ordinal];
        final int height;
        final int width;
        final int diameter;
        switch (scalingType) {
            case NO_SCALING -> {
                height = 1;
                width = 1;
                diameter = 1;
            }
            case SQUARE -> {
                System.out.println("How many pixels on each side of the square?");
                final int sideLength = input.nextInt();
                height = sideLength;
                width = sideLength;
                diameter = 1;
            }
            case RECTANGLE -> {
                System.out.println("How tall should each rectangle be?");
                height = input.nextInt();
                System.out.println("How wide should each rectangle be?");
                width = input.nextInt();
                diameter = 1;
            }
            case LTR_SLOPE, RTL_SLOPE -> {
                System.out.println("How thick should the line be?");
                diameter = input.nextInt();
                System.out.println("How many pixels tall should each line be (Total distance)?");
                height = input.nextInt();
                System.out.println("How many pixels wide should each line be (Total distance)?");
                width = input.nextInt();
            }
            default -> throw new IllegalStateException("The compiler is very dumb and insists upon this unreachable branch");
        }
        return new RunParameters(fileName, spectrum, numberOfColours, numberOfIterations, scalingType, height, width, diameter);
    }

    private record RunParameters(String fileName, Cluster[] spectrum, int numberOfColours, int numberOfIterations, ScalingType scalingType, int height, int width, int diameter) {}
}
