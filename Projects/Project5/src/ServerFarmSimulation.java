public class ServerFarmSimulation {

    public static void main(String[] args) {
        boolean simulationState = false;
        if (args.length < 2) throw new IllegalStateException("""
                    
                    To use pass in a server count and a file name.
                    """);
        int serverCount = Integer.parseInt(args[0]);
        String fileName = args[1];
        JobDispatcher jd = null;
        for (int i = 0; i < 4; i++) {
            jd = switch (i) {
                case 0 -> new RandomDispatcher(serverCount, false,simulationState);
                case 1 -> new RoundRobinDispatcher(serverCount, false,simulationState);
                case 2 -> new ShortestQueueDispatcher(serverCount,false,simulationState);
                case 3 -> new LeastWorkDispatcher(serverCount,false,simulationState);
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
