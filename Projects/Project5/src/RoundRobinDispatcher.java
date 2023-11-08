/**
 * The RoundRobinDispatcher class represents a job dispatcher
 * that selects Servers in a round-robin fashion to process incoming jobs.
 * Servers are selected sequentially in a cyclic manner.
 * <p>
 * This dispatcher ensures that each Server gets an equal share of jobs over time.
 * It maintains an internal pointer to keep track of the last selected Server.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class RoundRobinDispatcher extends JobDispatcher {

    private int current = -1;

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation disabled by default.
     *
     * @param k number of serves
     * @param showViz whether to show visualization
     */
    public RoundRobinDispatcher(int k, boolean showViz) {
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
    public RoundRobinDispatcher(int k, boolean showViz, boolean simulated) {
        super(k, showViz, simulated);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method selects a Server in a round-robin fashion to process the given job.
     * Servers are selected sequentially and cyclically.
     *
     * @param j The job for which the Server should be selected.
     * @return The next Server in the round-robin sequence to process the job.
     */
    @Override
    public Server pickServer(Job j) {
        current++;
        return getServerList().get(current % serverCount);
    }
}

