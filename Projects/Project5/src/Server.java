import java.awt.*;

/**
 * The Server class represents a server in a job processing system.
 * It manages a queue of jobs and processes them, recording statistics
 * about the jobs processed and wait times.
 * <p>
 * This class provides methods for adding jobs to the server, processing
 * jobs, and managing the server's operation.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class Server {
    private final LinkedList<Job> jobsProcessed;
    private final LinkedList<Double> waitTimePerJob;
    private final LinkedList<Job> jobQueue;
    private final LinkedList<Job> activeJobs;
    private final LinkedList<Job> waitingJobs;
    protected double currentRemainingWork;
    private double systemTime;
    private int jobsProcessedCount;
    private double totalWaitTime;
    private TimeCheck timer;
    private boolean simulatedProcessing;
    private boolean timeInterrupt;
    private Job currentJob;
    private boolean initialized;
    private boolean running;

    /**
     * Constructs a new server instance with default settings. The server
     * will not perform simulated processing by default.
     */
    public Server() {
        this(false);
    }

    /**
     * Constructs a new server instance with the option to enable or disable
     * simulated processing.
     *
     * @param simulatedProcessing {@code true} to enable simulated processing,
     *                            {@code false} otherwise.
     */
    public Server(boolean simulatedProcessing) {
        this.systemTime = 0;
        this.simulatedProcessing = simulatedProcessing;
        this.jobsProcessedCount = 0;
        this.jobsProcessed = new LinkedList<>();
        this.waitTimePerJob = new LinkedList<>();
        this.jobQueue = new LinkedList<>();
        this.activeJobs = new LinkedList<>();
        this.waitingJobs = new LinkedList<>();
        this.initialized = false;
        this.running = false;

    }

    /**
     * Gets the current remaining work to be processed by the server.
     *
     * @return The current remaining work.
     */
    public double getCurrentRemainingWork() {
        return currentRemainingWork;
    }

    /**
     * Sets the current remaining work to the specified value.
     *
     * @param currentRemainingWork The new value for the current remaining work.
     */
    public void setCurrentRemainingWork(double currentRemainingWork) {
        this.currentRemainingWork = currentRemainingWork;
    }

    /**
     * Increments the current remaining work by the specified amount.
     *
     * @param work The amount by which to increment the current remaining work.
     */
    public void incrementCurrentRemainingWork(double work) {
        this.currentRemainingWork += work;
    }

    /**
     * Gets the list of jobs processed by the server.
     *
     * @return The list of processed jobs.
     */
    public LinkedList<Job> getJobsProcessed() {
        return jobsProcessed;
    }

    /**
     * Gets the list of wait times for each processed job.
     *
     * @return The list of wait times per job.
     */
    public LinkedList<Double> getWaitTimePerJob() {
        return waitTimePerJob;
    }

    /**
     * Gets the list of jobs currently in the server's job queue.
     *
     * @return The list of jobs in the job queue.
     */
    public LinkedList<Job> getJobQueue() {
        return jobQueue;
    }

    /**
     * Gets the list of active jobs being processed by the server.
     *
     * @return The list of active jobs.
     */
    public LinkedList<Job> getActiveJobs() {
        return activeJobs;
    }

    /**
     * Gets the list of jobs waiting to be processed by the server.
     *
     * @return The list of waiting jobs.
     */
    public LinkedList<Job> getWaitingJobs() {
        return waitingJobs;
    }

    /**
     * Gets the total wait time of jobs processed by the server.
     *
     * @return The total wait time.
     */
    public double getTotalWaitTime() {
        return totalWaitTime;
    }

    /**
     * Sets the total wait time to the specified value.
     *
     * @param totalWaitTime The new total wait time to set.
     */
    public void setTotalWaitTime(double totalWaitTime) {
        this.totalWaitTime = totalWaitTime;
    }

    /**
     * Gets the timer associated with the server for time tracking.
     *
     * @return The server's timer.
     */
    public TimeCheck getTimer() {
        return timer;
    }

    /**
     * Sets the server's timer to the provided timer instance.
     *
     * @param timer The new timer to set for the server.
     */
    public void setTimer(TimeCheck timer) {
        this.timer = timer;
    }

    /**
     * Increments the system time by the specified amount.
     *
     * @param time The amount by which to increment the system time.
     */
    public void incrementTime(double time) {
        systemTime += time;
    }

    /**
     * Checks if the server's time is interrupted.
     *
     * @return {@code true} if time is interrupted, {@code false} otherwise.
     */
    public boolean getTimeInterrupt() {
        return timeInterrupt;
    }

    /**
     * Sets whether the server's time is interrupted or not.
     *
     * @param timeInterrupt {@code true} to indicate time interruption, {@code false} otherwise.
     */
    public void setTimeInterrupt(boolean timeInterrupt) {
        this.timeInterrupt = timeInterrupt;
    }

    /**
     * Gets the number of jobs in the server's job queue.
     *
     * @return The size of the job queue.
     */
    public int size() {
        return this.jobQueue.size();
    }

    /**
     * Adds a new job to the server's job queue.
     * The job will be placed in both the job queue and the waiting jobs list,
     * and the server's remaining work will be updated accordingly.
     *
     * @param job The job to be added to the server's queue.
     */
    public void addJob(Job job) {
        // Synchronize access to the job queue to ensure thread safety.
        synchronized (jobQueue) {
            this.jobQueue.offer(job);
        }
        // Synchronize access to the waiting jobs list for thread safety.
        synchronized (waitingJobs) {
            this.waitingJobs.offer(job);
        }

        // Increment the server's current remaining work by the job's processing time.
        incrementCurrentRemainingWork(job.getTotalProcessingTime());

        // Set the server as the job's server for reference.
        job.setServer(this);

        // If the server is configured for simulated processing, initiate a server refresh.
        if (isSimulated()) serverRefresh();
    }

    /**
     * Calculates the total remaining work in the server's job queue by iterating through
     * each job and summing up their remaining processing times.
     *
     * @return The total remaining work in the server's job queue.
     */
    public double remainingWorkInQueue() {
        double output = 0;
        LinkedList<Job> jobClone;

        // Synchronize access to the job queue for thread safety.
        synchronized (jobQueue) {
            // Clone the job queue to avoid concurrent modification while iterating.
            jobClone = this.jobQueue.clone();
        }

        // Iterate through the cloned job queue and sum up the remaining processing times.
        while (!jobClone.isEmpty()) {
            output += jobClone.poll().getTimeRemaining();
        }

        return output;
    }

    /**
     * Provides a simplified method for retrieving the current remaining work in the server's job queue.
     * <p>
     * Equivalent to {@link Server#getCurrentRemainingWork()}
     *
     * @return The current remaining work in the server's job queue.
     */
    public double remainingWorkInQueueShrimple() {
        return getCurrentRemainingWork();
    }

    /**
     * Processes jobs in the server's job queue until the specified time is reached or the job queue is empty.
     * The method iteratively processes jobs based on their remaining processing times.
     *
     * @param time The time up to which jobs should be processed.
     */
    public void processTo(double time) {
        while (systemTime < time && !jobQueue.isEmpty()) {
            double timeSpan = time - systemTime;
            Job tempJob = jobQueue.peek();
            double jobTime = tempJob.getTimeRemaining();

            if (jobTime < timeSpan) {
                // Process the job for its remaining time and remove it from the queue.
                tempJob.process(jobTime);
            } else {
                // Process the job for the available time span and break the loop.
                tempJob.process(timeSpan);
                break;
            }
        }
        // If the system time is still less than the specified time, update the system time.
        if (systemTime < time) setTime(time);
    }

    /**
     * Initiates the server's operation by starting the associated timer and invoking the server refresh process.
     * This method is used to begin processing jobs on the server.
     *
     * @return {@code true} if the server's operation started successfully,
     * {@code false} otherwise.
     */
    public boolean serverStart() {
        // Start the server's timer for time tracking.
        getTimer().start(this);

        // Initiate the server refresh process.
        return serverRefresh();
    }

    /**
     * Initiates the server refresh process, creating a new thread to execute the jobExecutor method.
     * This method is responsible for processing jobs and managing the server's operation.
     *
     * @return {@code true} if the server refresh process is successfully initiated,
     * {@code false} if the server is already running.
     */
    public boolean serverRefresh() {
        if (isRunning()) return false;
        setRunning(true);

        // Create a new thread to execute the jobExecutor method.
        Thread serverRuntime = new Thread(this::jobExecutor, "jobThread");
        serverRuntime.start();

        return isRunning();
    }

    /**
     * Shuts down the server by stopping its associated timer.
     * This method is used to stop the server's operation.
     */
    public void shutdown() {
        timer.stop(this);
    }

    /**
     * Executes the job processing for waiting jobs in the server.
     * This method is responsible for processing jobs marked as "waiting."
     * Once all waiting jobs are processed,
     * it sets the server to a non-running state and interrupts the current thread.
     */
    public void jobExecutor() {
        while (!waitingJobs.isEmpty()) {
            Job tempJob;

            synchronized (waitingJobs) {
                tempJob = waitingJobs.peek();
            }

            if (tempJob != null) tempJob.processComplete();
        }

        // Set the server to a non-running state.
        setRunning(false);

        // Interrupt the current thread.
        Thread thisThread = Thread.currentThread();
        thisThread.interrupt();
    }

    public void draw(Graphics g, Color c, double loc, int numberOfServers) {
        double sep = (ServerFarmViz.HEIGHT - 20) / (numberOfServers + 2.0);
        g.setColor(Color.BLACK);
        g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), (int) (72.0 * (sep * .5) / Toolkit.getDefaultToolkit().getScreenResolution())));
        g.drawString("Work: " + (remainingWorkInQueue() < 1000 ? remainingWorkInQueue() : ">1000"), 2, (int) (loc + .2 * sep));
        g.drawString("Jobs: " + (size() < 1000 ? size() : ">1000"), 5, (int) (loc + .55 * sep));
        g.setColor(c);
        g.fillRect((int) (3 * sep), (int) loc, (int) (.8 * remainingWorkInQueue()), (int) sep);
        g.drawOval(2 * (int) sep, (int) loc, (int) sep, (int) sep);
        if (remainingWorkInQueue() == 0) g.setColor(Color.GREEN.darker());
        else g.setColor(Color.RED.darker());
        g.fillOval(2 * (int) sep, (int) loc, (int) sep, (int) sep);
    }

    /**
     * Gets the current time of the server, either from the timer if simulated processing is enabled,
     * or from the internal system time.
     *
     * @return The current time of the server.
     */
    public double getTime() {
        return (isSimulated()) ? getTimer().getTimeMilliDouble(this) : this.systemTime;
    }

    /**
     * Sets the system time of the server to the specified time.
     *
     * @param time The new time to set for the server.
     */
    public void setTime(double time) {
        this.systemTime = time;
    }

    /**
     * Checks if the server is configured for simulated processing.
     *
     * @return {@code true} if simulated processing is enabled, {@code false} otherwise.
     */
    public boolean isSimulated() {
        return simulatedProcessing;
    }

    /**
     * Sets whether the server should use simulated processing or not.
     *
     * @param simulatedProcessing {@code true} to enable simulated processing, {@code false} to disable.
     */
    public void setSimulated(boolean simulatedProcessing) {
        this.simulatedProcessing = simulatedProcessing;
    }

    /**
     * Initializes the server by moving jobs from the job queue to the waiting jobs list.
     * If the server is already initialized, it throws an exception.
     *
     * @throws IllegalStateException if the server is already initialized.
     */
    public void initializeServer() {
        if (isInitialized()) throw new IllegalStateException("Can't initialize pre-initialized server.");
        for (Job job : jobQueue) {
            waitingJobs.offer(job);
        }
    }

    /**
     * Checks if the server is already initialized.
     *
     * @return {@code true} if the server is initialized, {@code false} otherwise.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Sets the initialized state of the server.
     *
     * @param initialized {@code true} to mark the server as initialized, {@code false} otherwise.
     */
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * Gets the current job being processed by the server.
     *
     * @return The current job being processed.
     */
    public Job getCurrentJob() {
        return currentJob;
    }

    /**
     * Sets the current job being processed by the server.
     *
     * @param currentJob The new current job to set.
     */
    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }

    /**
     * Gets the count of jobs processed by the server.
     *
     * @return The count of processed jobs.
     */
    public int getJobsProcessedCount() {
        return jobsProcessedCount;
    }

    /**
     * Sets the count of jobs processed by the server.
     *
     * @param jobsProcessedCount The new count of processed jobs.
     */
    public void setJobsProcessedCount(int jobsProcessedCount) {
        this.jobsProcessedCount = jobsProcessedCount;
    }

    /**
     * Checks if the server is currently running.
     *
     * @return {@code true} if the server is running, {@code false} otherwise.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets the running state of the server.
     *
     * @param running {@code true} to mark the server as running, {@code false} otherwise.
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Increments the count of jobs processed by the server.
     */
    public void incrementJobsProcessedCount() {
        jobsProcessedCount++;
    }

    /**
     * Increments the total wait time of jobs processed by the server by the specified time.
     *
     * @param time The time to increment the total wait time.
     */
    public void incrementTotalWaitTime(double time) {
        totalWaitTime += time;
    }
}
