import java.util.ArrayList;

/**
 *
 */
public class ShortestQueueDispatcher extends JobDispatcher {

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation disabled by default.
     *
     * @param k
     * @param showViz
     */
    public ShortestQueueDispatcher(int k, boolean showViz) {
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
    public ShortestQueueDispatcher(int k, boolean showViz, boolean simulated) {
        super(k, showViz, simulated);
    }

    @Override
    public Server pickServer(Job j) {
        ArrayList<Server> servers = getServerList();
        Server currentMin = null;
        int currentMinSize = Integer.MAX_VALUE;
        for (Server temp : servers) {
            synchronized (temp.getJobQueue()) {
                int size = temp.getJobQueue().size();
                if (size < currentMinSize) {
                    currentMin = temp;
                    currentMinSize = size;
                }
            }

        }
        return currentMin;
    }
}

