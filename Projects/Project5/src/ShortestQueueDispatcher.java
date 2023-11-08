import java.util.ArrayList;

/**
 * The ShortestQueueDispatcher class represents a job dispatcher
 * that selects Servers with the shortest job queue to process incoming jobs.
 * It ensures that jobs are assigned to Servers with the fewest pending jobs.
 * <p>
 * This dispatcher selects the Server with the smallest queue size, minimizing job wait times.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class ShortestQueueDispatcher extends JobDispatcher {

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation disabled by default.
     *
     * @param k number of serves
     * @param showViz whether to show visualization
     */
    public ShortestQueueDispatcher(int k, boolean showViz) {
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
    public ShortestQueueDispatcher(int k, boolean showViz, boolean simulated) {
        super(k, showViz, simulated);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method selects a Server with the shortest job queue to process the given job.
     * The Server with the smallest queue size is chosen to minimize job wait times.
     *
     * @param j The job for which the Server should be selected.
     * @return The Server with the shortest job queue to process the job.
     */
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

