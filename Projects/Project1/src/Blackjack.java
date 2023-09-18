/*
file name: Blackjack.java
Author: Roman Schiffino
last modified: 9/18/2023

Different representations than standard but at the end of the day just slight mod of standard language.
*/

import java.util.*;

/**
 * @author      Roman Schiffino <rjschi24@colby.edu>
 * @version     1.1
 * @since       1.1
 */
public class Blackjack {

    //Creates a multiDeck for larger games. This is a hashMap to keep everything organized.
    HashMap<String, Deck> deckMap = new HashMap<>();
    //Multi-deck that's shuffle-able.
    Deck.MultiDeck multiDeck = new Deck.MultiDeck();

    //Creates a handMap for tracking player hands.
    HashMap<String, Hand> handMap = new HashMap<>();
    //Creates a handMap for tracking player hands.
    static ArrayList<Hand> dealList = new ArrayList<>();

    int reshuffleCutOff;
    int playerCount;
    int deckCount;
    boolean enableHighLow;
    boolean interactiveState;
    boolean npcAIState;
    boolean advancedAIState;
    boolean debugInfoState;
    boolean simulationState;
    boolean anotherGame;
    ArrayList<Boolean> decisionMatrix = new ArrayList<>();
    HashMap<String,Object> outputData = new HashMap<>();

    /**
     * Initializes most basic version of Blackjack with all default parameters.
     */
    public Blackjack() {

        //Basic version 3 players.
        new Blackjack(15);
    }

    /**
     * Initializes basic version of Blackjack with n players.
     *
     * @param reshuffleCutOff the amount of cards in the deck before which the game will reshuffle.
     */
    public Blackjack(int reshuffleCutOff) {

        //Basic version n cards before reshuffle, 1 player, 1 deck, high ace, npcAI enabled, low level.
        new Blackjack(reshuffleCutOff,1,1,false,false,true, false,false, false);
    }

    /**
     * Initializes advanced version of Blackjack with n players, m decks, and with optional highLow settings.
     *
     * @param reshuffleCut the amount of cards in the deck before which the game will reshuffle.
     * @param players the amount of players.
     * @param decks the amount of decks to play with.
     * @param highLow optional high-low ace setting.
     * @param debugger enables debugger output.
     */
    public Blackjack(int reshuffleCut, int players, int decks, boolean highLow, boolean interactive, boolean npcAI,
                     boolean advancedAI, boolean debugger, boolean simulation) {

        //Stores init conditions.
        reshuffleCutOff = reshuffleCut;
        playerCount = players;
        deckCount = decks;
        enableHighLow = highLow;
        interactiveState = interactive;
        npcAIState = npcAI;
        advancedAIState = advancedAI;
        debugInfoState = debugger;
        simulationState = simulation;

        //Create decks and multiDeck.
        deckCreator();

        //Create hands and handMap.
        Hand dealerHand = new Hand();
        //Store decisions.
        decisionMatrix.add(true);
        handMap.put("dealerHand",dealerHand);
        for (int i=1; i <= playerCount; i++) {
            decisionMatrix.add(true);
            String handName = "hand" + i;
            Hand tempHand = new Hand();
            tempHand.handName = handName;
            handMap.put(handName, tempHand);
            dealList.add(tempHand);
        }
        //Dealer is always last, typical rules mean that the dealer deals clockwise ending with themselves.
        dealList.add(dealerHand);

        if (!simulationState) {
            anotherGame = true;
            do {
                outputData = singleGame();
                Scanner input = new Scanner(System.in);
                String answer;
                boolean done = false;
                do {
                    System.out.println("Do you want another hand? (Y/N)");
                    answer = input.nextLine();
                    if (Objects.equals(answer, "Y")) {
                        done = true;
                    } else if (Objects.equals(answer, "N")) {
                        anotherGame = false;
                        done = true;
                    } else if (Objects.equals(answer, "")) {
                        System.out.println("Please type \"Y\" or \"N\".");
                    } else {
                        System.out.println("Input only \"Y\" or \"N\".");
                    }
                } while (!done);
            } while (anotherGame);
        }
    }

