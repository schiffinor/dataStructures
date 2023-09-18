import java.util.HashMap;

/**
 * @author      Roman Schiffino <rjschi24@colby.edu>
 * @version     1.1
 * @since       1.1
 */
public class Simulation {

    /**
     * main function for simulation runs entire simulation.
     * <p>
     * Creates Blackjack game instance with 1 player, 1 deck, a reshuffleCutoff of 12, highLow = true, non-interactive
     * with advanced AI. The game then instantiates trackers for wins, ties, and losses. Next runtimes are defined,
     * in this case 10,000. At this point a for loop that creates a Hashmap equal to the output from the singleGame
     * method is created and the winState is extracted from each runtime. Each time the loop runs the current
     * runtime, and the outcome of the game are printed. The win, tie, and loss trackers are also incremented
     * depending on the game output. Finally, the loop finishes and the statistics are outputted.
     */
    public static void main(String[] args) {
        Blackjack gameInstance = new Blackjack(12,1,1,true,false,true,true,false,true);
        int wins = 0;
        int ties = 0;
        int losses = 0;
        int runtimes = 10000;
        for (int i = 0; i < runtimes; i++) {
            HashMap<String, Object> gameData = gameInstance.singleGame();
            gameInstance.reset();
            String state;
            if ((int) gameData.get("winState")==-1) {
                losses++;
                state = "Loss! ";
            } else if ((int) gameData.get("winState")==0) {
                ties++;
                state = "Tie! ";
            }
            else {
                wins++;
                state = "Win! ";
            }
            System.out.println(i);
            System.out.println(state);

        }
        gameInstance.anotherGame = false;
        String output = String.format("Wins: %s\nTies: %s\nLosses: %s\nWin Rate: %s%%\n",wins,ties,losses,(((double) wins)/runtimes*100));
        System.out.println(output);
    }
}
