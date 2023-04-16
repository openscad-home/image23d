package javax0.image23d;

import java.util.Set;

/**
 * This class is used to generate the STL file from the image.
 */
public record Surface(Set<Triangle> triangles) {

    public Set<Triangle> getTriangles() {
        return triangles;
    }

    public String toStl(String name) {
        final var sb = new StringBuilder();
        sb.append(String.format("solid %s\n", name));
        for (var triangle : triangles) {
            sb.append(triangle.toStl());
        }
        sb.append("endsolid image23d\n");
        return sb.toString();
    }

}

