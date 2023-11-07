import java.util.Arrays;

/**
TODO 
*/

public class ServerFarmTester {
    
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
            case "random" -> new RandomDispatcher(15, true,simulationState);
            case "round" -> new RoundRobinDispatcher(15, true,simulationState);
            case "shortest" -> new ShortestQueueDispatcher(15,true,simulationState);
            case "least" -> new LeastWorkDispatcher(15,true,simulationState);
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
