/*
Never finished implementing this machine learning system. It should work, I wasn't too far from finishing the
implementation I just needed to put the random action selector and implement the reward function.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

/**
 * @author      Roman Schiffino <rjschi24@colby.edu>
 * @version     1.1
 * @since       1.1
 */
public class MachineLearningAI {
    Hand hand;
    ArrayList<Double> frequencyList;
    boolean highLowState;
    boolean containsAce;
    int handValue;
    FileWriter fileWriter;
    HashMap<String,HashMap<String,ArrayList<Double>>> dataListStores;
    HashMap<String,HashMap<String,String>> dataStringStores;

    /**
     * Some testing for this unimplemented class.
     * <p>
     * Basically creates an AI entity and a Blackjack entity
     * and brute forces some tests.
     */
    public static void main(String[] args) throws IOException {
        MachineLearningAI init = new MachineLearningAI(true);
        Blackjack game = new Blackjack(12,1,1,true,false,true,true,false,true);

        for (int i = 0; i < 10; i++) {
            game.handMap.get("hand1").add(game.multiDeck.deal());
        }
        init.getFrequencyList(game.handMap.get("hand1"));
    }


    /**
     * Constructor for AI Object.
     * <p>
     * Instantiates most variables and extracts data from text
     * files, creates files if not present.
     * <p>
     * All data is placed into nested hash maps. Which can
     * then be accessed via unique identifiers.
     *
     * @param  highLowStateParam defines whether game is using high
     *                           or high-low ace.
     */
    public MachineLearningAI(boolean highLowStateParam) throws IOException {
        highLowState = highLowStateParam;
        BufferedReader fileGetter = null;
        dataListStores = new HashMap<>();
        dataStringStores = new HashMap<>();
        for (int j = 0; j < 3; j++) {
            String fileName = switch (j) {
                case 0 -> "inputDataHighAce.txt";
                case 1 -> "inputDataHighLowAceNoAce.txt";
                case 2 -> "inputDataHighLowAceYesAce.txt";
                default -> throw new IllegalStateException("Unexpected value: " + j);
            };
            String mapName = switch (j) {
                case 0 -> "highAceMap";
                case 1 -> "highLowAceNoAceMap";
                case 2 -> "highLowAceYesAceMap";
                default -> throw new IllegalStateException("Unexpected value: " + j);
            };
            try {
                fileGetter = new BufferedReader(new FileReader(fileName));
            } catch (Exception FileNotFoundException) {
                fileWriter = new FileWriter(fileName);
                for (int i = 0; i < 10; i++) {
                    String outString = "[0 0 0 0 0 0 0 0 0 0 0 0 0]\n";
                    fileWriter.write(outString);
                }
                fileWriter.close();
                fileGetter = new BufferedReader(new FileReader(fileName));
            }
            HashMap<String,String> linearOperatorStringMap = new HashMap<>();
            HashMap<String,ArrayList<Double>> linearOperatorListMap = new HashMap<>();
            for (int i = 11; i < 21; i++) {
                String linOppName = "linOpp"+i;
                String linOppStr = fileGetter.readLine();
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(linOppStr);
                ArrayList<Double> linOppList = new ArrayList<>();
                while (matcher.find()) {
                    linOppList.add(Double.valueOf(matcher.group()));
                }
                linearOperatorStringMap.put(linOppName,linOppStr);
                linearOperatorListMap.put(linOppName,linOppList);
            }
            dataStringStores.put(mapName+"String",linearOperatorStringMap);
            dataListStores.put(mapName+"List",linearOperatorListMap);
        }
        System.out.println(dataListStores);

    }

    /**
     * Determines and packages HashMap identifiers for linearOperator querying.
     * <p>
     * Booleans are parsed as integers and then packaged in array with handValue as args for
     * queryLinOps method.
     *
     * @param handValue the value of the current hand.
     * @param containsAce whether the current hand contains an ace.
     * @return an array converting game states into integers to be parsed by the query method.
     */
    public int[] stateDeterminer(int handValue, boolean containsAce) {
        int highLowIndex;
        int containsAceIndex;
        if (highLowState) {
            highLowIndex = 0;
        }
        else {
            highLowIndex = 1;
        }
        if (containsAce) {
            containsAceIndex = 0;
        }
        else {
            containsAceIndex = 1;
        }
        return new int[]{highLowIndex, containsAceIndex, handValue};
    }

