import java.util.*;

/**
 * The MoveTowardsPlayer class represents a player algorithm that chooses its moves to minimize
 * distance from another player on a given graph.
 * <p>
 * Again, I wanted to implement a better algorithm.
 * The choose start algorithm is pretty decent, it just chooses the centroid of the set.
 * I would have prefereed to implement this as an optimization problem comparing the quantity
 * of outward edges per vertex to the measure of centrality.
 * However, time ran out.
 * <p>
 * L∞ centrality, the metric we use to define the centroid,
 * is based on minimizing the maximum distance between a vertex and all others.
 * On a hyper-graph or topological surface, this metric is more consistent.
 * However, in a sparse graph like this, it can be less than ideal,
 * as distance is based on arbitrary metrics and can be misleading.
 * Especially considering distance between adjacent points is irrelevant to graph traversal.
 * If the pursuer and evader had movement points they could expend this metric would be more useful.
 * If we were able to account for the amount of outward edges as think ahead this would be considerably more powerful.
 */
public class MoveTowardsPlayer extends AbstractPlayerAlgorithm{

    /**
     * Constructs a MoveTowardsPlayer with the specified graph.
     *
     * @param graph The graph on which the player operates.
     */
    public MoveTowardsPlayer(Graph graph) {
        super(graph);
    }

    /**
     * Chooses the starting vertex for the player based on minimizing the size of connected components
     * and moving towards the centroid of the largest connected component.
     *
     * @return The chosen starting vertex.
     */
    @Override
    public Vertex chooseStart() {
        LinkedList<HashSet<Vertex>> connectedComponents = this.getGraph().connectedComponents();
        HashSet<Vertex> set = null;
        int maxSize = 0;
        // Iterate through connected components to find the largest one
        for (HashSet<Vertex> cc : connectedComponents) {
            if (cc.size() > maxSize) {
                maxSize = cc.size();
                set = cc;
            }
        }
        // Set the start vertex to the centroid of the largest connected component
        setStartVertex(this.graph.centroid(Objects.requireNonNull(set)));
        setCurrentVertex(getStartVertex());
        return getStartVertex();
    }

    /**
     * Chooses the starting vertex for the player as the specified other player's vertex.
     *
     * @param other The vertex of the other player.
     */
    @Override
    public void chooseStart(Vertex other) {
        // Set the start vertex to the specified other player's vertex
        setStartVertex(other);
        setCurrentVertex(getStartVertex());
    }

    /**
     * Chooses the next vertex for the player by moving towards the previous vertex,
     * effectively retracing the steps to minimize distance from another player's vertex.
     *
     * @param otherPlayer The vertex of the other player.
     */
    @Override
    public void chooseNext(Vertex otherPlayer) {
        // No computation is done to move towards the other player; simply move to the previous vertex
        HashMap<Vertex,Double> distances = getGraph().distanceFrom(otherPlayer);
        setCurrentVertex(getCurrentVertex().getPreviousVertex());
    }
}
