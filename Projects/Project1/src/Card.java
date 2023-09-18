/*
file name: Card.java
Author: Roman Schiffino
last modified:  9/10/2023

I decided to give it some more values and stuff.
*/

import java.util.ArrayList;

/**
 * @author      Roman Schiffino <rjschi24@colby.edu>
 * @version     1.1
 * @since       1.1
 */
public class Card {

    //Identity of card
    int identity;
    //The suit of the card.
    String suit;
    //The value of the card.
    int value;
    //Flag for card counting AI.
    boolean counted = false;
    //Container for card identity.
    ArrayList<Object> cardIdentifier = new ArrayList<>();

    /**
     * Constructs a card with the specified values.
     * <p>
     * Overloaded method to patch to allow test file to work.
     *
     * @param ident the identifier for the card.
     */
    public Card(int ident) {
        identity = ident;
        cardIdentifier.add(ident);
        value = switch ((int) cardIdentifier.get(0)) {
            case 1 -> 11;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            case 5 -> 5;
            case 6 -> 6;
            case 7 -> 7;
            case 8 -> 8;
            case 9 -> 9;
            case 10,11,12,13 -> 10;
            default -> throw new IllegalStateException("Unexpected value: " + (int) cardIdentifier.get(0));
        };
    }

    /**
     *The same card but with the added suit information.
     * <p>
     * This is also the overloading section. with my added stuff. This is necessary because cardTests
     * won't work otherwise.
     *
     * @param ident the unique identifier for the card per suit.
     * @param suit the suit of the card.
     */
    public Card(int ident, String suit) {
        identity = ident;
        cardIdentifier.add(ident);
        cardIdentifier.add(suit);
        value = switch ((int) cardIdentifier.get(0)) {
            case 1 -> 11;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            case 5 -> 5;
            case 6 -> 6;
            case 7 -> 7;
            case 8 -> 8;
            case 9 -> 9;
            case 10,11,12,13 -> 10;
            default -> throw new IllegalStateException("Unexpected value: " + (int) cardIdentifier.get(0));
        };
    }

    /**
     * Returns the value of the card.
     *
     * @return the value of the card
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the suit of the card.
     *
     * @return the suit of the card
     */
    public String getSuit() {
        return ((String) cardIdentifier.get(1));
    }

    /**
     * Returns the unique identifier of the card.
     *
     * @return the unique identifier of the card
     */
    public ArrayList<Object> getCard() {
        // TBD
        return cardIdentifier;
    }

    /**
     * Returns a string representation of this card.
     *
     * @return a string representation of this card
     */
    public String toString() {
        // String representation of the card.
        String stringRep = switch ((int) cardIdentifier.get(0)) {
            case 1 -> "1";
            case 2 -> "2";
            case 3 -> "3";
            case 4 -> "4";
            case 5 -> "5";
            case 6 -> "6";
            case 7 -> "7";
            case 8 -> "8";
            case 9 -> "9";
            case 10 -> "10";
            case 11 -> "Jack";
            case 12 -> "Queen";
            case 13 -> "King";
            default -> throw new IllegalStateException("Unexpected value: " + (String) cardIdentifier.get(0));
        };
        return getString(stringRep);
    }

    /**
     * Returns a fancy string representation of this card.
     *
     * @return a fancy string representation of this card
     */
    public String toStringFancy() {
        // String representation of the card.
        String stringRep = switch ((int) cardIdentifier.get(0)) {
            case 1 -> "Ace";
            case 2 -> "Two";
            case 3 -> "Three";
            case 4 -> "Four";
            case 5 -> "Five";
            case 6 -> "Six";
            case 7 -> "Seven";
            case 8 -> "Eight";
            case 9 -> "Nine";
            case 10 -> "Ten";
            case 11 -> "Jack";
            case 12 -> "Queen";
            case 13 -> "King";
            default -> throw new IllegalStateException("Unexpected value: " + (String) cardIdentifier.get(0));
        };
        return getString(stringRep);
    }

    /**
     * Constructs card string name from identifier.
     *
     * @return a string form of card identifier.
     */
    public String getString(String stringRep) {
        if (cardIdentifier.size() == 2) {
            switch ((String) cardIdentifier.get(1)) {
                case "Spades" -> stringRep += " of Spades";
                case "Hearts" -> stringRep += " of Hearts";
                case "Clubs" -> stringRep += " of Clubs";
                case "Diamonds" -> stringRep += " of Diamonds";
            }
        }
        return stringRep;
    }
}