    /**
     * Takes array argument from stateDeterminer and returns corresponding Linear operator array.
     * <p>
     * Goes through arguments and selects corresponding hashmap entries.
     *
     * @param args array argument from stateDeterminer {highLowState,containsAceState,handVal}
     * @return Linear operator array corresponding to inputs.
     */
    public ArrayList<Double> queryLinOps(int[] args) {
        String identifier;
        if (args[0] == 0) {
            if (args[1] == 0) {
                identifier = "highLowAceYesAceMapList";
            }
            else {
                identifier = "highLowAceNoAceMapList";
            }
        }
        else {
            identifier = "highAceMapList";
        }
        String stringIdentifier = "linOpp"+args[2];
        return dataListStores.get(identifier).get(stringIdentifier);
    }

    /**
     * Fetches frequency list of counted card for given hand.
     * <p>
     * Calls method from BlackJack class for determining cards visible to hand. Once cards
     * are counted, list is parsed to determine frequency of each card which is then passed into
     * ArrayList.
     *
     * @param hand hand to determine visible card list from.
     * @return ArrayList of counted card frequencies.
     */
    public ArrayList<Integer> getFrequencyList(Hand hand) {
        ArrayList<Card> visibleList = new ArrayList<>(Blackjack.visible(hand));
        for (Card card : visibleList) {
            if (!card.counted) {
                hand.countedCardVals.add(card.identity);
            }
        }
        ArrayList<Integer> freqTable = new ArrayList<>();
        Integer[] intArray = {1,2,3,4,5,6,7,8,9,10,11,12,13};
        for(Integer identity : intArray) {
            freqTable.add(Collections.frequency(hand.countedCardVals, identity));
        }
        System.out.println(freqTable);
        return freqTable;
    }

    /**
     * Takes frequency list and deckCount then adjusts frequencies to correspond to deckCount.
     * <p>
     * Divides frequencies by amount of decks to make more similar frequencies across multideck games.
     * Converts integers to doubles then divides by deckCount.
     *
     * @param inList frequency list to divide and adjust.
     * @param deckCount number of decks to divide by.
     * @return outList equal to frequencies divided by deckCount.
     */
    public ArrayList<Double> frequencyAdjuster(ArrayList<Integer> inList, int deckCount) {
        ArrayList<Double> outList = new ArrayList<>();
        for(Integer freq : inList) {
            outList.add((Double) ((double) freq)/deckCount);
        }
        System.out.println(outList);
        return outList;
    }

    /**
     * Gets frequency list, deck count, and hand before passing to getChoice function.
     * <p>
     * Gets frequency list for hand, then passes to frequency adjuster along with deckCount,
     * then passes adjusted frequency list to getChoice.
     *
     * @param handParam hand to calculate frequency from and to determine choice for.
     * @param deckCount amount of decks to adjust frequency by.
     * @return boolean of whether to hit or not depending on machine learning algorithm.
     */
    public boolean mlChoice(Hand handParam, int deckCount) {
        hand = handParam;
        return getChoice(hand, frequencyAdjuster(getFrequencyList(hand),deckCount));
    }

    /**
     * Handles hand values and frequency list to determine the best choice for AI dependent on linear operators.
     * <p>
     * Determines current hand value first, if 10 or less true, if 21 or greater false, in case hand value
     * between 10 and 21, further state data is determined and stateDeterminer called. Corresponding Linear
     * Operator Bra is called and frequencyList Ket is multiplied {@literal <linOpp| * |frequencyList>} to
     * obtain the betModifier. If the resulting betModifier is negative will not hit, otherwise will hit.
     *
     * @param handParam hand to determine best play for.
     * @param frequencyListParam frequency ket to use in algorithm.
     * @return decision based on betModifier.
     */
    public boolean getChoice(Hand handParam, ArrayList<Double> frequencyListParam) {
        boolean decision = true;
        if ((hand.getTotalValue(highLowState) >= 10) && (hand.getTotalValue(highLowState) <= 21)) {
            hand = handParam;
            frequencyList = frequencyListParam;
            if (highLowState) {
                for (Card card : hand.handList) {
                    if (card.identity == 1) {
                        containsAce = true;
                        break;
                    }
                }
            }
            else {
                containsAce = false;
            }
            handValue = hand.getTotalValue(highLowState);

            ArrayList<Double> linearOperator= queryLinOps(stateDeterminer(handValue,containsAce));
            int i = 0;
            double betModifier = 0;
            for (double freq : frequencyList) {
                betModifier += freq * linearOperator.get(i);
                i++;
            }
            System.out.println(betModifier);
            decision = !(betModifier <= 0);
        }
        else if (hand.getTotalValue(highLowState) >= 21) {
            decision = false;
        }
        return decision;
    }
}
