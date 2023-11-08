import java.util.ArrayList;

/**
 * The LeastWorkDispatcher class represents a job dispatcher
 * that selects the Server with the least remaining work in its queue to process incoming jobs.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class LeastWorkDispatcher extends JobDispatcher {

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation disabled by default.
     *
     * @param k number of serves
     * @param showViz whether to show visualization
     */
    public LeastWorkDispatcher(int k, boolean showViz) {
        this(k, showViz, false);
    }

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation toggleable.
     *
     * @param k number of serves
     * @param showViz whether to show visualization
     * @param simulated whether to use simulation
     */
    public LeastWorkDispatcher(int k, boolean showViz, boolean simulated) {
        super(k, showViz, simulated);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method selects the Server with the minimum remaining work in its queue to process the given job.
     *
     * @param j The job for which the Server should be selected.
     * @return The Server with the minimum remaining work to process the job.
     */
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

