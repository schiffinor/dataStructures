/*
file name: Blackjack.java
Author: Roman Schiffino
last modified:

Different representations than standard but at the end of the day just slight mod of standard language.
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
public class Blackjack {

    //Creates a multiDeck for larger games. This is a hashMap to keep everything organized.
    HashMap<String, Deck> deckMap = new HashMap<>();
    //Multi-deck that's shuffle-able.
    ArrayList<Card> multiDeck = new ArrayList<>();
    //Creates a handMap for tracking player hands.
    HashMap<String, Hand> handMap = new HashMap<>();
    //Creates a handMap for tracking player hands.
    ArrayList<Hand> dealList = new ArrayList<>();

    /**
     * Initializes most basic version of Blackjack with all default parameters.
     */
    public Blackjack() {

        //Basic version 3 players.
        new Blackjack(3);
    }

    /**
     * Initializes basic version of Blackjack with n players.
     * @param players the amount of players.
     */
    public Blackjack(int players) {

        //Basic version n players, 1 deck, high ace.
        new Blackjack(players,1,false);
    }

    /**
     * Initializes advanced version of Blackjack with n players, m decks, and with optional highLow settings.
     * @param players the amount of players.
     * @param decks the amount of decks to play with.
     * @param highLow optional high-low ace setting.
     */
    public Blackjack(int players, int decks, boolean highLow) {

        //Create decks and multiDeck.
        for (int i=1; i < decks; i++) {
            String deckName = "deck" + i;
            Deck tempDeck = new Deck();
            deckMap.put(deckName, tempDeck);
            multiDeck.addAll(tempDeck.cardDeck);
        }

        //Create hands and handMap.
        Hand dealerHand = new Hand();
        handMap.put("dealerHand",dealerHand);
        for (int i=1; i < players; i++) {
            String handName = "hand" + i;
            Hand tempHand = new Hand();
            handMap.put(handName, tempHand);
            dealList.add(tempHand);
        }
        dealList.add(dealerHand);


    }

}
