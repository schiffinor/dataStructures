/*

 */
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * <p>
 *
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class TimeCheck {
    private long startTime;
    private final LinkedList<Server> subscribers;
    private final TreeMap<Double, JobDispatcher> alerts;
    private final HashMap<Server,Long> subscriberStarts;
    private final JobDispatcher parent;
    private boolean updaterEnabled;
    private Map.Entry<Double,JobDispatcher> currentAlert;


    public TimeCheck() {
        this(null);
    }

    public TimeCheck(JobDispatcher parent) {
        this.parent = parent;
        this.subscribers = new LinkedList<>();
        this.alerts = new TreeMap<>();
        this.subscriberStarts = new HashMap<>();
        this.updaterEnabled = false;
    }

    public void startTimer() {
        this.startTime = System.nanoTime();
    }


    public void registerAlert(double time, JobDispatcher dispatcher) {
        alerts.put(time, dispatcher);
    }


    public void nextAlert() {
        currentAlert = alerts.pollFirstEntry();
    }

    public boolean start(Server server) {
        if (subscribers.contains(server)) return false;
        subscribers.add(server);
        subscriberStarts.put(server, System.nanoTime());
        return true;
    }


    public boolean stop(Server server) {
        if (!subscribers.contains(server)) return false;
        subscribers.remove(server);
        subscriberStarts.remove(server);
        return true;
    }


    public long getStartTime() {
        return startTime;
    }


    public long getStartTime(Server server) {
        return subscriberStarts.get(server);
    }


    public long getRelStartTime(Server server) {
        return subscriberStarts.get(server)-startTime;
    }


    public long getTime() {
        return System.nanoTime()-startTime;
    }


    public long getTime(Server server) {
        return System.nanoTime()-subscriberStarts.get(server);
    }


    public double getTimeMilliDouble() {
        return (double) getTime()/1000000;
    }

    public double getTimeMilliDouble(Server server) {
        return (double) getTime(server)/1000000;
    }

    public boolean isUpdaterEnabled() {
        return updaterEnabled;
    }

    public void setUpdaterEnabled(boolean updaterEnabled) {
        boolean prevState = this.updaterEnabled;
        this.updaterEnabled = updaterEnabled;
        if (!prevState && isUpdaterEnabled()) updater();
    }

    private void updater() {
        Thread updater = new Thread(this::initializeTimeThread,"timeKeeper");
        updater.start();
    }



    public void initializeTimeThread() {
        nextAlert();
        if (subscribers.isEmpty()) throw new IllegalStateException("No subscribers.");
        if (currentAlert != null) {
            long wait = 1000;
            while (this.updaterEnabled) {
                if (parent != null) parent.setSystemTime(getTimeMilliDouble());
                for (Server serv : this.subscribers) {
                    serv.setTime(getTimeMilliDouble(serv));
                }
                if (currentAlert != null) {
                    if (currentAlert.getKey() <= getTimeMilliDouble()) {
                        currentAlert.getValue().alert();
                        nextAlert();
                        if (currentAlert != null)
                            while ((currentAlert.getKey() - getTimeMilliDouble()) <= wait*10 && wait != 1000) wait *= 10;
                    } else if ((currentAlert.getKey() - getTimeMilliDouble()) <= wait && wait != 1) wait /= 10;
                }
                try {
                    TimeUnit.NANOSECONDS.sleep(wait);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            while (this.updaterEnabled) {
                for (Server serv : this.subscribers) {
                    serv.setTime(getTimeMilliDouble(serv));
                }
                try {
                    TimeUnit.NANOSECONDS.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
