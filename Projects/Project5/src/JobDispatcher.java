/*

 */
import java.awt.*;
import java.util.ArrayList;

/**
 *
 * <p>
 *
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public abstract class JobDispatcher {

    protected final boolean simulated;
    protected final ArrayList<Server> serverList;
    protected final int serverCount;
    protected final ServerFarmViz visualization;
    protected final boolean visualized;
    protected final TimeCheck timer;
    protected double systemTime;
    protected boolean alertState;
    protected int jobsHandled;
    protected Job lastJob;
    protected double lastJobTime;
    protected Thread visualThread;

    public boolean isVisualThreadEnabled() {
        return visualThreadEnabled;
    }

    public void setVisualThreadEnabled(boolean visualThreadEnabled) {
        this.visualThreadEnabled = visualThreadEnabled;
    }

    protected boolean visualThreadEnabled;

    public LinkedList<Job> getJobList() {
        return jobList;
    }

    protected LinkedList<Job> jobList;

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation disabled by default.
     * @param k
     * @param showViz
     */
    public JobDispatcher(int k, boolean showViz) {
        this(k,showViz,false);
    }

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation toggleable.
     * @param k
     * @param showViz
     * @param simulated
     */
    public JobDispatcher(int k, boolean showViz, boolean simulated) {
        this.serverCount = k;
        this.serverList = new ArrayList<>(k);
        this.visualized = showViz;
        this.visualization = (showViz) ? new ServerFarmViz(this) : null;
        this.visualThread = (showViz) ? new Thread(this::visualUpdateThread) : null;
        this.simulated = simulated;
        this.timer = (this.simulated) ? new TimeCheck(this) : null;
        this.systemTime = 0;
        for (int i = 0; i < this.serverCount; i++) {
            Server temp = new Server(simulated);
            this.serverList.add(temp);
            if (simulated) temp.setTimer(this.timer);
        }
    }


    /**
     * returns the time
     * @return
     */
    public double getTime() {
        return (isSimulated()) ? getTimer().getTimeMilliDouble() : this.systemTime;
    }

    /**
     * returns the jobDispatcher's collection of Servers
     * @return
     */
    public ArrayList<Server> getServerList() {
        return serverList;
    }

    /**
     * This method updates the current time to the specified time
     * and calls the processTo method for each Server it maintains.
     * @param time
     */
    public void advanceTimeTo(double time) {
        setSystemTime(time);
        for (Server server : serverList) {
            server.processTo(time);
        }
    }

    /**
     * advances the time to job's arrival time, if showViz is true,
     * it calls the ServerFarmViz object's repaint() method,
     * picks the Server appropriate for job (whichever one is returned by the pickServer method),
     * and adds job to the chosen Server, then, if showViz is true,
     * it calls the ServerFarmViz object's repaint() method again.
     * @param job
     */
    public void handleJob(Job job) {
        advanceTimeTo(job.getArrivalTime());
        if (isVisualized())
            getVisualization().repaint();
        pickServer(job).addJob(job);
        if (isVisualized())
            getVisualization().repaint();
    }


    /**
     *
     * @param job
     * @param isSimulated
     */
    public void handleJob(Job job, boolean isSimulated) {
        incrementJobsHandled();
        if (!isSimulated) handleJob(job);
        else handleJobSim(job);

    }



    public void visualUpdateThread() {
        setVisualThreadEnabled(true);
        while (isVisualThreadEnabled()) {
            getVisualization().repaint();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     *
     * @param job
     */
    public void handleJobSim(Job job) {
        pickServer(job).addJob(job);
    }

    /**
     * advances the time to the earliest point when all jobs will have completed.
     */
    public void finishUp() {
        double currentMaxWork = 0;
        for (Server server : serverList) {
            double work = server.remainingWorkInQueue();
            if (work > currentMaxWork) {
                currentMaxWork = work;
            }
        }
        advanceTimeTo(getSystemTime() + currentMaxWork);
        if (isVisualized())
            getVisualization().repaint();
    }


    public void alert() {
        Job latestJob = this.jobList.poll();
        handleJob(latestJob, isSimulated());
    }


    public void resetAlert() {
        this.alertState = false;
    }


    /**
     * Polls each Job from the specified queue of Jobs and calls handleJob on them.
     * After all Jobs have been handled, calls finishUp()
     *
     * @param jobs
     */
    public void handleJobs(LinkedList<Job> jobs) {
        this.jobList = jobs;
        if (isSimulated()) {
            TimeCheck timer = getTimer();
            LinkedList<Job> jobs2 = jobs.clone();
            while (!jobs2.isEmpty()) {
                timer.registerAlert(jobs2.poll().getArrivalTime(),this);
            }
            if (isVisualized()) visualThread.start();
            getTimer().startTimer();
            for (Server server : serverList) server.serverStart();
            timer.setUpdaterEnabled(true);
        } else {
            while (!jobs.isEmpty()) {
                Job latestJob = jobs.poll();
                handleJob(latestJob, isSimulated());
                }
            }
            finishUp();
    }



    /**
     * gets the total number of jobs handled across all Servers.
     * @return
     */
    public int getNumJobsHandled() {
        return jobsHandled;
    }

    /**
     * gets the total waiting time for each Server, adds them together,
     * and divides it by the number of jobs handled across all Servers.
     * @return
     */
    public double getAverageWaitingTime() {
        double output = 0;
        for (Server server : serverList) {
            output += server.getTotalWaitTime();
        }
        output /= getNumJobsHandled();
        return output;
    }


    public abstract Server pickServer(Job j);

    public boolean isSimulated() {
        return simulated;
    }


    public boolean isVisualized() {
        return visualized;
    }


    public ServerFarmViz getVisualization() {
        return visualization;
    }

    public double getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(double systemTime) {
        this.systemTime = systemTime;
    }


    public TimeCheck getTimer() {
        return timer;
    }


    public void incrementJobsHandled() {
        jobsHandled++;
    }


    public void draw(Graphics g){
        double sep = (ServerFarmViz.HEIGHT - 20) / (getServerList().size() + 2.0);
        g.drawString("Time: " + getTime(), (int) sep, ServerFarmViz.HEIGHT - 20);
        g.drawString("Jobs handled: " + getNumJobsHandled(), (int) sep, ServerFarmViz.HEIGHT - 10);
        for(int i = 0; i < getServerList().size(); i++){
            getServerList().get( i ).draw(g, (i % 2 == 0) ? Color.GRAY : Color.DARK_GRAY, (i + 1) * sep, getServerList().size());
        }
    }
}
