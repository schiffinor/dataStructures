import java.util.*;

public class MoveAwayPlayer extends AbstractPlayerAlgorithm{

    public MoveAwayPlayer(Graph graph) {
        super(graph);
    }
    @Override
    public Vertex chooseStart() {setStartVertex(this.graph.getVertex());
        LinkedList<HashSet<Vertex>> connectedComponents = this.getGraph().connectedComponents();
        HashSet<Vertex> set = null;
        int maxSize = 0;
        for (HashSet<Vertex> cc : connectedComponents) {
            if (cc.size() > maxSize) {
                maxSize = cc.size();
                set = cc;
            }
            if (cc.size() == 1) {
                setStartVertex((Vertex) cc.toArray()[0]);
                setCurrentVertex(getStartVertex());
                return getStartVertex();
            }
        }
        Vertex start = null;
        int maxConn = 0;
        for (Vertex v : Objects.requireNonNull(set)) {
            if (v.getOutwardEdges().size() > maxSize) {
                maxConn = v.getOutwardEdges().size();
                start = v;
            }
            if (v.getInwardEdges().isEmpty()) {
                setStartVertex(v);
                setCurrentVertex(getStartVertex());
                return getStartVertex();
            }
        }
        setStartVertex(start);
        setCurrentVertex(getStartVertex());
        return getStartVertex();
    }

    @Override
    public void chooseStart(Vertex other) {
        LinkedList<HashSet<Vertex>> connectedComponents = this.getGraph().connectedComponents();
        HashSet<Vertex> set = null;
        int maxSize = 0;
        for (HashSet<Vertex> cc : connectedComponents) {
            if (cc.size() > maxSize) {
                maxSize = cc.size();
                set = cc;
            }
            if (cc.size() == 1) {
                setStartVertex((Vertex) cc.toArray()[0]);
                setCurrentVertex(getStartVertex());
                return;
            }
        }
        Vertex start = null;
        int maxConn = 0;
        for (Vertex v : Objects.requireNonNull(set)) {
            if (v.getOutwardEdges().size() > maxSize && !v.equals(other) && !v.adjacentVertices().contains(other)) {
                maxConn = v.getOutwardEdges().size();
                start = v;
            }
            if (v.getInwardEdges().isEmpty()) {
                setStartVertex(v);
                setCurrentVertex(getStartVertex());
                return;
            }
        }
        if (start == null) {
            do {
                start = graph.getVertex();
            } while (other.adjacentVertices().contains(start) || start.equals(other));
        }
        setStartVertex(start);
        setCurrentVertex(getStartVertex());
    }

    @Override
    public void chooseNext(Vertex otherPlayer) {
        HashMap<Vertex,Double> distances = getGraph().distanceFrom(otherPlayer);
        Vertex next = null;
        Double maxDist = 0.0;
        for (Edge edge : getCurrentVertex().getOutwardEdges()) {
            Vertex v = edge.other(getCurrentVertex());
            if (distances.get(v) > maxDist) {
                maxDist = distances.get(v);
                next = v;
            }
        }
        if (distances.get(getCurrentVertex()) > maxDist) {
            maxDist = distances.get(getCurrentVertex());
            next = getCurrentVertex();
        }
        setCurrentVertex(next);
    }
}
