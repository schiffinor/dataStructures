import javax.swing.*;
import java.util.HashSet;
import java.util.LinkedList;

public class Driver {

    private boolean paused;
    private final AbstractPlayerAlgorithm pursuer;
    private final AbstractPlayerAlgorithm evader;


    /**
     * Constructor I'm sorry I really couldn't make a better implementation of this.
     * I would have wanted to use terminal arguments, but honestly,
     * that's a major time sink and not feasible for me.
     * I spent way too long on the other cooler features.
     * Tweak the graph parameters here and the other things in main-loop.
     *
     * @param n
     * @param p
     * @throws InterruptedException
     */
    public Driver(int n, double p) throws InterruptedException{
        // Create a random graph on which to play
        Graph graph = new Graph(n,p,GraphType.UNDIRECTED);
        // Graph graph = new Graph("data/in/shouldaLost.txt", "custom");
        System.out.println("Graph Data: ");
        System.out.println("Graph - Incidence: ");
        ToroidalDoublyLinkedList<Integer> iM1 = graph.incidenceMatrix();
        System.out.println(iM1);
        System.out.println("Graph - Betti: ");
        System.out.println("Betti_0: " + graph.betti_0());
        System.out.println("Betti_1: " + graph.betti_1());
        LinkedList<HashSet<Vertex>> concom1 = graph.connectedComponents();
        System.out.println("Connected Components: " + concom1);
        for (HashSet<Vertex> cc : concom1) {
            System.out.println(cc + " Centroid: " + graph.centroid(cc));
        }

        // Create the pursuer and evader
        pursuer = new MoveTowardsPlayer(graph);
        evader = new MoveAwayPlayer(graph);

        // Have each player choose a starting location
        pursuer.chooseStart();
        // Since the evader has a harder objective, they get to play second
        // and see where the pursuer chose
        evader.chooseStart(pursuer.getCurrentVertex());

        // Make the display
        GraphDisplay display = new GraphDisplay(graph, pursuer, evader, 40, this);
        display.repaint();
        pause();
    }

    /**
     * Advances the simulation by updating pursuer location.
     */
    public void advancePursuer() {
        pursuer.chooseNext(evader.getCurrentVertex());
    }

    /**
     * Advances the simulation by updating evader location.
     */
    public void advanceEvader() {
        if (pursuer.getCurrentVertex() != evader.getCurrentVertex()){
            evader.chooseNext(pursuer.getCurrentVertex());
        }
    }

    /**
     * Pauses the pursuit simulation.
     * Sets the 'paused' flag tco true and displays a message.
     */
    public void pause() {
        paused = true;
        System.out.println("paused");
    }

    /**
     * Pauses or unpauses the simulation based on the provided boolean value.
     *
     * @param bool `true` to pause the simulation, `false` to unpause it.
     */
    public void setPause(boolean bool) {
        paused = bool;
    }

    /**
     * Resumes the pursuit simulation.
     * Sets the 'paused' flag to false, advances the simulation, and updates the window.
     *
     * @param window the JFrame used for displaying the simulation
     */
    public void play(GraphDisplay window) {
        setPause(false);
        advancePursuer();
        window.repaint();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        window.repaint();
        advanceEvader();
        window.repaint();
        if (pursuer.getCurrentVertex() != evader.getCurrentVertex()) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (!paused) {
                SwingUtilities.invokeLater(() -> play(window));
            }
        }
    }

    public static void main(String[] args) throws InterruptedException{
        int n = 20;
        double p = .15;
        new Driver(n, p);
    }

    public boolean getPaused() {
        return paused;
    }

    public void advance() {
        advancePursuer();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        advanceEvader();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