    /**
     * Creates deck based on parameters defined by game initialization.
     * <p>
     * Specifically creates an instance n = deckCount instances of the deck class before adding these to the
     * multideck created at the beginning of the program then shuffles.
     */
    public void deckCreator() {
        for (int i=0; i < deckCount; i++) {
            String deckName = "deck" + i;
            Deck tempDeck = new Deck();
            deckMap.put(deckName, tempDeck);
            multiDeck.addAll(tempDeck.cardDeck);
        }
        multiDeck.shuffle();
    }

    /**
     * Runs a single instance of the game and returns a Hashmap containing various game stats.
     * <p>
     * Deals a public card to each player and then deals hole card to dealer (private card). Then
     * creates decision matrix to store decisions from each hand. Will continue to deal as long as
     * decisionMatrix contains true, will only deal to hands that state true.
     *
     * @return a HashMap containing various game stats tied to string names.
     */
    public HashMap<String,Object> singleGame() {
        reset();
        //Deals first public card
        for (int i =0;i<(dealList.size()-2);i++) {
            dealList.get(i).add(multiDeck.deal(),false);
        }
        //Deals hole card.
        dealList.get(dealList.size()-1).add(multiDeck.deal(),true);
        //Deals first public card.
        do {
            for (Hand hand : dealList) {
                if (decisionMatrix.get(dealList.indexOf(hand))) {
                    hand.add(multiDeck.deal(), false);
                }
            }
            if (!simulationState) {
                System.out.println("Your Hand: " + handMap.get("hand1").toStringFancy(enableHighLow));
                for (int i = 2; i <= playerCount; i++) {
                    String playerName = "player" + i + " Hand: ";
                    String handName = "hand" + i;
                    System.out.println(playerName + handMap.get(handName).publicToStringFancy(enableHighLow));
                }
                System.out.println("Dealer Hand: " + handMap.get("dealerHand").publicToStringFancy(enableHighLow));
            }
            if (interactiveState && !npcAIState) {
                Scanner inputListener = new Scanner(System.in);
                String decisionTxt = "";
                boolean done = false;
                for (Hand hand : dealList) {
                    boolean testValue = hand.equals(handMap.get("dealerHand"));
                    if (!testValue) {
                        getCardDecision(inputListener, decisionTxt, hand,done);
                    }
                    else {
                        decisionMatrix.set(playerCount,dealerAI(hand));
                    }
                }
            }
            else if (interactiveState && npcAIState) {
                Scanner inputListener = new Scanner(System.in);
                String decisionTxt = "";
                boolean done = false;
                for (Hand hand : dealList) {
                    boolean testValue = (hand.equals(handMap.get("dealerHand")) || hand.equals(handMap.get("hand1")));
                    if (!testValue) {
                        decisionMatrix.set(dealList.indexOf(hand),decisionMaker(hand));
                    }
                    else if (hand.equals(dealList.get(playerCount))) {
                        decisionMatrix.set(playerCount,dealerAI(hand));
                    }
                    else {
                        getCardDecision(inputListener, decisionTxt, hand, done);
                    }
                }
            }
            else {
                for (Hand hand : dealList) {
                    boolean testValue = (hand.equals(dealList.get(playerCount)));
                    if (!testValue) {
                        decisionMatrix.set(dealList.indexOf(hand),decisionMaker(hand));
                    }
                    else {
                        decisionMatrix.set(playerCount,dealerAI(hand));
                    }
                }
            }
        }
        while (decisionMatrix.contains(true));

        //This prints out the game state after dealing as well as extracting and repackaging game data.
        ArrayList<Object> gameState = getGameState();
        HashMap<String, Object> gameDataMap = (HashMap<String, Object>) gameState.get(1);
        String gameStateString = gameState.get(0).toString();
        if (!simulationState) {
            System.out.println(gameStateString);
        }
        return gameDataMap;
    }


