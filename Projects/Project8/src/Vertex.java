import java.util.*;

/**
 * Represents a vertex in a graph with weighted edges.
 * Vertices can be connected to other vertices through edges.
 * Implements the Comparable interface to support comparisons based on reference distances.
 *
 * <p>
 * This class provides a versatile representation of a vertex in a graph.
 * It supports connections to other vertices through edges and allows users to manipulate
 * the incident edges, outward edges, and inward edges.
 * The class also provides methods for adding and removing edges,
 * disconnecting from other vertices, and accessing information about the vertex,
 * such as its name, identifier, and attached vertex count.
 *
 * <p>
 * The implementation includes methods for traversing to adjacent vertices, getting
 * the edge to a specified vertex, and checking equality with another vertex.
 * The class is designed to be user-friendly and offers flexibility in managing
 * graph structures.
 *
 * <p>
 * Usage example:
 *
 * <pre>
 * {@code
 * Vertex v1 = new Vertex("A");
 * Vertex v2 = new Vertex("B");
 * Edge e = new Edge(v1, v2, 5.0);
 * v1.addEdge(e);
 * }
 * </pre>
 *
 * <p>
 * Note: The class also implements the Comparable interface, allowing users to compare
 * vertices based on their reference distances.
 * This feature is particularly useful in algorithms that involve priority queues.
 *
 * <p>
 * @author Roman Schiffino &lt;rjschi24@colby.edu&gt;
 * @version 1.0
 * @since 1.0
 */

public class Vertex implements Comparable<Vertex> {

    private final HashMap<Vertex, HashMap<Double, Edge>> edgeMap;
    private final HashMap<Edge, Vertex> vertexMap;
    private final HashSet<Edge> incidentEdges;
    private final HashSet<Edge> outwardEdges;
    private final HashSet<Edge> inwardEdges;
    private Double distanceFromReference;
    private Vertex previousVertex;
    private int attachedVertexCount;
    private String name;
    private String identifier;

    /**
     * Constructs a Vertex with default parameters.
     * Initializes the vertex with a null name and null identifier.
     * This constructor is primarily used for creating a default vertex.
     *
     * @since 1.0
     */
    public Vertex() {
        this(null);
    }

    /**
     * Constructs a Vertex with the specified name.
     * Initializes the vertex with the given name and a null identifier.
     * This constructor is useful when creating a vertex with a specified name.
     *
     * @param name the name of the vertex.
     * @since 1.0
     */
    public Vertex(String name) {
        this(name, null);
    }

    /**
     * Constructs a Vertex with the specified name and identifier.
     * Initializes the vertex with the given name, identifier,
     * and default values for other properties.
     * This constructor is suitable for creating a fully customized vertex with name and identifier.
     * Identifiers are used to seperate and make unequal teo vertices of the same name.
     *
     * @param name       the name of the vertex.
     * @param identifier the identifier of the vertex.
     * @since 1.0
     */
    public Vertex(String name, String identifier) {
        this.edgeMap = new HashMap<>();
        this.vertexMap = new HashMap<>();
        this.incidentEdges = new HashSet<>();
        this.outwardEdges = new HashSet<>();
        this.inwardEdges = new HashSet<>();
        this.name = name;
        this.identifier = identifier;
        this.previousVertex = this;
    }

    /**
     * Retrieves the set of outward edges from this vertex.
     *
     * @return a HashSet containing the outward edges from this vertex.
     * @since 1.0
     */
    public HashSet<Edge> getOutwardEdges() {
        return outwardEdges;
    }

    /**
     * Retrieves the set of inward edges to this vertex.
     *
     * @return a HashSet containing the inward edges to this vertex.
     * @since 1.0
     */
    public HashSet<Edge> getInwardEdges() {
        return inwardEdges;
    }

