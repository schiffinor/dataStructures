import java.util.*;

/**
 * The MoveAwayPlayer class represents a player algorithm that chooses its moves to maximize
 * distance from another player on a given graph.
 * <p>
 * I wanted to implement a better algorithm.
 * However, also ran out of time and brain power.
 * The choose start algorithm is better than pretty much anything else I could think of on the fly.
 * It actually makes data collection super annoying over largely disconnected graphs, as it chooses
 * the solution that makes it impossible for the pursuer to win most of the time.
 * <p>
 * It always chooses singleton graphs if they exist.
 * On directed graphs it will always choose points with no inward edges.
 * Otherwise, it will make sure to choose, if possible, edges not equal to or adjacent the pursuer.
 */
public class MoveAwayPlayer extends AbstractPlayerAlgorithm{

    /**
     * Constructs a MoveAwayPlayer with the specified graph.
     *
     * @param graph The graph on which the player operates.
     */
    public MoveAwayPlayer(Graph graph) {
        super(graph);
    }

    /**
     * Chooses the starting vertex for the player based on maximizing the size of connected components.
     *
     * @return The chosen starting vertex.
     */
    @Override
    public Vertex chooseStart() {setStartVertex(this.graph.getVertex());
        LinkedList<HashSet<Vertex>> connectedComponents = this.getGraph().connectedComponents();
        HashSet<Vertex> set = null;
        int maxSize = 0;
        // Iterate through connected components to find the largest one
        for (HashSet<Vertex> cc : connectedComponents) {
            if (cc.size() > maxSize) {
                maxSize = cc.size();
                set = cc;
            }
            // If a component has only one vertex, set it as the start vertex
            if (cc.size() == 1) {
                setStartVertex((Vertex) cc.toArray()[0]);
                setCurrentVertex(getStartVertex());
                return getStartVertex();
            }
        }
        Vertex start = null;
        int maxConn = 0;
        // Iterate through vertices in the largest connected component
        for (Vertex v : Objects.requireNonNull(set)) {
            if (v.getOutwardEdges().size() > maxConn) {
                maxConn = v.getOutwardEdges().size();
                start = v;
            }
            // If a vertex has no inward edges, set it as the start vertex
            if (v.getInwardEdges().isEmpty()) {
                setStartVertex(v);
                setCurrentVertex(getStartVertex());
                return getStartVertex();
            }
        }
        // Set the start vertex to the chosen vertex
        setStartVertex(start);
        setCurrentVertex(getStartVertex());
        return getStartVertex();
    }

    /**
     * Chooses the starting vertex for the player based on maximizing the size of connected components
     * and avoiding a specified other player's vertex.
     *
     * @param other The vertex of the other player to avoid.
     */
    @Override
    public void chooseStart(Vertex other) {
        LinkedList<HashSet<Vertex>> connectedComponents = this.getGraph().connectedComponents();
        HashSet<Vertex> set = null;
        int maxSize = 0;
        // Iterate through connected components to find the largest one
        for (HashSet<Vertex> cc : connectedComponents) {
            if (cc.size() > maxSize) {
                maxSize = cc.size();
                set = cc;
            }
            // If a component has only one vertex, set it as the start vertex
            if (cc.size() == 1) {
                setStartVertex((Vertex) cc.toArray()[0]);
                setCurrentVertex(getStartVertex());
                return;
            }
        }
        Vertex start = null;
        int maxConn = 0;
        // Iterate through vertices in the largest connected component
        for (Vertex v : Objects.requireNonNull(set)) {
            // Choose the vertex with the maximum outward edges, avoiding the specified other player's vertex
            if (v.getOutwardEdges().size() > maxConn && !v.equals(other) && !v.adjacentVertices().contains(other)) {
                maxConn = v.getOutwardEdges().size();
                start = v;
            }
            // If a vertex has no inward edges, set it as the start vertex
            if (v.getInwardEdges().isEmpty()) {
                setStartVertex(v);
                setCurrentVertex(getStartVertex());
                return;
            }
        }
        // If no suitable vertex is found, choose a random non-adjacent vertex to the other player
        if (start == null) {
            HashSet<Vertex> nonVisited = new HashSet<>((Collection<Vertex>) getGraph().getVertices());
            do {
                start = graph.getVertex();
                nonVisited.remove(start);
            } while ((other.adjacentVertices().contains(start) || start.equals(other)) && !nonVisited.isEmpty());
        }
        // If still no suitable vertex is found, choose a random non-adjacent vertex to the other player
        if (start == null || other.adjacentVertices().contains(start) || start.equals(other)) {
            HashSet<Vertex> nonVisited = new HashSet<>((Collection<Vertex>) getGraph().getVertices());
            do {
                start = graph.getVertex();
                nonVisited.remove(start);
            } while (start.equals(other) && !nonVisited.isEmpty());
            if (start.equals(other)) {
                start = graph.getVertex();
            }
        }
        // Set the start vertex to the chosen vertex
        setStartVertex(start);
        setCurrentVertex(getStartVertex());
    }

    /**
     * Chooses the next vertex for the player based on maximizing distance from another player's vertex.
     *
     * @param otherPlayer The vertex of the other player.
     */
    @Override
    public void chooseNext(Vertex otherPlayer) {
        // Calculate distances from the other player's vertex
        HashMap<Vertex,Double> distances = getGraph().distanceFrom(otherPlayer);
        Vertex next = null;
        Double maxDist = 0.0;
        // Iterate through outward edges of the current vertex to find the maximum distance
        for (Edge edge : getCurrentVertex().getOutwardEdges()) {
            Vertex v = edge.other(getCurrentVertex());
            if (distances.get(v) > maxDist) {
                maxDist = distances.get(v);
                next = v;
            }
        }
        // If the distance from the current vertex is greater than the maximum distance, choose the current vertex
        if (distances.get(getCurrentVertex()) > maxDist) {
            maxDist = distances.get(getCurrentVertex());
            next = getCurrentVertex();
        }
        // Set the current vertex to the chosen next vertex
        setCurrentVertex(next);
    }
}
