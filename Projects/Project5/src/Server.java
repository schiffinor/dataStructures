import java.awt.*;

/**
 *
 * <p>
 *
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
    private double systemTime;
    private int jobsProcessedCount;
    private double totalWaitTime;
    private TimeCheck timer;
    private boolean simulatedProcessing;
    private boolean timeInterrupt;
    private Job currentJob;
    private boolean initialized;
    private boolean running;

    public double getCurrentRemainingWork() {
        return currentRemainingWork;
    }

    public void incrementCurrentRemainingWork(double work) {
        this.currentRemainingWork += work;
    }

    public void setCurrentRemainingWork(double currentRemainingWork) {
        this.currentRemainingWork = currentRemainingWork;
    }

    protected double currentRemainingWork;

    public Server() {
        this(false);
    }

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

    public static void main(String[] args) throws InterruptedException {
        TimeCheck timer = new TimeCheck();
        Server server = new Server(true);
        server.setTimer(timer);
        LinkedList<Job> jobs = Job.readJobFile("jobs.txt");
        assert jobs != null;
        for (Job job : jobs) {
            server.addJob(job);
        }
        timer.startTimer();
        timer.setUpdaterEnabled(true);
        server.serverStart();
        Thread.sleep(5000);
        server.shutdown();
        timer.setUpdaterEnabled(false);

        Server server2 = new Server();
        LinkedList<Job> jobs2 = Job.readJobFile("jobs.txt");
        assert jobs2 != null;
        for (Job job : jobs2) {
            server2.addJob(job);
        }
        server2.processTo(500);
        server2.processTo(1000);
    }

    public LinkedList<Job> getJobsProcessed() {
        return jobsProcessed;
    }

    public LinkedList<Double> getWaitTimePerJob() {
        return waitTimePerJob;
    }

    public LinkedList<Job> getJobQueue() {
        return jobQueue;
    }

    public LinkedList<Job> getActiveJobs() {
        return activeJobs;
    }

    public LinkedList<Job> getWaitingJobs() {
        return waitingJobs;
    }

    public double getTotalWaitTime() {
        return totalWaitTime;
    }

    public void setTotalWaitTime(double totalWaitTime) {
        this.totalWaitTime = totalWaitTime;
    }

    public TimeCheck getTimer() {
        return timer;
    }

    public void setTimer(TimeCheck timer) {
        this.timer = timer;
    }

    public void incrementTime(double time) {
        systemTime += time;
    }

    public boolean getTimeInterrupt() {
        return timeInterrupt;
    }

    public void setTimeInterrupt(boolean timeInterrupt) {
        this.timeInterrupt = timeInterrupt;
    }

    public int size() {
        return this.jobQueue.size();
    }

    public void addJob(Job job) {
        synchronized (jobQueue) {
            this.jobQueue.offer(job);
        }
        synchronized (waitingJobs) {
            this.waitingJobs.offer(job);
        }
        incrementCurrentRemainingWork(job.getTotalProcessingTime());
        job.setServer(this);
        if (isSimulated()) serverRefresh();
    }

    public double remainingWorkInQueue() {
        double output = 0;
        LinkedList<Job> jobClone = null;
        synchronized (jobQueue) {
            jobClone = this.jobQueue.clone();
        }
        while (!jobClone.isEmpty()) {
            output += jobClone.poll().getTimeRemaining();
        }
        return output;
    }

    public double remainingWorkInQueueShrimple() {
        return getCurrentRemainingWork();
    }

    public void processTo(double time) {
        while (systemTime < time && !jobQueue.isEmpty()) {
            double timeSpan = time - systemTime;
            Job tempJob = jobQueue.peek();
            double jobTime = tempJob.getTimeRemaining();
            if (jobTime < timeSpan) {
                tempJob.process(jobTime);
            } else {
                tempJob.process(timeSpan);
                break;
            }
        }
        if (systemTime < time) setTime(time);
    }

    public boolean serverStart() {
        getTimer().start(this);
        return serverRefresh();
    }

    public boolean serverRefresh() {
        if (isRunning()) return false;
        setRunning(true);
        Thread serverRuntime = new Thread(this::jobExecutor, "jobThread");
        serverRuntime.start();
        return isRunning();
    }

    public void shutdown() {
        timer.stop(this);
    }

    public void jobExecutor() {
        while (!waitingJobs.isEmpty()) {
            Job tempJob = null;
            synchronized (waitingJobs) {
                tempJob = waitingJobs.peek();
            }
            if (tempJob != null) tempJob.processComplete();
        }
        setRunning(false);
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

    public double getTime() {
        return (isSimulated()) ? getTimer().getTimeMilliDouble(this) : this.systemTime;
    }

    public void setTime(double time) {
        this.systemTime = time;
    }

    public boolean isSimulated() {
        return simulatedProcessing;
    }

    public void setSimulated(boolean simulatedProcessing) {
        this.simulatedProcessing = simulatedProcessing;
    }

    public void initializeServer() {
        if (isInitialized()) throw new IllegalStateException("Can't initialize pre-initialized server.");
        for (Job job : jobQueue) {
            waitingJobs.offer(job);
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public Job getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }

    public int getJobsProcessedCount() {
        return jobsProcessedCount;
    }

    public void setJobsProcessedCount(int jobsProcessedCount) {
        this.jobsProcessedCount = jobsProcessedCount;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }


    public void incrementJobsProcessedCount() {
        jobsProcessedCount++;
    }


    public void incrementTotalWaitTime(double time) {
        totalWaitTime += time;
    }
}
