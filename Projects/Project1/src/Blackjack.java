/*
file name: Blackjack.java
Author: Roman Schiffino
last modified:

Different representations than standard but at the end of the day just slight mod of standard language.
*/

import java.util.*;

public class Blackjack {

    //Creates a multiDeck for larger games. This is a hashMap to keep everything organized.
    HashMap<String, Deck> deckMap = new HashMap<>();
    //Multi-deck that's shuffle-able.
    Deck.MultiDeck multiDeck = new Deck.MultiDeck();

    //Creates a handMap for tracking player hands.
    HashMap<String, Hand> handMap = new HashMap<>();
    //Creates a handMap for tracking player hands.
    ArrayList<Hand> dealList = new ArrayList<>();

    int reshuffleCutOff;
    int playerCount;
    int deckCount;
    boolean enableHighLow;
    boolean interactiveState;
    boolean npcAIState;
    boolean advancedAIState;
    boolean debugInfoState;
    ArrayList<Boolean> decisionMatrix = new ArrayList<>();

    /**
     * Initializes most basic version of Blackjack with all default parameters.
     */
    public Blackjack() {

        //Basic version 3 players.
        new Blackjack(5);
    }

    /**
     * Initializes basic version of Blackjack with n players.
     * @param reshuffleCutOff the amount of cards in the deck before which the game will reshuffle.
     */
    public Blackjack(int reshuffleCutOff) {

        //Basic version n cards before reshuffle, 1 player, 1 deck, high ace, npcAI enabled, low level.
        new Blackjack(reshuffleCutOff,1,1,false,false,true, false,false);
    }

    /**
     * Initializes advanced version of Blackjack with n players, m decks, and with optional highLow settings.
     * @param reshuffleCut the amount of cards in the deck before which the game will reshuffle.
     * @param players the amount of players.
     * @param decks the amount of decks to play with.
     * @param highLow optional high-low ace setting.
     */
    public Blackjack(int reshuffleCut, int players, int decks, boolean highLow, boolean interactive, boolean npcAI,
                     boolean advancedAI, boolean debugger) {

        //Stores init conditions.
        reshuffleCutOff = reshuffleCut;
        playerCount = players;
        deckCount = decks;
        enableHighLow = highLow;
        interactiveState = interactive;
        npcAIState = npcAI;
        advancedAIState = advancedAI;
        debugInfoState = debugger;

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
            handMap.put(handName, tempHand);
            dealList.add(tempHand);
        }
        //Dealer is always last, typical rules mean that the dealer deals clockwise ending with themselves.
        dealList.add(dealerHand);