    /**
     * Packages string representation of gameState along with game data into list and passes list out.
     * <p>
     * String contains each hand and its current value, later determines winner and appends string corresponding
     * to who has won.
     * <p>
     * HashMap contains various game data values:
     * <p>
     * "winState" : whether game was win, loss, or tie.
     * <p>
     * "winners" : list of winners.
     * <p>
     * "hands" : list of all hands.
     * <p>
     * "winners.forEach((hand) -> hand.handName)" : hand of each Winner.
     *
     * @return list containing string representation of gameState in index 0 and HashMap of game Data in index 1.
     */
    public ArrayList<Object> getGameState() {
        ArrayList<Object> outList = new ArrayList<>();
        HashMap<String, Object> outMap = new HashMap<>();
        StringBuilder outStr = new StringBuilder();
        outStr.append("Your Hand: ").append(handMap.get("hand1").toStringFancy(enableHighLow)).append("\n");
        for (int i = 2; i <= playerCount; i++) {
            String playerName = "player" + i + " Hand: ";
            String handName = "hand" + i;
            outStr.append(playerName).append(handMap.get(handName).toStringFancy(enableHighLow)).append("\n");
        }
        outStr.append("Dealer Hand: ").append(handMap.get("dealerHand").toStringFancy(enableHighLow)).append("\n");
        TreeMap<Integer, ArrayList<Hand>> scoreMap = new TreeMap<>();
        for (Hand hand : dealList) {
            if (hand.getTotalValue(enableHighLow) <= 21) {
                if (!scoreMap.containsKey(hand.getTotalValue(enableHighLow))) {
                    ArrayList<Hand> equalPointArray = new ArrayList<>();
                    scoreMap.put(hand.getTotalValue(enableHighLow), equalPointArray);
                }
                scoreMap.get(hand.getTotalValue(enableHighLow)).add(hand);
            }

        }
        StringBuilder outVar = new StringBuilder();
        int winState = 0;
        ArrayList<Hand> winners = null;
        boolean winnersExist = true;
        try {
            int topScore = scoreMap.lastKey();
            winners = new ArrayList<>(scoreMap.get(topScore));
            if (winners.size() > 1) {
                for (Hand hand : winners) {
                    if (winners.indexOf(hand) == 0 && dealList.indexOf(hand) == 0) {
                        outVar.append("Tie! You, ");
                        winState = 0;
                    } else if (winners.indexOf(hand) == 0) {
                        outVar.append("Loss! Player ").append(dealList.indexOf(hand) + 1).append(", ");
                        winState = -1;
                    } else if (!(winners.indexOf(hand) == (winners.size() - 1))) {
                        outVar.append("player ").append(dealList.indexOf(hand) + 1).append(", ");
                    } else if (dealList.indexOf(hand) != (dealList.size() - 1)) {
                        outVar.append("and player ").append(dealList.indexOf(hand) + 1).append(" ");
                    } else {
                        outVar.append("and the dealer ");
                    }
                }
                outVar.append("have won.");
            } else {
                if (dealList.indexOf(winners.get(0)) == 0) {
                    outVar.append("Victory! You have won.");
                    winState = 1;
                } else if (dealList.indexOf(winners.get(0)) != (dealList.size() - 1)) {
                    outVar.append("Loss! Player ").append(dealList.indexOf(winners.get(0)) + 1).append(" has won.");
                    winState = -1;
                } else {
                    outVar.append("Loss! Dealer has won.");
                    winState = -1;
                }
            }
        } catch (Exception NoSuchElementException) {
            outVar.append("Loss! All Players and Dealer have lost.");
            winState = 0;
            winnersExist = false;
        }
        outMap.put("winState", winState);
        outMap.put("winners", winners);
        outMap.put("hands", dealList);
        if (winnersExist) {
            winners.forEach((Hand hand) -> outMap.put(hand.handName, visible(hand)));
        }
        outStr.append(outVar).append("\n");
        outList.add(outStr.toString());
        outList.add(outMap);
        return outList;
    }


