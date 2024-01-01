import java.util.*;

public class MoveTowardsPlayer extends AbstractPlayerAlgorithm{

    public MoveTowardsPlayer(Graph graph) {
        super(graph);
    }
    @Override
    public Vertex chooseStart() {
        LinkedList<HashSet<Vertex>> connectedComponents = this.getGraph().connectedComponents();
        HashSet<Vertex> set = null;
        int maxSize = 0;
        for (HashSet<Vertex> cc : connectedComponents) {
            if (cc.size() > maxSize) {
                maxSize = cc.size();
                set = cc;
            }
        }
        setStartVertex(this.graph.centroid(Objects.requireNonNull(set)));
        setCurrentVertex(getStartVertex());
        return getStartVertex();
    }

    @Override
    public Vertex chooseStart(Vertex other) {
        setStartVertex(other);
        setCurrentVertex(getStartVertex());
        return getStartVertex();
    }

    @Override
    public Vertex chooseNext(Vertex otherPlayer) {
        HashMap<Vertex,Double> distances = getGraph().distanceFrom(otherPlayer);
        setCurrentVertex(getCurrentVertex().getPreviousVertex());
        return getCurrentVertex();
    }
}