        boolean anotherGame = true;
        do {
            singleGame();
            reset();
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

    public void deckCreator() {
        for (int i=1; i < deckCount; i++) {
            String deckName = "deck" + i;
            Deck tempDeck = new Deck();
            deckMap.put(deckName, tempDeck);
            multiDeck.addAll(tempDeck.cardDeck);
        }
        multiDeck.shuffle();
    }

    public void singleGame() {
        //Deals first secret card.
        for (Hand hand : dealList) {
            hand.add(multiDeck.deal(),true);
        }
        //Deals first public card.
        do {
            for (Hand hand : dealList) {
                if (decisionMatrix.get(dealList.indexOf(hand))) {
                    hand.add(multiDeck.deal(), false);
                }
            }
            System.out.println("Your Hand: "+handMap.get("hand1").toStringFancy(enableHighLow));
            for (int i=2; i <= playerCount; i++) {
                String playerName = "player" + i + " Hand: ";
                String handName = "hand" + i;
                System.out.println(playerName + handMap.get(handName).publicToStringFancy(enableHighLow));
            }
            System.out.println("Dealer Hand: "+handMap.get("dealerHand").publicToStringFancy(enableHighLow));
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

        //This prints out the game state after dealing.
        String gameState = getGameState();
        System.out.println(gameState);
    }

    private String getGameState() {
        StringBuilder outStr = new StringBuilder();
        outStr.append("Your Hand: ").append(handMap.get("hand1").toStringFancy(enableHighLow)).append("\n");
        for (int i=2; i <= playerCount; i++) {
            String playerName = "player" + i + " Hand: ";
            String handName = "hand" + i;
            outStr.append(playerName).append(handMap.get(handName).toStringFancy(enableHighLow)).append("\n");
        }
        outStr.append("Dealer Hand: ").append(handMap.get("dealerHand").toStringFancy(enableHighLow)).append("\n");
        TreeMap<Integer,ArrayList<Hand>> scoreMap = new TreeMap<>();
        for (Hand hand : dealList) {
            if (hand.getTotalValue(enableHighLow)<=21) {
                if (!scoreMap.containsKey(hand.getTotalValue(enableHighLow))) {
                    ArrayList<Hand> equalPointArray = new ArrayList<>();
                    scoreMap.put(hand.getTotalValue(enableHighLow), equalPointArray);
                }
                scoreMap.get(hand.getTotalValue(enableHighLow)).add(hand);
            }

        }
        int topScore = scoreMap.lastKey();
        ArrayList<Hand> winners = new ArrayList<>(scoreMap.get(topScore));
        StringBuilder outVar = new StringBuilder();
        if (winners.size() > 1) {
            for (Hand hand : winners) {
                if (winners.indexOf(hand) == 0 && dealList.indexOf(hand)==0) {
                    outVar.append("Tie! You, ");
                }
                else if (winners.indexOf(hand) == 0) {
                    outVar.append("Loss! Player ").append(dealList.indexOf(hand)+1).append(", ");
                }
                else if (!(winners.indexOf(hand) == (winners.size()-1))) {
                    outVar.append("player ").append(dealList.indexOf(hand) + 1).append(", ");
                }
                else if (dealList.indexOf(hand) != (dealList.size()-1)) {
                    outVar.append("and player ").append(dealList.indexOf(hand) + 1).append(" ");
                }
                else {
                    outVar.append("and the dealer ");
                }
            }
            outVar.append("have won.");
        }
        else {
            if (dealList.indexOf(winners.get(0))==0) {
                outVar.append("Victory! You have won.");
            }
            else {
                outVar.append("Loss! Player ").append(dealList.indexOf(winners.get(0))+1).append(" has won.");
            }
        }
        outStr.append(outVar).append("\n");
        return outStr.toString();
    }

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

    public ArrayList<Card> fetchAll() {
        return fetchAll(false);
    }

    public ArrayList<Card> fetchAll(boolean privateState) {
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

    public ArrayList<Card> visible(Hand hand) {
        ArrayList<Card> outList = new ArrayList<>(fetchAll());
        outList.addAll(hand.privateList);
        return outList;
    }

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

    public boolean dealerAI(Hand hand) {
        boolean decision;
        if (hand.getTotalValue(enableHighLow) < 17) {
            decision = true;
        }
        else {
            decision = decisionMaker(hand);
        }
        return decision;
    }

    public boolean advancedAI(Hand hand) {
        boolean decision;
        ArrayList<Integer> countedCardVals = new ArrayList<>();
        ArrayList<Card> visibleList = new ArrayList<>(visible(hand));
        for (Card card : visibleList) {
            if (!card.counted) {
                countedCardVals.add(card.identity);
            }
        }
        int runningCount = 0;
        for (int val : countedCardVals) {
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
        double trueCount = runningCount / decksRemaining;
        double betModifier = 1 - (trueCount * (0.04 )) - ((hand.getTotalValue(enableHighLow)-10) * (0.1));
        decision = betModifier > 0.5;
        if (hand.getTotalValue(enableHighLow)>=20) {
            decision = false;
        }
        if (hand.getTotalValue(enableHighLow)<=10) {
            decision = true;
        }
        if (debugInfoState) {
            System.out.println("hand: "+hand+" TC: "+trueCount+" TCMod: "+(trueCount * (0.04 ))+" BM: "+betModifier+" CC: "+countedCardVals+" CR: "+cardsRemaining);
        }
        return decision;
    }

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

         new Blackjack(5, playerCount, deckCount, highLowState,
                interactState, AIState, enableGoodAI, debugStateSetter);
    }

    /**
     * Not going to lie, pretty much everything from here on I implemented because it was in the expectations for the
     * project. I had a vision of how to implement this, so I just freely wrote.
     * Never really used much of it. But, nevertheless it was coded and it is here.
     */

    /**
     * Should reset the game. Both the player Hand and dealer Hand should start with no cards.
     * If the number of cards in the deck is less than the reshuffle cutoff,
     * then the method should create a fresh (complete), shuffled deck.
     * Otherwise, it should not modify the deck, just clear the player and dealer hands.
     */
    public void reset() {
        for (Hand hand : dealList) {
            hand.reset();
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
     * @return false if the player goes over 21, (bust)
     */
    public boolean playerTurn() {
        return playerTurn(dealList.get(0));
    }

    /**
     * have the player draw cards until the total value of the player's hand is equal to or above 16.
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
     * @param cutoff the number of cards at which to reshuffle;
     */
    public void setReshuffleCutoff(int cutoff) {
        reshuffleCutOff = cutoff;

    }

    /**
     * returns the current value of the reshuffle cutoff field.
     * @return the reshuffleCutOff, the number of cards in the deck before the deck reshuffles on next Game.
     */
    public int getReshuffleCutoff() {
        return reshuffleCutOff;
    }

    /**
     * Shows the player and dealer hands as well as their current total value.
     * @return returns a String that has represents the state of the game.
     */
    public String toString() {
        return getGameState();
    }
}