    /**
     * When interactiveGame == true, this method is called to determine user input.
     * <p>
     * Basic implementation of scanner to determine userInput. Some user input validation.
     *
     * @param scanner scanner object to use for user input.
     * @param decision decision string to set with scanner.
     * @param hand hand to determine choice for.
     * @param doneFlag whether to continue attempting to get user input.
     */
    public void getCardDecision(Scanner scanner, String decision, Hand hand, boolean doneFlag) {
        do {
            System.out.println("Do you want another card? (Y/N)");
            try {
                decision = scanner.nextLine();
            } catch (Exception IllegalArgumentException) {
                System.out.println("Please Type \"Y\" or \"N\"");
            }
            if (Objects.equals(decision, "Y")) {
                decisionMatrix.set(dealList.indexOf(hand), true);
                doneFlag = true;
            } else if (Objects.equals(decision, "N")) {
                decisionMatrix.set(dealList.indexOf(hand), false);
                doneFlag = true;
            } else {
                System.out.println("Input only \"Y\" or \"N\".");
            }
        } while (!doneFlag);
    }

    /**
     * fetchAll function parameterless, basically default to only get public cards when called without parameter.
     *
     * @return an ArrayList of cards containing the public cards held by all hands.
     */
    public static ArrayList<Card> fetchAll() {
        return fetchAll(false);
    }

    /**
     * fetchAll function base version. Takes a boolean of whether to fetch all cards or just public
     * cards from each hand.
     *
     * @param privateState whether to get private cards in addition to public cards/
     * @return an ArrayList of cards containing public or all cards held in each hand.
     */
    public static ArrayList<Card> fetchAll(boolean privateState) {
        ArrayList<Card> outList = new ArrayList<>();
        if (privateState) {
            for (Hand hand : dealList) {
                outList.addAll(hand.handList);
            }
        }
        else {
                for (Hand hand : dealList) {
                    outList.addAll(hand.publicList);
                }
            }
        return outList;
    }

    /**
     * Function that returns all cards possibly visible to any player.
     * <p>
     * This function calls fetchAll(privateState:false) and then adds the hand that called the functions private list.
     * As such all the information available to the player is contained here.
     *
     * @param hand the hand calling the function, will only add this hands private cards.
     * @return ArrayList of visible cards.
     */
    public static ArrayList<Card> visible(Hand hand) {
        ArrayList<Card> outList = new ArrayList<>(fetchAll());
        outList.addAll(hand.privateList);
        return outList;
    }

    /**
     * Calls AI corresponding to advancedAIState.
     *
     * @param hand hand to call with AI function.
     * @return boolean value returned by corresponding AI function.
     */
    public boolean decisionMaker(Hand hand) {
        boolean decision;
        if (advancedAIState) {
            decision = advancedAI(hand);
        }
        else {
            decision = simpleAI(hand);
        }
        return decision;
    }

    /**
     * Simple AI function that determines hand choice based on only value of hand and whether hand is "hard" or "soft".
     * <p>
     * Basic Implementation of soft hands and hard hands. If hand contains ace that can count as either 1 or 11
     * then the hand is soft and risk tolerance is increased. On the other hand if hand contains no such ace hand is
     * hard and lower risk tolerated.
     *
     * @param hand hand to calculate choice for.
     * @return the decision reached by the AI based on hand state.
     */
    public boolean simpleAI(Hand hand) {
        boolean decision = false;
        ArrayList<Integer> valList = new ArrayList<>();
        for (Card h : hand.handList) {
            valList.add(h.identity);
        }
        if (enableHighLow && (valList.contains(1))) {
            if (hand.getTotalValue(enableHighLow) < 17) {
                decision = true;
            }
        }
        else {
            if (hand.getTotalValue(enableHighLow) < 15) {
                decision = true;
            }
        }
        return decision;
    }

