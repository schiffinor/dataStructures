/*

 */
import java.util.Random;

/**
 *
 */
public class RandomDispatcher extends JobDispatcher{

    private final Random rand;

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation disabled by default.
     *
     * @param k
     * @param showViz
     */
    public RandomDispatcher(int k, boolean showViz) {
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
    public RandomDispatcher(int k, boolean showViz, boolean simulated) {
        super(k, showViz, simulated);
        this.rand = new Random();
    }

    @Override
    public Server pickServer(Job j) {
        return getServerList().get(rand.nextInt(serverCount));
    }
}
