package javax0.image23d;

import java.awt.image.BufferedImage;

public class ScaledImage {

    private final double scale;
    private final double layer;
    private final double base;
    private final boolean flip;


    private final BufferedImage image;

    private final int yMax, xMax;
    private final double scaledYmax, scaledXmax;

    public ScaledImage(final double scale, final double layer, final double base, final boolean flip, final BufferedImage image) {
        this.scale = scale;
        this.layer = layer;
        this.base = base;
        this.flip = flip;
        this.image = image;

        xMax = image.getWidth();
        yMax = image.getHeight();
        scaledXmax = xMax * scale;
        scaledYmax = yMax * scale;
    }


    public double getXMax() {
        return scaledXmax;
    }

    public double getYMax() {
        return scaledYmax;
    }

    public double getZ(final double x, final double y) {
        int unscaledX = (int) (x / scale);
        int unscaledY = (int) (y / scale);
        final int z = 255 - getRawValue(unscaledX, unscaledY);
        return z * layer + base;
    }

    private int getRawValue(int x, int y) {
        if (x >= xMax) {
            System.out.printf("x=%d xMax=%d%n", x, xMax);
            x = xMax - 1;
        }
        if (y >= yMax) {
            System.out.printf("y=%d yMax=%d%n", y, yMax);
            y = yMax - 1;
        }
        if (flip) {
            x = xMax - x - 1;
        }
        final var rgb = image.getRGB(x, y);
        final var r = (rgb >> 16) & 0xFF;
        final var g = (rgb >> 8) & 0xFF;
        final var b = rgb & 0xFF;
        return (r + g + b) / 3;
    }
}