    /**
     * Basic dealerAI function that determines choice for dealer based on game rules.
     * <p>
     * Hits if below 17, stays if greater than or equal to 17.
     * <p>
     * I commented out the part implementing the AI when the card value is over 17, so it just stays after 17.
     *
     * @param hand dealer hand to calculate choice for.
     * @return decision reached by AI for dealer hand.
     */
    public boolean dealerAI(Hand hand) {
        boolean decision;
        if (hand.getTotalValue(enableHighLow) < 17) {
            decision = true;
        }
        else {
            //decision = decisionMaker(hand);
            decision = false;
        }
        return decision;
    }

    /**
     * advancedAI method no extra training parameter, just calls base advancedAI method.
     *
     * @param hand hand to determine choice for.
     * @return decision reached by AI.
     */
    public boolean advancedAI(Hand hand) {
        return ((boolean) advancedAI(hand, false).get(0));
    }

    /**
     * advancedAI method that implements Omega-II card counting algorithm to determine hand choice.
     * <p>
     * Omega-II is a balanced algorithm meaning that the after counting every card in the deck the true count
     * will be zero. Effectively the algorithm gets all the cards visible and seen by the player and for each
     * of these cards a value of either -2, -1, 0, 1, or 2  is added to the running count. The running count is
     * then divided by the amount of decks remaining to determine true count. Decks remaining is determined by
     * dividing remaining cards by 52. This value and the current hand value are both sent to a linear function
     * which determines the betModifier, which, if less than 0.5, will make the decision false.
     * <p>
     * Interestingly enough this AI is pretty lackluster in outcome. The win rate is roughly equivalent to the
     * expected win rate reached by playing perfect basic strategy (~38.9% (at 1,000,000 games)~ vs 42.2%). Lower than what would be
     * expected by counting cards. For this reason I began to implement the machineLearningAI but ran out of time.
     *
     * @param hand hand to determine decision for.
     * @param training whether to output training data (not implemented).
     * @return decision based on betModifier in AI.
     */
    public ArrayList<Object> advancedAI(Hand hand, boolean training) {
        ArrayList<Object> output = new ArrayList<>();
        boolean decision;
        ArrayList<Card> visibleList = new ArrayList<>(visible(hand));
        for (Card card : visibleList) {
            if (!card.counted) {
                hand.countedCardVals.add(card.identity);
            }
        }
        ArrayList<Integer> valList = new ArrayList<>();
        for (Card h : hand.handList) {
            valList.add(h.identity);
        }
        int thresholdMod;
        double balanceMod;
        if (enableHighLow && (valList.contains(1))) {
            thresholdMod = 15;
            balanceMod = 0.015;
        }
        else {
            thresholdMod = 12;
            balanceMod = 0.010;
        }
        int runningCount = 0;
        for (int val : hand.countedCardVals) {
            runningCount += switch (val) {
                case 4,5,6 -> 2;
                case 2,3,7 -> 1;
                case 8,1 -> 0;
                case 9 -> -1;
                case 10,11,12,13 -> -2;
                default -> throw new IllegalStateException("Unexpected value: " + val);
            };
        }
        double cardsRemaining = playerCount + multiDeck.size();
        double decksRemaining = cardsRemaining / 52;
        if (deckCount==1) {
            decksRemaining = 1;
        }
        double trueCount = runningCount / decksRemaining;
        double betModifier = 1 - (trueCount * (balanceMod)) - ((hand.getTotalValue(enableHighLow)-thresholdMod) * (0.195));
        decision = betModifier > 0.5;
        if (hand.getTotalValue(enableHighLow)>=20) {
            decision = false;
        }
        if (hand.getTotalValue(enableHighLow)<=10) {
            decision = true;
        }
        String debugInfo;
        debugInfo = String.format("hand: %s\nTC: %s\nTCMod: %s\nBM: %s\nCC: %s\nCR: %s\n",hand,trueCount,(trueCount * (balanceMod )),betModifier,hand.countedCardVals,cardsRemaining);
        if (debugInfoState) {
            System.out.println(debugInfo);
        }
        output.add(decision);
        output.add(debugInfo);
        return output;
    }

