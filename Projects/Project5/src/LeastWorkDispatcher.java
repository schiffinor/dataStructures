import java.util.ArrayList;

/**
 *
 */
public class LeastWorkDispatcher extends JobDispatcher {

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation disabled by default.
     *
     * @param k
     * @param showViz
     */
    public LeastWorkDispatcher(int k, boolean showViz) {
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
    public LeastWorkDispatcher(int k, boolean showViz, boolean simulated) {
        super(k, showViz, simulated);
    }

    @Override
    public Server pickServer(Job j) {
        ArrayList<Server> servers = getServerList();
        Server currentMin = null;
        double currentMinWork = Double.MAX_VALUE;
        for (Server temp : servers) {
            double work = temp.remainingWorkInQueueShrimple();
            if (work < currentMinWork) {
                currentMin = temp;
                currentMinWork = work;
            }
        }
        return currentMin;
    }
}

