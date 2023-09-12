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
    boolean highLowState;
    boolean interactiveState;
    boolean npcAIState;
    boolean advancedAIState;
    ArrayList<Boolean> decisionMatrix = new ArrayList<Boolean>();

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
        new Blackjack(reshuffleCutOff,1,1,false,false,true, false);
    }

    /**
     * Initializes advanced version of Blackjack with n players, m decks, and with optional highLow settings.
     * @param reshuffleCut the amount of cards in the deck before which the game will reshuffle.
     * @param players the amount of players.
     * @param decks the amount of decks to play with.
     * @param highLow optional high-low ace setting.
     */
    public Blackjack(int reshuffleCut, int players, int decks, boolean highLow, boolean interactive, boolean npcAI,
                     boolean advancedAI) {

        //Stores init conditions.
        reshuffleCutOff = reshuffleCut;
        playerCount = players;
        deckCount = decks;
        highLowState = highLow;
        interactiveState = interactive;
        npcAIState = npcAI;
        advancedAIState = advancedAI;

        //Create decks and multiDeck.
        for (int i=1; i < deckCount; i++) {
            String deckName = "deck" + i;
            Deck tempDeck = new Deck();
            deckMap.put(deckName, tempDeck);
            multiDeck.addAll(tempDeck.cardDeck);
        }
        multiDeck.shuffle();

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
            System.out.println("Your Hand: "+handMap.get("hand1").toStringFancy(highLowState));
            for (int i=2; i <= playerCount; i++) {
                String playerName = "player" + i + " Hand: ";
                String handName = "hand" + i;
                System.out.println(playerName + handMap.get(handName).publicToStringFancy(highLowState));
            }
            System.out.println("Dealer Hand: "+handMap.get("dealerHand").publicToStringFancy(highLowState));
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
                boolean done = false;
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

        System.out.println("Your Hand: "+handMap.get("hand1").toStringFancy(highLowState));
        for (int i=2; i <= playerCount; i++) {
            String playerName = "player" + i + " Hand: ";
            String handName = "hand" + i;
            System.out.println(playerName + handMap.get(handName).toStringFancy(highLowState));
        }
        System.out.println("Dealer Hand: " + handMap.get("dealerHand").toStringFancy(highLowState));
        TreeMap<Integer,ArrayList<Hand>> scoreMap = new TreeMap<>();
        for (Hand hand : dealList) {
            if (!scoreMap.containsKey(hand.getTotalValue(highLowState))) {
                ArrayList<Hand> equalPointArray = new ArrayList<>();
                scoreMap.put(hand.getTotalValue(highLowState),equalPointArray);
            }
            scoreMap.get(hand.getTotalValue(highLowState)).add(hand);
        }
        int topScore = scoreMap.lastKey();
        ArrayList<Hand> winners = new ArrayList<Hand>(scoreMap.get(topScore));
        if (winners.size() > 1) {
            String outVar = "Tie! ";
            for (Hand hand : winners) {
                dealList.indexOf(hand);
            }
            System.out.println(outVar);
        }
        else {

        }
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
        if (highLowState && (valList.contains(1))) {
            if (hand.getTotalValue(highLowState) < 17) {
                decision = true;
            }
        }
        else {
            if (hand.getTotalValue(highLowState) < 15) {
                decision = true;
            }
        }
        return decision;
    }

    public boolean dealerAI(Hand hand) {
        boolean decision = false;
        ArrayList<Integer> valList = new ArrayList<>();
        for (Card h : hand.handList) {
            valList.add(h.identity);
        }
        if (hand.getTotalValue(highLowState) < 17) {
            decision = true;
        }
        return decision;
    }

    public boolean advancedAI(Hand hand) {
        boolean decision;
        ArrayList<Card> countedCards = new ArrayList<>();
        ArrayList<Integer> countedCardVals = new ArrayList<>();
        ArrayList<Card> visibleList = new ArrayList<>(visible(hand));
        for (Card card : visibleList) {
            if (!card.counted) {
                countedCards.add(card);
                countedCardVals.add(card.identity);
            }
        }
        int runningCount = 0;
        for (int val : countedCardVals) {
            runningCount += switch ((int) val) {
                case 4,5,6 -> 2;
                case 2,3,7 -> 1;
                case 8,1 -> 0;
                case 9 -> -1;
                case 10,11,12,13 -> -2;
                default -> throw new IllegalStateException("Unexpected value: " + (int) val);
            };
        }
        double cardsRemaining = playerCount + multiDeck.size();
        double decksRemaining = cardsRemaining / 52;
        double trueCount = runningCount / decksRemaining;
        double betModifier = 1 - (trueCount * (0.04 )) - ((hand.getTotalValue(highLowState)-10) * (0.1));
        decision = betModifier > 0.5;
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

        String highLow;
        boolean highLowState = false;
        done = false;
        do {
            highLow = "";
            System.out.println("Do you want to play with High-Low Aces? (Y/N)");
            try {
                highLow = inputListener.nextLine();
            } catch (Exception IllegalArgumentException) {
                System.out.println("Please Type \"Y\" or \"N\"");
            }
            if (Objects.equals(highLow, "Y")) {
                highLowState = true;
                done = true;
            }
            else if (Objects.equals(highLow, "N")) {
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

        String npcAI;
        boolean AIState = false;
        done = false;
        do {
            System.out.println("Do you want to enable npcAI? (Y/N)");
            npcAI = inputListener.nextLine();
            if (Objects.equals(npcAI, "Y")) {
                AIState = true;
                done = true;
            }
            else if (Objects.equals(npcAI, "N")) {
                done = true;
            }
            else if (Objects.equals(npcAI, "")) {
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

        Blackjack gameInitialization = new Blackjack(5, playerCount, deckCount, highLowState,
                interactState, AIState, enableGoodAI);
    }



}
