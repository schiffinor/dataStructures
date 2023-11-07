public class DebugTester {
    public static void main(String[] args) {
        //JobDispatcher jd = new RandomDispatcher(15 , true, true);
        //JobDispatcher jd = new RoundRobinDispatcher(30 , true, true);
        //JobDispatcher jd = new ShortestQueueDispatcher(30 , true, true);
        JobDispatcher jd = new LeastWorkDispatcher(30 , true, true);
        LinkedList<Job> jobs = Job.readJobFile("smallerTest.txt");
        //LinkedList<Job> jobs = Job.readJobFile("JobSequence_3_100_short.txt");
        jd.handleJobs(jobs);
    }
}
