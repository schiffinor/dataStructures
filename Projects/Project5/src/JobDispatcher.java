/*

 */

import java.awt.*;
import java.util.ArrayList;

/**
 * Provides a base class for managing and dispatching jobs to a collection of servers.
 * It handles time tracking and job handling in both simulated and non-simulated environments.
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
    protected final Thread visualThread;
    protected boolean visualThreadEnabled;
    protected LinkedList<Job> jobList;

    /**
     * Constructs a JobDispatcher with k total Servers.
     * Make sure this initializes all the fields of this class to appropriate values.
     * Multithreaded simulation disabled by default.
     *
     * @param k number of serves
     * @param showViz whether to show visualization
     */
    public JobDispatcher(int k, boolean showViz) {
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

    public boolean isVisualThreadEnabled() {
        return visualThreadEnabled;
    }

    public void setVisualThreadEnabled(boolean visualThreadEnabled) {
        this.visualThreadEnabled = visualThreadEnabled;
    }

    public LinkedList<Job> getJobList() {
        return jobList;
    }

    /**
     * Gets the current time of the job dispatcher.
     *
     * @return The current time in milliseconds.
     */
    public double getTime() {
        return (isSimulated()) ? getTimer().getTimeMilliDouble() : this.systemTime;
    }

    /**
     * Gets the list of servers maintained by the job dispatcher.
     *
     * @return The list of servers.
     */
    public ArrayList<Server> getServerList() {
        return serverList;
    }

    /**
     * Advances the time to the specified time and processes jobs on all servers accordingly.
     *
     * @param time The target time to advance to.
     */
    public void advanceTimeTo(double time) {
        setSystemTime(time);
        for (Server server : serverList) {
            server.processTo(time);
        }
    }

    /**
     * Handles a job by advancing the time to its arrival time,
     * assigning it to a server, and updating the visualization if enabled.
     *
     * @param job The job to handle.
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
     * Handles a job based on whether it is simulated or not. Increments the jobs handled count.
     *
     * @param job         The job to handle.
     * @param isSimulated A flag indicating whether the job handling is simulated.
     */
    public void handleJob(Job job, boolean isSimulated) {
        incrementJobsHandled();
        if (!isSimulated) handleJob(job);
        else handleJobSim(job);

    }

    /**
     * Continuously updates the visualization in a separate thread, if enabled.
     */
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
     * Handles a job in a simulated environment by assigning it to an appropriate server.
     *
     * @param job The job to handle in a simulated environment.
     */
    public void handleJobSim(Job job) {
        pickServer(job).addJob(job);
    }

    /**
     * Advances the time to the earliest point when all jobs will have completed
     * and updates the visualization if enabled.
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

    /**
     * Triggers an alert to handle the next job in the queue and marks the alert state as true.
     */
    public void alert() {
        Job latestJob = this.jobList.poll();
        handleJob(latestJob, isSimulated());
    }

    /**
     * Resets the alert state to false, indicating that an alert has been processed.
     */
    public void resetAlert() {
        this.alertState = false;
    }


    /**
     * Polls each job from the specified queue of jobs and calls the handleJob method on them.
     * After all jobs have been handled, it calls the finishUp method to ensure all jobs are completed.
     *
     * @param jobs The queue of jobs to be handled by the dispatcher.
     */
    public void handleJobs(LinkedList<Job> jobs) {
        this.jobList = jobs;

        // If the simulation is enabled, set up alerts for job arrivals and start the timer.
        if (isSimulated()) {
            TimeCheck timer = getTimer();
            LinkedList<Job> jobs2 = jobs.clone();
            while (!jobs2.isEmpty()) {
                timer.registerAlert(jobs2.poll().getArrivalTime(), this);
            }

            // Start the visual update thread if visualization is enabled.
            if (isVisualized()) visualThread.start();

            // Start the timer and server processing threads.
            getTimer().startTimer();
            for (Server server : serverList) server.serverStart();

            // Enable the timer updater.
            timer.setUpdaterEnabled(true);
        } else {
            // Handle each job in a non-simulated environment.
            while (!jobs.isEmpty()) {
                Job latestJob = jobs.poll();
                handleJob(latestJob, isSimulated());
            }
        }

        // Ensure all jobs are completed.
        finishUp();
    }


    /**
     * Gets the total number of jobs handled across all Servers.
     *
     * @return The total number of jobs handled.
     */
    public int getNumJobsHandled() {
        return jobsHandled;
    }

    /**
     * Gets the average waiting time for all Servers. It adds the waiting times of each Server
     * and divides it by the total number of jobs handled across all Servers.
     *
     * @return The average waiting time in milliseconds.
     */
    public double getAverageWaitingTime() {
        double output = 0;
        for (Server server : serverList) {
            output += server.getTotalWaitTime();
        }
        output /= getNumJobsHandled();
        return output;
    }

    /**
     * Abstract method to be implemented by subclasses for selecting the appropriate Server for a job.
     *
     * @param j The job for which the Server should be selected.
     * @return The selected Server for the job.
     */
    public abstract Server pickServer(Job j);

    /**
     * Checks if the simulation is enabled.
     *
     * @return True if the simulation is enabled, false otherwise.
     */
    public boolean isSimulated() {
        return simulated;
    }

    /**
     * Checks if visualization is enabled.
     *
     * @return True if visualization is enabled, false otherwise.
     */
    public boolean isVisualized() {
        return visualized;
    }

    /**
     * Gets the visualization object for the dispatcher.
     *
     * @return The ServerFarmViz object for visualization.
     */
    public ServerFarmViz getVisualization() {
        return visualization;
    }

    /**
     * Gets the current system time of the dispatcher.
     *
     * @return The current system time in milliseconds.
     */
    public double getSystemTime() {
        return systemTime;
    }

    /**
     * Sets the system time of the dispatcher.
     *
     * @param systemTime The new system time to set.
     */
    public void setSystemTime(double systemTime) {
        this.systemTime = systemTime;
    }

    /**
     * Gets the timer for time-related operations.
     *
     * @return The TimeCheck timer object.
     */
    public TimeCheck getTimer() {
        return timer;
    }

    /**
     * Increments the total number of jobs handled by one.
     */
    public void incrementJobsHandled() {
        jobsHandled++;
    }

    /**
     * Draws a visual representation of the dispatcher, including the current time,
     * the number of jobs handled, and the status of each Server in the visualization.
     *
     * @param g The Graphics object to draw on.
     */
    public void draw(Graphics g) {
        double sep = (ServerFarmViz.HEIGHT - 20) / (getServerList().size() + 2.0);
        g.drawString("Time: " + getTime(), (int) sep, ServerFarmViz.HEIGHT - 20);
        g.drawString("Jobs handled: " + getNumJobsHandled(), (int) sep, ServerFarmViz.HEIGHT - 10);
        for (int i = 0; i < getServerList().size(); i++) {
            getServerList().get(i).draw(g, (i % 2 == 0) ? Color.GRAY : Color.DARK_GRAY, (i + 1) * sep, getServerList().size());
        }
    }
}
