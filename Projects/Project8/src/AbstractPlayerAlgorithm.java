public abstract class AbstractPlayerAlgorithm {
    final Graph graph;
    Vertex currentVertex;
    Vertex startVertex;

    public AbstractPlayerAlgorithm(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    public Vertex getCurrentVertex() {
        return currentVertex;
    }

    public void setCurrentVertex(Vertex currentVertex) {
        this.currentVertex = currentVertex;
    }

    public Vertex getStartVertex() {
        return startVertex;
    }

    public void setStartVertex(Vertex startVertex) {
        this.startVertex = startVertex;
    }

    public abstract Vertex chooseStart();
    public abstract Vertex chooseStart(Vertex other);
    public abstract Vertex chooseNext(Vertex otherPlayer);
}
