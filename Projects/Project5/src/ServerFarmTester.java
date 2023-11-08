/*
This is like the standard thing, but better.
Try the visualization on the simulations it's pretty cool.
Basically, the simulation runs a server on each thread pretty cool.
One thing though, obviously, be mindful of how many cores you have.
I have 32 cores and 64 threads, thus I can execute 30 concurrent threads pretty easily.
I can include a video to demonstrate.
*/

/**
 * The ServerFarmTester class serves as a testing program
 * for evaluating different job dispatching strategies in a server farm simulation.
 * This program allows users to select a dispatcher type (Random, Round Robin, Shortest Queue,
 * or Least Work) and specify whether to enable multithreaded real-time simulation.
 * It then runs simulations using the selected dispatcher and prints the results.
 * Users can choose the simulation mode by specifying "true"
 * to enable multithreaded real-time simulation or "false" to use single-threaded simulation.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class ServerFarmTester {

    /**
     * The main method of the testing program.
     * It accepts command line arguments to specify the dispatcher type and simulation state.
     * Users can select one of the dispatcher types and specify "true"
     * to enable multithreaded real-time simulation or "false" for single-threaded simulation.
     * The program runs simulations with the selected dispatcher and prints the results.
     *
     * @param args Command line arguments. It should include the dispatcher type and an optional parameter for simulation state.
     * @throws IllegalArgumentException If an invalid dispatcher type or an incorrect number of command line arguments is provided.
     */
    public static void main(String[] args) {

        //JobDispatcher jd = new RandomDispatcher(30 , true, false);
        //JobDispatcher jd = new RoundRobinDispatcher(30 , true, true);
        //JobDispatcher jd = new ShortestQueueDispatcher(30 , true, true);
        //JobDispatcher jd = new LeastWorkDispatcher(30 , true, true);
        //LinkedList<Job> jobs = Job.readJobFile("jobs.txt");
        //LinkedList<Job> jobs = Job.readJobFile("JobSequence_3_100_short.txt");
        boolean simulationState = false;
        if (args.length == 0) throw new IllegalStateException("""
                                    
                To use pass in a dispatcher type, "random", "round", "shortest", or "least".
                Then optionally pass in a value for simulation state, "true" or "false".
                The default value is false but true will enable a multithreaded realtime simulation.""");
        if (args.length == 2) simulationState = Boolean.parseBoolean(args[1]);
        JobDispatcher jd = switch (args[0]) {
            case "random" -> new RandomDispatcher(15, true, simulationState);
            case "round" -> new RoundRobinDispatcher(15, true, simulationState);
            case "shortest" -> new ShortestQueueDispatcher(15, true, simulationState);
            case "least" -> new LeastWorkDispatcher(15, true, simulationState);
            default -> throw new IllegalStateException("""
                                        
                    To use pass in a dispatcher type, "random", "round", "shortest", or "least".\s
                     Then optionally pass in a value for simulation state, "true" or "false".\s
                     The default value is false but true will enable a multithreaded realtime simulation.""");
        };
        String fileName = (simulationState) ? "smallerTest.txt" : "jobs.txt";
        LinkedList<Job> jobs = Job.readJobFile(fileName);
        jd.handleJobs(jobs);

    }

}
