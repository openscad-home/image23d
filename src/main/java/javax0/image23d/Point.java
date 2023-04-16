package javax0.image23d;

/**
 * This class is used to generate the STL file from the image.
 */
public record Point(double x, double y, double z) {

    public String toStl() {
        return String.format("vertex %f %f %f\n", x, y, z);
    }
}