    /**
     * Retrieves the edge connecting this vertex to the specified vertex.
     *
     * @param vertex the target vertex.
     * @return the edge connecting this vertex to the specified vertex, or null if no such edge exists.
     * @since 1.0
     */
    public Edge getEdgeTo(Vertex vertex) {
        HashMap<Double, Edge> edges = edgeMap.get(vertex);
        if (edges == null || edges.isEmpty()) return null;
        if (edges.size() == 1) return edges.get(edges.keySet().iterator().next());
        Double returnKey = null;
        try {
            returnKey = edges.keySet().stream().min(Comparator.comparingDouble(Math::abs)).get();
        } catch (NoSuchElementException exception) {
            return null;
        }
        return edges.get(returnKey);
    }

    /**
     * Retrieves the edge map associated with this vertex.
     *
     * @return a HashMap representing the edge map of this vertex.
     * @since 1.0
     */
    public HashMap<Vertex, HashMap<Double, Edge>> getEdgeMap() {
        return edgeMap;
    }

    /**
     * Retrieves the name of this vertex.
     *
     * @return the name of this vertex.
     * @since 1.0
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this vertex.
     *
     * @param name the new name for this vertex.
     * @since 1.0
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the set of adjacent vertices to this vertex.
     *
     * @return a Set containing the adjacent vertices to this vertex.
     * @since 1.0
     */
    public Set<Vertex> adjacentVertices() {
        return edgeMap.keySet();
    }

    /**
     Adds the specified edge to this vertex, updating incident, outward, and inward edges.
     * If the edge is undirected, it is added to both outward and inward edges.
     * If the edge is directed, it is added to either outward or inward edges based on the direction.
     * The edge is also added to the edgeMap, associating it with the connected vertex.
     * If the connected vertex is not already in the edgeMap, a new entry is created for it.
     *
     * @param edge the edge to add.
     * @throws IllegalArgumentException if the specified edge does not contain this vertex or
     *                                  if this vertex already contains the specified edge.
     * @since 1.0
     */
    public void addEdge(Edge edge) {
        // Determine the vertex connected to this edge
        Vertex connected = edge.other(this);

        // Validate the edge and ensure it is not already in the incident edges set
        if (connected == null) throw new IllegalArgumentException("This Edge does not contain this vertex.");
        if (!incidentEdges.add(edge)) throw new IllegalArgumentException("This vertex already contains this edge.");

        // Update outward and inward edges based on the edge's direction
        if (edge.getDirection() == EdgeType.UNDIRECTED) {
            outwardEdges.add(edge);
            inwardEdges.add(edge);
        } else if (edge.getTailVertex().equals(this)) outwardEdges.add(edge);
        else inwardEdges.add(edge);

        // Update edgeMap to associate the edge with the connected vertex
        if (!edgeMap.containsKey(connected)) {
            edgeMap.put(connected, new HashMap<>());
            attachedVertexCount++;
        }
        // Add the edge to the edgeMap, using its distance as the key
        edgeMap.get(connected).put(edge.getDistance(), edge);
        // Update the vertexMap to map the edge to the connected vertex
        vertexMap.put(edge, connected);
    }

    /**
     * Removes the specified edge from this vertex, updating incident, outward, and inward edges.
     * The edge is also removed from the edgeMap, disconnecting it from the connected vertex.
     * If the connected vertex has no more incident edges, its entry is removed from the edgeMap.
     *
     * @param edge the edge to remove.
     * @since 1.0
     */
    public void removeEdge(Edge edge) {
        // Check if the edge is in the incident edges set
        if (!incidentEdges.contains(edge)) return;

        // Remove the edge from the incident edges set and the vertexMap
        incidentEdges.remove(edge);
        vertexMap.remove(edge);

        // Determine the connected vertex
        Vertex otherVertex = edge.other(this);
        // Retrieve the set of shared edges with the connected vertex from the edgeMap
        HashMap<Double, Edge> sharedEdges = edgeMap.get(otherVertex);

        // Validate sharedEdges
        if (sharedEdges == null) throw new IllegalArgumentException("Edge incident but not in edgeMap.");
        if (sharedEdges.isEmpty()) throw new IllegalArgumentException("Edge incident but not in sharedEdges.");

        // If the sharedEdges set has only one edge, remove the connected vertex entry from the edgeMap
        if (sharedEdges.size() == 1) {
            edgeMap.remove(otherVertex);
            attachedVertexCount--;
        } else {
            // Remove the edge from the sharedEdges set using its distance as the key
            sharedEdges.remove(edge.getDistance(), edge);
        }
    }

