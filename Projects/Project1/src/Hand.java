/*
file name: Hand.java
Author: Roman Schiffino
last modified:

Different representations than standard but at the end of the day just slight mod of standard language.
*/
import java.util.ArrayList;

public class Hand {

    //Again this is here for global access reasons.
    ArrayList<Card> handList;
    ArrayList<Card> publicList;
    ArrayList<Card> privateList;

    /**
     * Create empty hand via ArrayList.
     */
    public Hand(){
        handList = new ArrayList<>();
        publicList = new ArrayList<>();
        privateList = new ArrayList<>();
    }

    /**
     * Removes any cards currently in the hand. 
     */
    public void reset(){
        handList.clear();
    }

    /**
     * Adds the specified card to the hand.
     * @param card the card to be added to the hand
     */
    public void add(Card card){
        //Adds card to public hand and base handList.
        add(card, false);
    }

    /**
     * Adds the specified card to the hand either public or private and base handList.
     * @param card the card to be added to the hand
     * @param privateState determines whether the card is added to private or public hand.
     */
    public void add(Card card, boolean privateState){
        handList.add(card);
        if (privateState) {
            privateList.add(card);
        }
        else {
            publicList.add(card);
        }

    }

    /**
     * Fetches public hand from specified.
     * @param hand the hand to fetch from.
     */
    public ArrayList<Card> fetch(Hand hand) {
        return fetch(hand,false);
    }

    /**
     * Fetches public hand from specified.
     * @param hand the hand to fetch from.
     * @param privateState whether to fetch full hand or just public. Not standard use.
     */
    public ArrayList<Card> fetch(Hand hand, boolean privateState) {
        ArrayList<Card> outList;
        if (privateState) {
            outList = new ArrayList<>(handList);
        }
        else {
            outList = new ArrayList<>(publicList);

        }
        return outList;
    }

    /**
     * Returns the number of cards in the hand.
     * @return the number of cards in the hand
     */
    public int size(){
        return handList.size();
    }

    /**
     * Returns the card in the hand specified by the given index. 
     * @param index the index of the card in the hand.
     * @return the card in the hand at the specified index.
     */
    public Card getCard(int index){
        return handList.get(index);
    }

    /**
     * Returns the summed value over all cards in the hand.
     * @return the summed value over all cards in the hand
     */
    public int getTotalValue(){
        return getTotalValue(false);
    }

    /**
     * Returns the summed value over all cards in the hand for high-low.
     * This optimizes the rate of low and high aces to maximize value without going bust.
     * @return the summed value over all cards in the hand for high-low.
     */
    public int getTotalValue(boolean highLow) {
        int sumValue = 0;
        if (highLow) {
            ArrayList<Card> aceList = new ArrayList<>();
            ArrayList<Card> noAceList = new ArrayList<>();
            for (Card card : handList) {
                if (card.identity == 1)
                    aceList.add(card);
                else {
                    noAceList.add(card);
                }
            }
            for (Card card : noAceList) {
                sumValue += card.getValue();
            }
            if (!aceList.isEmpty()) {
                if (sumValue > 10) {
                    for (Card card : aceList) {
                        sumValue += (card.getValue() - 10);
                    }
                } else {
                    for (Card card : aceList) {
                        sumValue += (card.getValue() - 10);
                    }
                    sumValue += 10;
                }
            }
        }
        else {
            for (Card card : handList) {
                sumValue += card.getValue();
            }
        }
        return sumValue;
    }

    /**
     * static so it's accessible from deck. Creates our string thingy.
     * @param argList Input list to formulate string.
     * @return the string version.
     */
    static String getString(ArrayList<Card> argList) {
        StringBuilder outString = new StringBuilder("[");
        for (Card card : argList) {
            outString.append(card.toString()).append(", ");
        }
        if (!argList.isEmpty()) {
            outString.replace(outString.length() - 2,outString.length(),"");
        }
        outString.append("]");
        return outString.toString();
    }

    /**
     * static so it's accessible from deck. Creates our string thingy (Fancy Version).
     * @param argList Input list to formulate string.
     * @return the string version.
     */
    static String getStringFancy(ArrayList<Card> argList) {
        StringBuilder outString = new StringBuilder("[");
        for (Card card : argList) {
            outString.append(card.toStringFancy()).append(", ");
        }
        if (!argList.isEmpty()) {
            outString.replace(outString.length() - 2,outString.length(),"");
        }
        outString.append("]");
        return outString.toString();
    }

    /**
     * Returns a string representation of the hand.
     * @return a string representation of the hand
     */
    public String toString(){
        return getString(handList) + " : " + getTotalValue();
    }

    //HighLow version of above.
    public String toString(boolean highLow){
        return getString(handList) + " : " + getTotalValue(highLow);
    }

    /**
     * Returns a fancy string representation of the hand.
     * @return a fancy string representation of the hand
     */
    public String toStringFancy(){
        return getStringFancy(handList) + " : " + getTotalValue();
    }

    //HighLow version of above.
    public String toStringFancy(boolean highLow){
        return getStringFancy(handList) + " : " + getTotalValue(highLow);
    }

    /**
     * Returns a fancy string representation of the public hand.
     * @return a fancy string representation of the public hand
     */
    public String publicToStringFancy(){
        return getStringFancy(publicList);
    }

    //HighLow version of above.
    public String publicToStringFancy(boolean highLow){
        return getStringFancy(publicList);
    }
}
