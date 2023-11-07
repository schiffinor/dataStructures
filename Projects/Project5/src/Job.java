/*

 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The `Job` class represents a job that needs to be processed by a server.
 * It tracks various attributes of the job including its arrival time, processing time,
 * and its state during processing.
 * This is one of the core classes of the project and everything is super dependent on it.
 * <p>
 * Also, not going to lie, I realize that all my code is super interdependent on itself.
 * I looked at some video essays on dev, especially that of games and how such co-dependent
 * code is not the best practice, i.e., spaghetti code.
 * It would be useful to learn how to fix that.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class Job {

    private final double arrivalTime;
    private final double processingTime;
    private boolean isStarted;
    private boolean isFinished;
    private double processStartTime;
    private double processEndTime;
    private double timeProcessed;
    private double timeToProcessed;
    private Server server;
    private String jobName;

    /**
     * Constructs a new job object from an array of data,
     * where the first element of the array represents the arrival time,
     * and the second element represents the processing time.
     *
     * @param dataArray An array containing the arrival time and processing time of the job.
     */
    public Job(double[] dataArray) {
        this(dataArray[0], dataArray[1]);
    }

    /**
     * Constructs a new job with the specified arrival time and processing time.
     * The job is initially not started and not finished,
     * and the time processed is set to 0.
     *
     * @param arrivalTime    The arrival time of the job.
     * @param processingTime The total processing time required for the job.
     */
    public Job(double arrivalTime, double processingTime) {
        this.arrivalTime = arrivalTime;
        this.processingTime = processingTime;
        this.isStarted = false;
        this.isFinished = false;
        this.timeToProcessed = processingTime;
        this.timeProcessed = 0;
    }

    /**
     * Reads a text file containing job data and extracts the job information to create a sequence of Job objects.
     *
     * @param fileName The name of the file to be read.
     * @return A linked list of Job objects representing the jobs read from the file.
     * Returns null if there was an error during file reading or if the file format is incorrect.
     */
    public static LinkedList<Job> readJobFile(String fileName) {

        // create queue
        LinkedList<Job> jobSequence = new LinkedList<>();

        // Flag to continue processing or not.
        boolean continueOn = true;

        BufferedReader fileGetter = null;
        try {
            fileGetter = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            continueOn = false;
            System.out.println("File not found.");
            System.out.println("Board.read():: unable to open file " + fileName);
        }
        if (continueOn) {
            String currLine;
            int jobCount = 0;
            try {
                Pattern charPattern = Pattern.compile("\\D+,\\D+");
                Pattern numPattern = Pattern.compile("((\\d+\\.\\d+)|\\d+),((\\d+\\.\\d+)|\\d+)");
                do {
                    // Read a line from the file.
                    currLine = fileGetter.readLine();

                    // Exit the loop if the end of the file is reached or the board is fully read.
                    if (currLine == null) break;

                    boolean numSearch = false;
                    do {
                        Matcher charMatcher = charPattern.matcher(currLine);
                        if (charMatcher.find()) {
                            currLine = fileGetter.readLine();
                        } else {
                            numSearch = true;
                        }
                        if (currLine == null) break;
                    } while (!numSearch);
                    if (currLine == null) break;

                    Matcher matcher = numPattern.matcher(currLine);
                    ArrayList<String> matchList = new ArrayList<>();
                    while (matcher.find()) {
                        matchList.add(matcher.group());
                    }
                    // Check if there's exactly one match in the line.
                    if (matchList.size() == 1) {
                        String[] split = matchList.get(0).split(",");
                        double[] splitDouble = {Double.parseDouble(split[0]), Double.parseDouble(split[1])};

                        // Create a Job object from the data and add it to the job sequence.
                        Job tempJob = new Job(splitDouble);
                        tempJob.setJobName(fileName + "_job_" + jobCount);
                        jobSequence.offer(tempJob);
                        jobCount++;
                    } else {
                        System.out.println("File not of correct format.");
                        throw new IOException("File not of correct format.");
                    }
                } while (true);

                // Close the file reader.
                fileGetter.close();

                return jobSequence;
            } catch (IOException e) {
                // Handle any IO exceptions, log an error message.
                System.out.println("Read Error.");
                System.out.println("JobReader.read():: error reading file " + fileName);
            } catch (Exception e) {
                // Handle other exceptions, log the error message.
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Gets the server assigned to the Job.
     *
     * @return the server assigned to the Job.
     */
    public Server getServer() {
        return server;
    }

    /**
     * Sets the server for the job so that time keeping events are more properly handled.
     * Also, just a good reference to have.
     *
     * @param server the server to assign.
     */
    public void setServer(Server server) {
        this.server = server;
    }

    /**
     * Returns the arrival time of this job.
     *
     * @return The arrival time of the job.
     */
    public double getArrivalTime() {
        return this.arrivalTime;
    }

    /**
     * Returns the total necessary processing time of the job.
     *
     * @return The total processing time required for this job.
     */
    public double getTotalProcessingTime() {
        return this.processingTime;
    }

    /**
     * Returns the time spent working on this job so far.
     *
     * @return The time already spent processing this job.
     */
    public double getTimeProcessed() {
        return timeProcessed;
    }

    /**
     * Returns the necessary time remaining to spend working on this job.
     *
     * @return The remaining time needed to complete this job.
     */
    public double getTimeRemaining() {
        return timeToProcessed;
    }

    /**
     * Returns true if this job has been run to completion.
     *
     * @return True if the job has been completed; otherwise, false.
     */
    public boolean isFinished() {
        return this.isFinished;
    }

    /**
     * Sets the completion status of the job.
     *
     * @param finished True if the job is finished; otherwise, false.
     */
    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    /**
     * Returns the time when the job was completed.
     *
     * @return The completion time of the job.
     * @throws IllegalStateException If the job is not finished.
     */
    public double getFinishTime() {
        if (!isFinished()) throw new IllegalStateException("Job is not finished.");
        return this.processEndTime;
    }

    /**
     * Sets the time when the job was completed.
     * Use of synchronized is important here in the case of multithreaded applications.
     * As our simulated case is heavily multithreaded, this ensures that our queue is properly maintained.
     *
     * @param time The time when the job was finished.
     * @throws IllegalStateException If the job is already finished.
     */
    public void setFinishTime(double time) {
        if (isFinished()) throw new IllegalStateException("Job is already finished.");
        this.processEndTime = time;
        setFinished(true);
        server.setCurrentJob(null);
        server.getActiveJobs().poll();
        if (server.getJobQueue().isEmpty()) {
            System.out.println("Job queue is empty");
        }
        synchronized (server.getJobQueue()) {
            server.getJobQueue().poll();
        }
        server.getJobsProcessed().offer(this);
        server.incrementJobsProcessedCount();
        server.getWaitTimePerJob().offer(totalTimeLapsed());
        server.incrementTotalWaitTime(totalTimeLapsed());
        server.incrementCurrentRemainingWork(-getTotalProcessingTime());
    }


    /**
     * Returns the difference in time between the arrival and start times of this job.
     *
     * @return The time spent in the queue before processing started.
     */
    public double timeInQueue() {
        if (!isStarted()) return server.getTime() - this.arrivalTime;
        else return this.processStartTime - this.arrivalTime;
    }

    /**
     * Returns the difference in time between the arrival and end times of this job.
     *
     * @return The total time elapsed from arrival to completion.
     */
    public double totalTimeLapsed() {
        if (!isFinished()) return server.getTime() - this.arrivalTime;
        else return this.processEndTime - this.arrivalTime;
    }

    /**
     * Processes this job for the specified time units of time, updating the necessary fields.
     *
     * @param time The time to process the job.
     * @throws IllegalStateException If the job is already finished.
     */
    public void process(double time) {
        // Check if the job is already finished
        if (isFinished()) throw new IllegalStateException("Cannot process finished job.");

        // If the job has not started, set the start time
        if (!isStarted()) setStartTime(server.getTime());

        double timeRemaining = this.processingTime - this.timeProcessed;
        double elapsed;

        // Check if the specified time is less than the remaining time.
        if (time < timeRemaining) {
            this.timeProcessed += time;
            elapsed = time;
        } else {
            this.timeProcessed = processingTime;
            elapsed = timeRemaining;
        }

        // Update the server's time if it's not in simulated mode
        if (!server.isSimulated()) {
            server.incrementTime(elapsed);
        } else {
            try {
                // Simulate time passage using TimeUnit.NANOSECONDS.sleep()
                TimeUnit.NANOSECONDS.sleep((long) (time * 1000000));
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        // Check if the job has been fully processed and set the finish time
        if (this.timeProcessed >= this.processingTime) {
            if (timeRemaining < 0) this.timeProcessed = this.processingTime;
            setFinishTime(server.getTime());
        }

        timeToProcessed = timeRemaining;
    }


    /**
     * Completes the processing of this job by processing the remaining time.
     * Equivalent to calling `process` with the remaining processing time.
     */
    public void processComplete() {
        process(timeToProcessed);
    }

    /**
     * Checks if this job has started processing.
     *
     * @return True if the job has started processing, false otherwise.
     */
    public boolean isStarted() {
        return isStarted;
    }

    /**
     * Sets the status of whether this job has started processing.
     *
     * @param started True if the job has started processing, false otherwise.
     */
    public void setStarted(boolean started) {
        isStarted = started;
    }

    /**
     * Returns a string representation of the job's information, including its name, server,
     * arrival time, processing time, start time (if applicable), finish time (if applicable),
     * time processed, time remaining, time spent in the queue, and total time lapsed.
     *
     * @return A formatted string containing job information.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nJob: ").append(getJobName()).append("\n");
        sb.append("Server: ").append(getServer()).append("\n");
        sb.append("arrivalTime: ").append(getArrivalTime()).append("\n");
        sb.append("processingTime: ").append(getTotalProcessingTime()).append("\n");
        sb.append("isStarted: ").append(isStarted()).append("\n");
        if (isStarted()) sb.append("Start Time: ").append(getStartTime()).append("\n");
        sb.append("isFinished: ").append(isFinished()).append("\n");
        if (isFinished()) sb.append("Finished Time: ").append(getFinishTime()).append("\n");
        sb.append("timeProcessed: ").append(getTimeProcessed()).append("\n");
        sb.append("timeRemaining: ").append(getTimeRemaining()).append("\n");
        sb.append("queueTime: ").append(timeInQueue()).append("\n");
        sb.append("TotalTime: ").append(totalTimeLapsed()).append("\n");
        return sb.toString();
    }

    /**
     * Gets the name of this job.
     *
     * @return The name of the job.
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Sets the name of this job.
     *
     * @param jobName The name to set for the job.
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Gets the start time of this job's processing.
     *
     * @return The start time of job processing.
     */
    public double getStartTime() {
        return processStartTime;
    }

    /**
     * Sets the start time of this job's processing.
     * Use of synchronized is important here in the case of multithreaded applications.
     *      * As our simulated case is heavily multithreaded, this ensures that our queue is properly maintained.
     *
     * @param time The start time to set for job processing.
     * @throws IllegalStateException If the job's processing has already started.
     */
    public void setStartTime(double time) {
        if (isStarted()) throw new IllegalStateException("Process already started");
        setStarted(true);
        processStartTime = time;
        server.setCurrentJob(this);
        synchronized (server.getWaitingJobs()) {
            server.getWaitingJobs().poll();
        }
        server.getActiveJobs().offer(this);
    }
}
