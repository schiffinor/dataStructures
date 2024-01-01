import java.util.List;
import java.util.Objects;

/**
 * Represents an edge connecting two vertices in a graph.
 * <p>
 * This class defines the properties and behavior of an edge, including the tail and head vertices,
 * distance, direction, name, and identifier. It provides methods for retrieving and setting various
 * attributes of the edge, such as vertices, direction, name, distance, and identifier. The class
 * also includes methods for determining the other vertex connected to a given vertex and comparing
 * the equality of two edges.
 *
 * <p>
 * The edge is directional and can be either directed or undirected. The distance represents the
 * weight or cost associated with the edge, and the name and identifier provide optional labels for
 * the edge.
 *
 * <p>
 * Usage example:
 *
 * <pre>
 * {@code
 * Vertex vertexA = new Vertex("A");
 * Vertex vertexB = new Vertex("B");
 * Edge edgeAB = new Edge(vertexA, vertexB, 5, EdgeType.DIRECTED_NORMAL, "AB", "edge1");
 * }
 * </pre>
 *
 * <p>
 * Note: The Edge class assumes that the vertices provided during instantiation are distinct.
 *
 * <p>
 *
 * @author Roman Schiffino &lt;rjschi24@colby.edu&gt;
 * @version 1.0
 * @since 1.0
 */
public class Edge {
    private final Vertex tailVertex;
    private final Vertex headVertex;
    private double distance;
    private String name;
    private String identifier;
    private EdgeType direction;

    /**
     * Constructs an undirected edge with a default distance of 1.
     * <p>
     * This constructor creates an undirected edge between the specified tail and head vertices
     * with a default distance of 1. The edge has no specified direction, name, or identifier.
     *
     * @param tailVertex the tail vertex of the edge.
     * @param headVertex the head vertex of the edge.
     */
    public Edge(Vertex tailVertex, Vertex headVertex) {
        this(tailVertex, headVertex, 1);
    }

    /**
     * Constructs an undirected edge with the specified distance.
     * <p>
     * This constructor creates an undirected edge between the specified tail and head vertices
     * with the given distance. The edge has no specified direction, name, or identifier.
     *
     * @param tailVertex the tail vertex of the edge.
     * @param headVertex the head vertex of the edge.
     * @param distance   the distance or weight associated with the edge.
     */
    public Edge(Vertex tailVertex, Vertex headVertex, double distance) {
        this(tailVertex, headVertex, distance, EdgeType.UNDIRECTED);
    }

    /**
     * Constructs an edge with the specified distance and direction.
     * <p>
     * This constructor creates an edge between the specified tail and head vertices with the given
     * distance and direction. The edge can be directed or undirected. It has no specified name or identifier.
     *
     * @param tailVertex the tail vertex of the edge.
     * @param headVertex the head vertex of the edge.
     * @param distance   the distance or weight associated with the edge.
     * @param direction  the direction of the edge (directed, undirected, or inverted).
     */
    public Edge(Vertex tailVertex, Vertex headVertex, double distance, EdgeType direction) {
        this(tailVertex, headVertex, distance, direction, null);
    }

    /**
     * Constructs an edge with the specified distance, direction, and name.
     * <p>
     * This constructor creates an edge between the specified tail and head vertices with the given
     * distance, direction, and name.
     * The edge can be directed or undirected.
     * It has no specified identifier.
     *
     * @param tailVertex the tail vertex of the edge.
     * @param headVertex the head vertex of the edge.
     * @param distance   the distance or weight associated with the edge.
     * @param direction  the direction of the edge (directed, undirected, or inverted).
     * @param name       the name or label of the edge.
     */
    public Edge(Vertex tailVertex, Vertex headVertex, double distance, EdgeType direction, String name) {
        this(tailVertex, headVertex, distance, direction, name, null);
    }

