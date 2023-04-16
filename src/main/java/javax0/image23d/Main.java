package javax0.image23d;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {
            final var properties = new Properties();
            properties.load(Files.newBufferedReader(Path.of("image23d.properties")));
            new Main(properties).convert();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final double layer;
    public final double base;
    public final double pixel;
    public final String inputFileName;
    public final double scale;

    public final boolean flip;

    private Main(Properties properties) {
        /*snippet conf400
* `layer` is the thickness of the layer in millimeters.
The default value is `0.02`.
The scale of the image is 0 to 255 in grayscale.
This is multiplied by the value of the parameter to get the thickness of the output at a certain point.
Since the thickness is proportional with the amount of light consumed, therefore the darker the pixel the thicker the layer.
A grayscale value `x` will be used as `(255-x)*layer` in millimeters.
         end snippet*/
        layer = Optional.ofNullable(properties.getProperty("layer"))
                .map(Double::parseDouble)
                .orElse(0.02);
        /*snippet conf500
* `base` is the base of the image in millimeters.
When calculating the height of each point this value is added to the result.
This will result a baseplate under the image.
The default value is `1.0`, meaning one millimeter.
         end snippet*/
        base = Optional.ofNullable(properties.getProperty("base"))
                .map(Double::parseDouble)
                .orElse(1.0);
        /*snippet conf300
* `pixel` is the size of one pixel in millimeters.
The default value is `0.1`.
         end snippet*/
        pixel = Optional.ofNullable(properties.getProperty("pixel"))
                .map(Double::parseDouble)
                .orElse(0.1);
        /*snippet conf100
* `image` must present in the configuration file, and it has to specify the input file name.
The output file name is automatically calculated from it shopping off the extension and replacing it with `.stl`.
This is the only mandatory parameter.
         end snippet*/
        inputFileName = Optional.ofNullable(properties.getProperty("image"))
                .orElseThrow(() -> new RuntimeException("image is not defined."));
        /*snippet conf200
* `scale` is the scaling of the image.
The default value is `1.0` which means that the image is not scaled.
Scale changes the size of the image in pixels. If scale is 0.5 then the number of the pixels are halved in both linear directions.
         end snippet*/
        scale = Optional.ofNullable(properties.getProperty("scale"))
                .map(Double::parseDouble)
                .orElse(1.0);
        /*snippet conf600
* `flip` is a boolean value, either `true` or `false`.
The default value is `false`, which means that the picture will not be flipped.
If the value is `true` then the picture will be flipped along the vertical axe.
         end snippet*/
        this.flip = Optional.ofNullable(properties.getProperty("flip"))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }


    public void convert() throws IOException {
        var dot = inputFileName.lastIndexOf('.');
        if (dot == -1) {
            dot = inputFileName.length();
        }
        final var outputFileName = inputFileName.substring(0, dot) + ".stl";

        final var image = new ScaledImage(scale, layer, base, flip, ImageIO.read(new File(inputFileName)));
        final var xMax = image.getXMax();
        final var yMax = image.getYMax();
        final var surface = new Surface(new HashSet<>());
        createBottom(image, xMax, yMax, surface);
        createSides(image, xMax, yMax, surface);
        createTop(image, xMax, yMax, surface);
        Files.write(Path.of(outputFileName), surface.toStl("image23d").getBytes());

    }

    private void createTop(final ScaledImage image, final double xMax, final double yMax, final Surface surface) {
        System.out.printf("Top surface is made of %f triangles%n", xMax * yMax * 2);
        for (int y = 0; y < yMax; y += 1) {
            for (int x = 0; x < xMax; x += 1) {
                final double xn = Math.min(x + 1, xMax);
                final double yn = Math.min(y + 1, yMax);
                final var polygon = new Polygon(List.of(
                        surfacePoint(image, x, y),
                        surfacePoint(image, xn, y),
                        surfacePoint(image, xn, yn),
                        surfacePoint(image, x, yn)
                ));
                surface.getTriangles().addAll(polygon.toTriangles());
            }
        }
    }

    private void createBottom(final ScaledImage ignored, final double xMax, final double yMax, final Surface surface) {
        System.out.printf("Creating picture %fmm times %fmm%n", xMax, yMax);
        surface.getTriangles().addAll(new Polygon(List.of(
                point(0, 0, 0),
                point(xMax, 0, 0),
                point(xMax, yMax, 0),
                point(0, yMax, 0))
        ).toTriangles());
    }

    /**
     * Create the side panels of the body of the picture.
     *
     * @param image   the scaled image
     * @param xMax    is the height of the image
     * @param yMax    is the width of the image
     * @param surface is the surface to add the triangles to
     */
    private void createSides(final ScaledImage image, final double xMax, final double yMax, final Surface surface) {
        for (int i = 0; i < 2; i++) {
            final var y = i * yMax;
            for (double x1 = 0; x1 < xMax; x1++) {
                final double x2 = Math.min(x1 + 1, xMax);
                final double z1 = image.getZ(x1, y);
                final double z2 = image.getZ(x2, y);
                surface.getTriangles().addAll(new Polygon(List.of(
                        point(x1, y, z1),
                        point(x2, y, z2),
                        point(x2, y, 0),
                        point(x1, y, 0)
                )).toTriangles());
            }
        }
        for (int i = 0; i < 2; i++) {
            final var x = i * xMax;
            for (double y1 = 0; y1 < yMax; y1++) {
                final double y2 = Math.min(y1 + 1, yMax);
                final double z1 = image.getZ(x, y1);
                final double z2 = image.getZ(x, y2);
                surface.getTriangles().addAll(new Polygon(List.of(
                        point(x, y1, z1),
                        point(x, y2, z2),
                        point(x, y2, 0),
                        point(x, y1, 0)
                )).toTriangles());
            }
        }
    }

    private Point surfacePoint(ScaledImage image, final double x, final double y) {
        return point(x, y, image.getZ(x, y));
    }

    private Point point(final double x, final double y, final double z) {
        return new Point(x * pixel, y * pixel, z);
    }

}

