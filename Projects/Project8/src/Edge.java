import java.util.List;
import java.util.Objects;

public class Edge {
    private final Vertex tailVertex;
    private final Vertex headVertex;
    private double distance;
    private String name;
    private String identifier;

    public void setDirection(EdgeType direction) {
        this.direction = direction;
    }

    private EdgeType direction;
    public Vertex getTailVertex() {
        return tailVertex;
    }

    public Vertex getHeadVertex() {
        return headVertex;
    }

    public EdgeType getDirection() {
        return direction;
    }


    public Edge(Vertex tailVertex, Vertex headVertex) {
        this(tailVertex, headVertex, 1);
    }

    public Edge(Vertex tailVertex, Vertex headVertex, double distance) {
        this(tailVertex, headVertex, distance, EdgeType.UNDIRECTED);
    }

    public Edge(Vertex tailVertex, Vertex headVertex, double distance, EdgeType direction) {
        this(tailVertex, headVertex, distance, direction, null);
    }

    public Edge(Vertex tailVertex, Vertex headVertex, double distance, EdgeType direction, String name) {
        this(tailVertex, headVertex, distance, direction, name, null);
    }


    public Edge(Vertex tailVertex, Vertex headVertex, double distance, EdgeType direction, String name, String identifier) {
        this.tailVertex = (direction != EdgeType.DIRECTED_INVERTED) ? tailVertex : headVertex;
        this.headVertex = (direction != EdgeType.DIRECTED_INVERTED) ? headVertex : tailVertex;
        this.distance = distance;
        this.direction = (direction == EdgeType.UNDIRECTED) ? direction : EdgeType.DIRECTED_NORMAL;
        this.name = name;
        this.identifier = identifier;
    }

    public Vertex[] vertices() {
        return new Vertex[]{tailVertex, headVertex};
    }

    public Vertex other(Vertex vertex) {
        return (List.of(vertices()).contains(vertex)) ? ((headVertex.equals(vertex)) ? tailVertex : headVertex) : null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String toString() {
        Vertex[] verts =vertices();
        return getName() + ": [" + verts[0] + " -> " + verts[1] + "]; Dist: " + getDistance() + "; Type: " + getDirection();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Edge edge))
            return false;
        EdgeType edgeDirection = edge.getDirection();
        if (direction == EdgeType.UNDIRECTED ||
                edgeDirection == EdgeType.UNDIRECTED)
            return (direction == edgeDirection &&
                    ((headVertex.equals(edge.getHeadVertex()) && tailVertex.equals(edge.getTailVertex())) ||
                            (headVertex.equals(edge.getTailVertex()) && tailVertex.equals(edge.getHeadVertex())))
                    && Objects.equals(edge.getDistance(), this.getDistance())
                    && Objects.equals(edge.getIdentifier(), this.getIdentifier()));
        return (edge.vertices()[0].equals(this.vertices()[1]) && edge.vertices()[1].equals(this.vertices()[0])) &&
                Objects.equals(edge.getDistance(), this.getDistance()) && Objects.equals(edge.getIdentifier(), this.getIdentifier());
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
