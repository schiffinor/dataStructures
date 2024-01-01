import java.util.*;

public class Vertex implements Comparable<Vertex> {

    private final HashMap<Vertex, HashMap<Double, Edge>> edgeMap;
    private final HashMap<Edge, Vertex> vertexMap;
    private final HashSet<Edge> incidentEdges;
    private final HashSet<Edge> outwardEdges;
    private Double distanceFromReference;
    private Vertex previousVertex;

    public HashSet<Edge> getOutwardEdges() {
        return outwardEdges;
    }

    public HashSet<Edge> getInwardEdges() {
        return inwardEdges;
    }

    private final HashSet<Edge> inwardEdges;

    private int attachedVertexCount;
    private Set<Vertex> adjacentVertices;
    private String name;
    private String identifier;

    public Vertex() {
        this(null);
    }

    public Vertex(String name) {
        this(name, null);
    }

    public Vertex(String name, String identifier) {
        this.edgeMap = new HashMap<>();
        this.vertexMap = new HashMap<>();
        this.incidentEdges = new HashSet<>();
        this.outwardEdges = new HashSet<>();
        this.inwardEdges = new HashSet<>();
        this.adjacentVertices = new HashSet<>();
        this.name = name;
        this.identifier = identifier;
        this.previousVertex = this;
    }

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

    public HashMap<Vertex, HashMap<Double, Edge>> getEdgeMap() {
        return edgeMap;
    }

    public String getName() {
        return name;
    }

    public Set<Vertex> adjacentVertices() {
        Set<Vertex> adjVert = edgeMap.keySet();
        this.adjacentVertices = adjVert;
        return adjVert;
    }

    public void addEdge(Edge edge) {
        Vertex connected = edge.other(this);
        if (connected == null) throw new IllegalArgumentException("This Edge does not contain this vertex.");
        if (!incidentEdges.add(edge)) throw new IllegalArgumentException("This vertex already contains this edge.");
        if (edge.getDirection() == EdgeType.UNDIRECTED) {
            outwardEdges.add(edge);
            inwardEdges.add(edge);
        } else if (edge.getTailVertex().equals(this)) outwardEdges.add(edge);
        else inwardEdges.add(edge);
        if (!edgeMap.containsKey(connected)) {
            edgeMap.put(connected, new HashMap<>());
            attachedVertexCount++;
        }
        Edge returnEdge = edgeMap.get(connected).put(edge.getDistance(), edge);
        vertexMap.put(edge, connected);
    }


    public void removeEdge(Edge edge) {
        if (!incidentEdges.contains(edge)) return;
        incidentEdges.remove(edge);
        vertexMap.remove(edge);
        Vertex otherVertex = edge.other(this);
        HashMap<Double, Edge> sharedEdges = edgeMap.get(otherVertex);
        if (sharedEdges == null) throw new IllegalArgumentException("Edge incident but not in edgeMap.");
        if (sharedEdges.isEmpty()) throw new IllegalArgumentException("Edge incident but not in sharedEdges.");
        if (sharedEdges.size() == 1) {
            edgeMap.remove(otherVertex);
            attachedVertexCount--;
        }
        else sharedEdges.remove(edge.getDistance(),edge);
    }

    public void disconnect() {
        for (Edge edge : new HashSet<>(incidentEdges)) {
            removeEdge(edge);
        }
    }

    public boolean disconnect(Vertex vertex) {
        if (!edgeMap.containsKey(vertex)) return false;
        Collection<Edge> edgesToVertex = edgeMap.get(vertex).values();
        if (edgesToVertex.isEmpty()) return false;
        for (Edge edge : edgesToVertex) removeEdge(edge);
        return true;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<Edge> getIncidentEdges() {
        return incidentEdges;
    }

    public HashMap<Edge, Vertex> getVertexMap() {
        return vertexMap;
    }

    public String toString() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vertex vert)) return false;
        return Objects.equals(name, vert.getName()) && Objects.equals(identifier, vert.getIdentifier());
    }

    public Double refDistance() {
        return distanceFromReference;
    }

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

    public Vertex getPreviousVertex() {
        return previousVertex;
    }

    public void setPreviousVertex(Vertex previousVertex) {
        this.previousVertex = previousVertex;
    }


    final class VertexSet extends AbstractSet<Vertex> {
        public int size() {
            return attachedVertexCount;
        }
        public void clear() {
            disconnect();
        }
        public Iterator<Vertex> iterator() {
            return edgeMap.keySet().iterator();
        }
        public boolean contains(Object obj) {
            if (!(obj instanceof Vertex vert)) return false;
            return edgeMap.containsKey(vert);
        }
    }
}
