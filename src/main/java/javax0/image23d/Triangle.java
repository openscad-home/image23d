package javax0.image23d;

/**
 * A single three-dimensional triangle.
 */
public record Triangle(Point a, Point b, Point c) {

    public String toStl() {
        final var sb = new StringBuilder();
        sb.append("facet normal 0 0 0\n");
        sb.append("outer loop\n");
        sb.append(a.toStl());
        sb.append(b.toStl());
        sb.append(c.toStl());
        sb.append("endloop\n");
        sb.append("endfacet\n");
        return sb.toString();
    }

}
