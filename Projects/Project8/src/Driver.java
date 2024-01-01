import javax.swing.*;
import java.util.ArrayList;

public class Driver {

    private boolean paused;
    private GraphDisplay display;
    private AbstractPlayerAlgorithm pursuer;
    private AbstractPlayerAlgorithm evader;


    public Driver(int n, double p) throws InterruptedException{
        // Create a random graph on which to play
        // Graph graph = new Graph(n,p,GraphType.UNDIRECTED);
        Graph graph = new Graph("data/in/cg4.txt", "custom");

        // Create the pursuer and evader
        pursuer = new MoveTowardsPlayer(graph);
        evader = new MoveAwayPlayer(graph);

        // Have each player choose a starting location
        pursuer.chooseStart();
        // Since the evader has a harder objective, they get to play second
        // and see where the pursuer chose
        evader.chooseStart(pursuer.getCurrentVertex());

        // Make the display
        display = new GraphDisplay(graph, pursuer, evader, 40, this);
        display.repaint();
        pause();
    }

    /**
     * Advances the simulation by updating agent states.
     * After updating agent states, it checks if any agent has moved.
     * If no agent has moved, the simulation is paused.
     */
    public void advancePursuer() {
        pursuer.chooseNext(evader.getCurrentVertex());
    }

    public void advanceEvader() {
        if (pursuer.getCurrentVertex() != evader.getCurrentVertex()){
            evader.chooseNext(pursuer.getCurrentVertex());
        }
    }

    /**
     * Pauses the Game of Life simulation.
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
        System.out.println("paused");
    }

    /**
     * Resumes the Agent simulation.
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
        int n = 10;
        double p = .3;
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
