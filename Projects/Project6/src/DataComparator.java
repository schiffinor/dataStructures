import java.util.ArrayList;
import java.util.HashMap;

public class DataComparator {
    public static void main( String[] args ) {

        HashMap<String,WordCounter> dataBank = new HashMap<>();

        for (int i = 2008; i <= 2015; i++) {
            WordCounter newHash = new WordCounter("HashMap");
            newHash.setName(i + "Hash");
            dataBank.put(newHash.getName(), newHash);
            WordCounter newBST = new WordCounter("BSTMap");
            newBST.setName(i + "BST");
            dataBank.put(newBST.getName(), newBST);
            String filename = "Reddit_Comments_Files-20220403/reddit_comments_"+i+".txt" ;
            ArrayList<String> words1 = newHash.readWords(filename);
            newHash.buildMap( words1 );
            words1 = null;
            ArrayList<String> words2 = newBST.readWords(filename);
            newBST.buildMap( words2 );
            words2 = null;
        }

        //Write word counts to an output file
        dataBank.values().forEach(counter -> counter.writeWordCount(counter.getName()+".txt" ));

    }
}
