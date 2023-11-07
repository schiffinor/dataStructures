/**
 *
 */
public class RoundRobinDispatcher extends JobDispatcher {

    private int current = -1;

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation disabled by default.
     *
     * @param k
     * @param showViz
     */
    public RoundRobinDispatcher(int k, boolean showViz) {
        this(k, showViz, false);
    }

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation toggleable.
     *
     * @param k
     * @param showViz
     * @param simulated
     */
    public RoundRobinDispatcher(int k, boolean showViz, boolean simulated) {
        super(k, showViz, simulated);
    }

    @Override
    public Server pickServer(Job j) {
        current++;
        return getServerList().get(current % serverCount);
    }
}

