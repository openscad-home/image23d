package javax0.image23d;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A three-dimensional polygon. Note that the points of in the polygon are not necessarily in the same plane.
 * The last point in the list is automatically connected to the first point.
 * The first point MUST NOT be the same as the last.
 */
public record Polygon(List<Point> points) {

    /**
     * Convert the polygon to a set of triangles.
     * <p>
     * The algorithm is extremely simple. Take the first, the second and the third point to create the first triangle.
     * Then take again the first point the third and the fourth to create the second triangle and so on.
     *
     * @return the set of triangles
     */
    public Set<Triangle> toTriangles() {
        final var result = new HashSet<Triangle>();
        for (int i = 1; i < points.size() - 1; i++) {
            result.add(new Triangle(points.get(0), points.get(i), points.get(i + 1)));
        }
        return result;
    }

}
