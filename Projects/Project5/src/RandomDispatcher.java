/*

 */

import java.util.Random;

/**
 * The RandomDispatcher class represents a job dispatcher that selects a Server randomly to process incoming jobs.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class RandomDispatcher extends JobDispatcher {

    private final Random rand;

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation disabled by default.
     *
     * @param k number of serves
     * @param showViz whether to show visualization
     */
    public RandomDispatcher(int k, boolean showViz) {
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
    public RandomDispatcher(int k, boolean showViz, boolean simulated) {
        super(k, showViz, simulated);
        this.rand = new Random();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method selects a Server randomly to process the given job.
     *
     * @param j The job for which the Server should be selected.
     * @return A randomly selected Server to process the job.
     */
    @Override
    public Server pickServer(Job j) {
        return getServerList().get(rand.nextInt(serverCount));
    }
}
