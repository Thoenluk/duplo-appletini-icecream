package ch.thoenluk;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        final Image image = new Image("brekfest.png");
        image.writeToDefaultOutput();
    }
}
