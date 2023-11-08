import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * TimeCheck class is responsible for tracking and managing time-related operations,
 * including registering alerts for specific times and providing time data to subscribers.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class TimeCheck {
    private final LinkedList<Server> subscribers;
    private final TreeMap<Double, JobDispatcher> alerts;
    private final HashMap<Server, Long> subscriberStarts;
    private final JobDispatcher parent;
    private long startTime;
    private boolean updaterEnabled;
    private Map.Entry<Double, JobDispatcher> currentAlert;

    /**
     * Constructs a new TimeCheck instance without a parent JobDispatcher.
     */
    public TimeCheck() {
        this(null);
    }

    /**
     * Constructs a new TimeCheck instance with an optional parent JobDispatcher.
     *
     * @param parent The parent JobDispatcher associated with the TimeCheck.
     */
    public TimeCheck(JobDispatcher parent) {
        this.parent = parent;
        this.subscribers = new LinkedList<>();
        this.alerts = new TreeMap<>();
        this.subscriberStarts = new HashMap<>();
        this.updaterEnabled = false;
    }

    /**
     * Starts the timer and records the current system time.
     */
    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    /**
     * Registers an alert for a specific time, associated with a JobDispatcher.
     *
     * @param time       The time at which the alert should trigger.
     * @param dispatcher The associated JobDispatcher to handle the alert.
     */
    public void registerAlert(double time, JobDispatcher dispatcher) {
        alerts.put(time, dispatcher);
    }

    /**
     * Moves to the next scheduled alert in the queue.
     */
    public void nextAlert() {
        currentAlert = alerts.pollFirstEntry();
    }

    /**
     * Starts tracking time for a specific server by adding it to the list of subscribers.
     *
     * @param server The server to start tracking time for.
     * @return {@code true} if the server is successfully added as a subscriber, {@code false} if already subscribed.
     */
    public boolean start(Server server) {
        if (subscribers.contains(server)) return false;
        subscribers.add(server);
        subscriberStarts.put(server, System.nanoTime());
        return true;
    }


    /**
     * Stops tracking time for a specific server by removing it from the list of subscribers.
     *
     * @param server The server to stop tracking time for.
     * @return {@code true} if the server is successfully removed as a subscriber, {@code false} if not subscribed.
     */
    public boolean stop(Server server) {
        if (!subscribers.contains(server)) return false;
        subscribers.remove(server);
        subscriberStarts.remove(server);
        return true;
    }

    /**
     * Gets the start time of the TimeCheck instance.
     *
     * @return The start time in nanoseconds.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the start time for a specific server.
     *
     * @param server The server for which to retrieve the start time.
     * @return The start time for the server in nanoseconds.
     */
    public long getStartTime(Server server) {
        return subscriberStarts.get(server);
    }

    /**
     * Gets the relative start time for a specific server.
     *
     * @param server The server for which to retrieve the relative start time.
     * @return The relative start time for the server in nanoseconds.
     */
    public long getRelStartTime(Server server) {
        return subscriberStarts.get(server) - startTime;
    }

    /**
     * Gets the current system time in nanoseconds.
     *
     * @return The current system time in nanoseconds.
     */
    public long getTime() {
        return System.nanoTime() - startTime;
    }

    /**
     * Gets the current system time for a specific server in nanoseconds.
     *
     * @param server The server for which to retrieve the system time.
     * @return The current system time for the server in nanoseconds.
     */
    public long getTime(Server server) {
        return System.nanoTime() - subscriberStarts.get(server);
    }

    /**
     * Gets the current system time in milliseconds as a double.
     *
     * @return The current system time in milliseconds as a double.
     */
    public double getTimeMilliDouble() {
        return (double) getTime() / 1000000;
    }

    /**
     * Gets the current system time in milliseconds as a double for a specific server.
     *
     * @param server The server for which to retrieve the system time.
     * @return The current system time for the server in milliseconds as a double.
     */
    public double getTimeMilliDouble(Server server) {
        return (double) getTime(server) / 1000000;
    }

    /**
     * Checks if the updater is enabled.
     *
     * @return {@code true} if the updater is enabled, {@code false} otherwise.
     */
    public boolean isUpdaterEnabled() {
        return updaterEnabled;
    }

    /**
     * Enables or disables the updater.
     *
     * @param updaterEnabled {@code true} to enable the updater, {@code false} to disable it.
     */
    public void setUpdaterEnabled(boolean updaterEnabled) {
        boolean prevState = this.updaterEnabled;
        this.updaterEnabled = updaterEnabled;
        if (!prevState && isUpdaterEnabled()) updater();
    }

    /**
     * Initializes and starts the time-keeping thread for the updater.
     */
    private void updater() {
        Thread updater = new Thread(this::initializeTimeThread, "timeKeeper");
        updater.start();
    }

    /**
     * Initializes and manages the time-keeping thread, which updates time for subscribers
     * and triggers alerts when necessary.
     * This method handles the continuous tracking of time.
     */
    public void initializeTimeThread() {
        // Move to the next scheduled alert.
        nextAlert();

        // Check if there are subscribers.
        if (subscribers.isEmpty()) throw new IllegalStateException("No subscribers.");

        if (currentAlert != null) {
            long wait = 1000;
            // Continuously update time while the updater is enabled.
            while (this.updaterEnabled) {
                // If there is a parent JobDispatcher, update its system time.
                if (parent != null) parent.setSystemTime(getTimeMilliDouble());

                // Update time for all subscribed servers.
                for (Server serv : this.subscribers) {
                    serv.setTime(getTimeMilliDouble(serv));
                }

                if (currentAlert != null) {
                    // Check if it's time to trigger an alert.
                    if (currentAlert.getKey() <= getTimeMilliDouble()) {
                        currentAlert.getValue().alert();
                        nextAlert();

                        // Adjust the waiting time based on the time until the next alert.
                        if (currentAlert != null)
                            while ((currentAlert.getKey() - getTimeMilliDouble()) <= wait * 10 && wait != 1000)
                                wait *= 10;
                    } else if ((currentAlert.getKey() - getTimeMilliDouble()) <= wait && wait != 1) wait /= 10;
                }

                try {
                    // Sleep for the specified waiting time.
                    TimeUnit.NANOSECONDS.sleep(wait);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            // If there are no scheduled alerts, simply update time for subscribers.
            while (this.updaterEnabled) {
                for (Server serv : this.subscribers) {
                    serv.setTime(getTimeMilliDouble(serv));
                }

                try {
                    // Sleep for one second if there are no alerts.
                    TimeUnit.NANOSECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
