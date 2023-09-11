/*
file name: Deck.java
Author: Roman Schiffino
last modified:

I decided to give it some more values and stuff.
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Deck {

    //These are here to ensure global access to these variables.
    ArrayList<Card> cardDeck;
    //Storing cards in dictionary.
    HashMap<String, Card> deckListing;

    /**
     * Calls build() as a subroutine to build the deck itself.
     */
    public Deck() {
        cardDeck = new ArrayList<>();
        deckListing = new HashMap<>();
        build();
    }

    /**
     * Builds the underlying deck as a standard 52 card deck. 
     * Replaces any current deck stored. 
     */
    public void build() {
        cardDeck.clear();
        deckListing.clear();
        String[] suitList = {"Spades","Hearts","Clubs","Diamonds"};
        for (String suit : suitList) {
            for (int identity=1;identity<14;identity++) {
                String cardVarName = suit+identity;
                Card currentCard = new Card(identity,suit);
                deckListing.put(cardVarName, currentCard);
                cardDeck.add(deckListing.get(cardVarName));
            }
        }
    }

    /**
     * Returns the number of cards left in the deck.
     * @return the number of cards left in the deck
     */
    public int size() {
        return cardDeck.size();
    }

    /**
     * Returns and removes the first card of the deck.
     * @return the first card of the deck
     */
    public Card deal() {
        Card currentCard = cardDeck.get(0);
        cardDeck.remove(0);
        return currentCard;
    }

    /**
     * Shuffles the cards currently in the deck.
     */
    public void shuffle() {
        ArrayList<Card> tempList = new ArrayList<>(cardDeck);
        ArrayList<Card> outList = new ArrayList<>();
        for(int i=tempList.size();i>0;i--) {
            Random ran = new Random();
            int index = ran.nextInt(0,i);
            outList.add(tempList.get(index));
            tempList.remove(index);
        }
        cardDeck = outList;
    }

    /**
     * Returns a string representation of the deck.
     * @return a string representation of the deck
     */
    public String toString() {
        return Hand.getString(cardDeck);
    }

    /**
     * Returns a string representation of the deck (Fancy Version).
     * @return a string representation of the deck
     */
    public String toStringFancy() {
        return Hand.getStringFancy(cardDeck);
    }
    public static class MultiDeck extends Deck{

        //Initializes list for cards.
        ArrayList<Card> multiDeck = new ArrayList<>();

        public void addAll(ArrayList<Card> argList) {
            multiDeck.addAll(argList);
        }

        public Card get(int index) {
            return multiDeck.get(index);
        }

        public void add(Card card) {
            multiDeck.add(multiDeck.size(), card);
        }

        public void add(int index, Card card) {
            multiDeck.add(index, card);
        }

        public  void clear() {
            multiDeck.clear();
        }
    }
}