    /**
     * Disconnects this vertex from all incident edges.
     *
     * @since 1.0
     */
    public void disconnect() {
        for (Edge edge : new HashSet<>(incidentEdges)) {
            removeEdge(edge);
        }
    }

    /**
     * Disconnects this vertex from the specified vertex by removing all incident edges
     * between them. If the specified vertex is not connected to this vertex, no action is taken.
     *
     * @param vertex the vertex to disconnect from.
     * @return {@code true} if the disconnection is successful; {@code false} otherwise.
     * @since 1.0
     */
    public boolean disconnect(Vertex vertex) {
        if (!edgeMap.containsKey(vertex)) return false;
        Collection<Edge> edgesToVertex = edgeMap.get(vertex).values();
        if (edgesToVertex.isEmpty()) return false;
        for (Edge edge : edgesToVertex) removeEdge(edge);
        return true;
    }

    /**
     * Returns the set of incident edges connected to this vertex.
     *
     * @return the set of incident edges connected to this vertex.
     * @since 1.0
     */
    public HashSet<Edge> getIncidentEdges() {
        return incidentEdges;
    }

    /**
     * Returns the mapping of edges to their connected vertices.
     *
     * @return the mapping of edges to their connected vertices.
     * @since 1.0
     */
    public HashMap<Edge, Vertex> getVertexMap() {
        return vertexMap;
    }

    /**
     * Returns a string representation of this vertex, which is its name.
     *
     * @return a string representation of this vertex.
     * @since 1.0
     */
    public String toString() {
        return name;
    }

    /**
     * Returns the identifier of this vertex.
     *
     * @return the identifier of this vertex.
     * @since 1.0
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier of this vertex.
     *
     * @param identifier the new identifier to set.
     * @since 1.0
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Indicates whether some other object is "equal to" this vertex.
     * Two vertices are considered equal if they have the same name and identifier.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this vertex is equal to the specified object;
     *         {@code false} otherwise.
     * @since 1.0
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Vertex vert)) return false;
        return Objects.equals(name, vert.getName()) && Objects.equals(identifier, vert.getIdentifier());
    }

    /**
     * Returns the reference distance of this vertex, which is used in comparisons.
     *
     * @return the reference distance of this vertex.
     * @since 1.0
     */
    public Double refDistance() {
        return distanceFromReference;
    }

    /**
     * Sets the reference distance of this vertex, used in comparisons.
     *
     * @param distanceFromReference the new reference distance to set.
     * @since 1.0
     */
    public void setRefDistance(Double distanceFromReference) {
        this.distanceFromReference = distanceFromReference;
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     */
    @Override
    public int compareTo(Vertex o) {
        return distanceFromReference.compareTo(o.refDistance());
    }

    /**
     * Returns the previous vertex connected to this vertex.
     *
     * @return the previous vertex connected to this vertex.
     * @since 1.0
     */
    public Vertex getPreviousVertex() {
        return previousVertex;
    }

    /**
     * Sets the previous vertex connected to this vertex.
     *
     * @param previousVertex the new previous vertex to set.
     * @since 1.0
     */
    public void setPreviousVertex(Vertex previousVertex) {
        this.previousVertex = previousVertex;
    }

    /**
     * Returns the count of attached vertices connected to this vertex.
     *
     * @return the count of attached vertices connected to this vertex.
     * @since 1.0
     */
    public int getAttachedVertexCount() {
        return attachedVertexCount;
    }
}