    /**
     * main function which uses userInput to create Blackjack instance corresponding to inputs.
     * <p>
     * Determines player count, deck count, high-low ace state, interactive state, npcAI state, npcAI level,
     * and debugInfo state. Then instantiates game.
     * <p>
     *
     * @param args just arguments genuinely does nothing.
     */
    public static void main(String[] args) {
        Scanner inputListener = new Scanner(System.in);
        System.out.println("Lets play some BlackJack!");
        int playerCount = 0;
        boolean done = false;
        do {
            System.out.println("First, how many Players?");
            try {
                playerCount = inputListener.nextInt();
                done = true;
                inputListener.nextLine();
            }
            catch (Exception InputMismatchException) {
                System.out.println("Input only integer values.");
                inputListener.nextLine();
            }
        } while (!done);

        int deckCount = 0;
        done = false;
        do {
            System.out.println("Next, how many Decks?");
            try {
                deckCount = inputListener.nextInt();
                done = true;
                inputListener.nextLine();
            }
            catch (Exception InputMismatchException) {
                System.out.println("Input only integer values.");
                inputListener.nextLine();
            }
        } while (!done);

        String highLowString;
        boolean highLowState = false;
        done = false;
        do {
            highLowString = "";
            System.out.println("Do you want to play with High-Low Aces? (Y/N)");
            try {
                highLowString = inputListener.nextLine();
            } catch (Exception IllegalArgumentException) {
                System.out.println("Please Type \"Y\" or \"N\"");
            }
            if (Objects.equals(highLowString, "Y")) {
                highLowState = true;
                done = true;
            }
            else if (Objects.equals(highLowString, "N")) {
                done = true;
            }
            else {
                System.out.println("Input only \"Y\" or \"N\".");
            }
        } while(!done);

        String interact;
        boolean interactState = false;
        done = false;
        do {
            System.out.println("Do you want an interactive game? (Y/N)");
            interact = inputListener.nextLine();
            if (Objects.equals(interact, "Y")) {
                interactState = true;
                done = true;
            }
            else if (Objects.equals(interact, "N")) {
                done = true;
            }
            else if (Objects.equals(interact, "")) {
                System.out.println("Please type \"Y\" or \"N\".");
            }
            else {
                System.out.println("Input only \"Y\" or \"N\".");
            }
        } while(!done);

        String npcAIString;
        boolean AIState = false;
        done = false;
        do {
            System.out.println("Do you want to enable npcAI? (Y/N)");
            npcAIString = inputListener.nextLine();
            if (Objects.equals(npcAIString, "Y")) {
                AIState = true;
                done = true;
            }
            else if (Objects.equals(npcAIString, "N")) {
                done = true;
            }
            else if (Objects.equals(npcAIString, "")) {
                System.out.println("Please type \"Y\" or \"N\".");
            }
            else {
                System.out.println("Input only \"Y\" or \"N\".");
            }
        } while(!done);

        String AILevel;
        boolean enableGoodAI = false;
        done = false;
        do {
            System.out.println("Do you want to enable high-level AI? (Y/N)");
            AILevel = inputListener.nextLine();
            if (Objects.equals(AILevel, "Y")) {
                enableGoodAI = true;
                done = true;
            }
            else if (Objects.equals(AILevel, "N")) {
                done = true;
            }
            else if (Objects.equals(AILevel, "")) {
                System.out.println("Please type \"Y\" or \"N\".");
            }
            else {
                System.out.println("Input only \"Y\" or \"N\".");
            }
        } while(!done);

        String debugString;
        boolean debugStateSetter = false;
        done = false;
        do {
            System.out.println("Do you want to enable debug output? (Y/N)");
            debugString = inputListener.nextLine();
            if (Objects.equals(debugString, "Y")) {
                debugStateSetter = true;
                done = true;
            }
            else if (Objects.equals(debugString, "N")) {
                done = true;
            }
            else if (Objects.equals(debugString, "")) {
                System.out.println("Please type \"Y\" or \"N\".");
            }
            else {
                System.out.println("Input only \"Y\" or \"N\".");
            }
        } while(!done);

        int fairReshuffle = (playerCount+1)*6;
        new Blackjack(fairReshuffle, playerCount, deckCount, highLowState,
                interactState, AIState, enableGoodAI, debugStateSetter,false);
    }

