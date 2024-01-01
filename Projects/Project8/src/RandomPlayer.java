import java.util.ArrayList;

public class RandomPlayer extends AbstractPlayerAlgorithm{

    public RandomPlayer(Graph graph) {
        super(graph);
    }
    @Override
    public Vertex chooseStart() {
        setStartVertex(this.graph.getVertex());
        setCurrentVertex(getStartVertex());
        return getStartVertex();
    }

    @Override
    public Vertex chooseStart(Vertex other) {
        return chooseStart();
    }

    @Override
    public Vertex chooseNext(Vertex otherPlayer) {
        ArrayList<Vertex> choices = new ArrayList<>();
        Vertex currentVertex = getCurrentVertex();
        choices.add(currentVertex);
        for (Edge edge : getCurrentVertex().getOutwardEdges()) {
            Vertex vertex = edge.other(currentVertex);
            choices.add(vertex);
        }
        setCurrentVertex(choices.get(graph.getR().nextInt(0, choices.size())));
        return getCurrentVertex();
    }
}
