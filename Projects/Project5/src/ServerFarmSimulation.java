/**
 * The ServerFarmSimulation class represents a simulation program
 * for evaluating different job dispatching strategies using server farms.
 * It allows users to specify the server count and input file name
 * to perform simulations with different dispatchers and compare their results.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class ServerFarmSimulation {

    /**
     * The main method of the simulation program.
     * It parses command line arguments to determine the server count and input file name.
     * It runs simulations with four different dispatchers (Random, Round Robin, Shortest Queue, and Least Work),
     * and prints the average waiting time for each dispatcher's simulation.
     *
     * @param args Command line arguments. It should include the server count and the input file name.
     * @throws IllegalArgumentException If the number of command line arguments is insufficient or if an invalid dispatcher type is specified.
     */
    public static void main(String[] args) {
        boolean simulationState = false;
        if (args.length < 2) throw new IllegalStateException("""
                                    
                To use pass in a server count and a file name.
                """);
        int serverCount = Integer.parseInt(args[0]);
        String fileName = args[1];
        JobDispatcher jd;
        for (int i = 0; i < 4; i++) {
            jd = switch (i) {
                case 0 -> new RandomDispatcher(serverCount, false, simulationState);
                case 1 -> new RoundRobinDispatcher(serverCount, false, simulationState);
                case 2 -> new ShortestQueueDispatcher(serverCount, false, simulationState);
                case 3 -> new LeastWorkDispatcher(serverCount, false, simulationState);
                default -> throw new IllegalStateException("""
                                            
                        To use pass in a dispatcher type, "random", "round", "shortest", or "least".\s
                        """);
            };
            LinkedList<Job> jobs = Job.readJobFile(fileName);
            jd.handleJobs(jobs);
            System.out.println(switch (i) {
                case 0 -> "random: ";
                case 1 -> "round: ";
                case 2 -> "shortest: ";
                case 3 -> "least: ";
                default -> throw new IllegalStateException("Unexpected value: " + i);
            } + jd.getAverageWaitingTime());
        }
    }
}