    /*
      Not going to lie, pretty much everything from here on I implemented because it was in the expectations for the
      project. I had a vision of how to implement this, so I just freely coded.
      Never really used much of it. But, nevertheless it was coded and it is here.
     */


    /**
     * Resets the game by emptying all hand internal lists and if deck size less than reshuffleCutOff creates
     * new deck.
     * <p>
     * Should reset the game. Both the player Hand and dealer Hand should start with no cards. If the number
     * of cards in the deck is less than the reshuffle cutoff, then the method should create a fresh (complete),
     * shuffled deck. Otherwise, it should not modify the deck, just clear the player and dealer hands.
     *
     */
    public void reset() {
        for (Hand hand : dealList) {
            hand.reset();
        }
        if (multiDeck.size()<reshuffleCutOff) {
            deckCreator();
            for (Hand hand : dealList) {
                hand.countedCardVals.clear();
            }
        }
    }

    /**
     * Should deal out two cards to both players from the Deck.
     */
    public void deal() {
        for (Hand hand : dealList) {
            hand.add(multiDeck.deal());
        }
    }

    /**
     * Have the player draw cards until the total value of the player's hand is equal to or above 16.
     *
     * @return false if the player goes over 21, (bust)
     */
    public boolean playerTurn() {
        return playerTurn(dealList.get(0));
    }

    /**
     * Have the player draw cards until the total value of the player's hand is equal to or above 16.
     *
     * @param player which player to deal to.
     * @return false if the player goes over 21, (bust)
     */
    public boolean playerTurn(Hand player) {
        boolean bustState = true;
        do {
            player.add(multiDeck.deal());
        }
        while (player.getTotalValue(enableHighLow)<=16);
        if (player.getTotalValue(enableHighLow)>21) {
            bustState = false;
        }
        return bustState;
    }

    /**
     * Have the dealer draw cards until the total of the dealer's hand is equal to or above 17.
     *
     * @return false if the dealer goes over 21.
     */
    public boolean dealerTurn() {
        boolean bustState = true;
        do {
            handMap.get("dealerHand").add(multiDeck.deal());
        }
        while (handMap.get("dealerHand").getTotalValue(enableHighLow)<=16);
        if (handMap.get("dealerHand").getTotalValue(enableHighLow)>21) {
            bustState = false;
        }
        return bustState;
    }

    /**
     * Should assign the new cutoff value to the internal reshuffle cutoff field.
     *
     * @param cutoff the number of cards at which to reshuffle;
     */
    public void setReshuffleCutoff(int cutoff) {
        reshuffleCutOff = cutoff;

    }

    /**
     * Returns the current value of the reshuffle cutoff field.
     *
     * @return the reshuffleCutOff, the number of cards in the deck before the deck reshuffles on next Game.
     */
    public int getReshuffleCutoff() {
        return reshuffleCutOff;
    }

    /**
     * Shows the player and dealer hands as well as their current total value.
     *
     * @return returns a String that has represents the state of the game.
     */
    public String toString() {
        return getGameState().get(0).toString();
    }
}