    /**
     * Constructs an edge with the specified attributes.
     * <p>
     * This constructor creates an edge between the specified tail and head vertices with the given
     * distance, direction, name, and identifier.
     * The edge can be directed or undirected.
     *
     * @param tailVertex the tail vertex of the edge.
     * @param headVertex the head vertex of the edge.
     * @param distance   the distance or weight associated with the edge.
     * @param direction  the direction of the edge (directed, undirected, or inverted).
     * @param name       the name or label of the edge.
     * @param identifier the unique identifier of the edge.
     */
    public Edge(Vertex tailVertex, Vertex headVertex, double distance, EdgeType direction, String name, String identifier) {
        this.tailVertex = (direction != EdgeType.DIRECTED_INVERTED) ? tailVertex : headVertex;
        this.headVertex = (direction != EdgeType.DIRECTED_INVERTED) ? headVertex : tailVertex;
        this.distance = distance;
        this.direction = (direction == EdgeType.UNDIRECTED) ? direction : EdgeType.DIRECTED_NORMAL;
        this.name = name;
        this.identifier = identifier;
    }

    /**
     * Gets the tail vertex of the edge.
     *
     * @return the tail vertex of the edge.
     */
    public Vertex getTailVertex() {
        return tailVertex;
    }

    /**
     * Gets the head vertex of the edge.
     *
     * @return the head vertex of the edge.
     */
    public Vertex getHeadVertex() {
        return headVertex;
    }

    /**
     * Gets the direction of the edge.
     *
     * @return the direction of the edge (directed, undirected, or inverted).
     */
    public EdgeType getDirection() {
        return direction;
    }

    /**
     * Sets the direction of the edge.
     *
     * @param direction the new direction of the edge (directed, undirected, or inverted).
     */
    public void setDirection(EdgeType direction) {
        this.direction = direction;
    }

    /**
     * Gets an array containing the tail and head vertices of the edge.
     *
     * @return an array containing the tail and head vertices of the edge.
     */
    public Vertex[] vertices() {
        return new Vertex[]{tailVertex, headVertex};
    }

    /**
     * Gets the other vertex connected to the specified vertex.
     *
     * @param vertex the reference vertex.
     * @return the other vertex connected to the specified vertex, or null if the vertex is not connected.
     */
    public Vertex other(Vertex vertex) {
        return (List.of(vertices()).contains(vertex)) ? ((headVertex.equals(vertex)) ? tailVertex : headVertex) : null;
    }

    /**
     * Gets the name or label of the edge.
     *
     * @return the name or label of the edge.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name or label of the edge.
     *
     * @param name the new name or label of the edge.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the distance or weight associated with the edge.
     *
     * @return the distance or weight associated with the edge.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Sets the distance or weight associated with the edge.
     *
     * @param distance the new distance or weight associated with the edge.
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Returns a string representation of the edge.
     *
     * @return a string representation of the edge, including name, vertices, distance, and direction.
     */
    public String toString() {
        Vertex[] verts = vertices();
        return getName() + ": [" + verts[0] + " -> " + verts[1] + "]; Dist: " + getDistance() + "; Type: " + getDirection();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * This method implements the equality comparison for the {@code Edge} class.
     * Two edges are considered equal if they have the same direction, vertices, distance, and identifier.
     * For undirected edges, the order of vertices does not affect equality.
     * </p>
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is equal to the {@code obj} argument; {@code false} otherwise.
     */
    public boolean equals(Object obj) {
        // Check if object is Edge
        if (!(obj instanceof Edge edge))
            return false;

        EdgeType edgeDirection = edge.getDirection();

        // Equality for undirected Edges
        if (direction == EdgeType.UNDIRECTED ||
                edgeDirection == EdgeType.UNDIRECTED)
            return (direction == edgeDirection &&
                    ((headVertex.equals(edge.getHeadVertex()) && tailVertex.equals(edge.getTailVertex())) ||
                            (headVertex.equals(edge.getTailVertex()) && tailVertex.equals(edge.getHeadVertex())))
                    && Objects.equals(edge.getDistance(), this.getDistance())
                    && Objects.equals(edge.getIdentifier(), this.getIdentifier()));

        // Equality for directed Edges
        return (edge.vertices()[0].equals(this.vertices()[1]) && edge.vertices()[1].equals(this.vertices()[0])) &&
                Objects.equals(edge.getDistance(), this.getDistance()) && Objects.equals(edge.getIdentifier(), this.getIdentifier());
    }

    /**
     * Retrieves the identifier associated with this edge.
     * <p>
     * The identifier is a string used to uniquely identify the edge.
     * </p>
     *
     * @return the identifier of this edge.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier for this edge.
     * <p>
     * The identifier is a string used to uniquely identify the edge.
     * </p>
     *
     * @param identifier the new identifier to be set.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
