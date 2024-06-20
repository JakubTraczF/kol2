package client;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ChartGenerator {
    public static BufferedImage generateChart(String[] values) {
        int width = 200;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Set background color to white
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Set points color to red
        g2d.setColor(Color.RED);

        // Draw points
        for (int i = 0; i < values.length; i++) {
            int x = i * 2;
            int y = height / 2 - Integer.parseInt(values[i]);
            g2d.fillRect(x, y, 1, 1);
        }

        g2d.dispose();
        return image;
    }
}
