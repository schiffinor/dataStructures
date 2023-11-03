/*

 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class Job {

    private final double arrivalTime;
    private final double processingTime;

    public Job(double arrivalTime,double processingTime) {
        this.arrivalTime = arrivalTime;
        this.processingTime = processingTime;
    }


    public static Queue<Job> readJobFile(String fileName) {

        // create queue
        Queue<Job> jobSequence = new LinkedList<Job>();

        // Flag to continue processing or not.
        boolean continueOn = true;

        BufferedReader fileGetter = null;
        try {
            fileGetter = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            continueOn = false;
            System.out.println("File not found.");
            System.out.println("Board.read():: unable to open file " + fileName);
        }
        if (continueOn) {
            String currLine;
            try {
                do {
                    // Read a line from the file.
                    currLine = fileGetter.readLine();

                    // Exit the loop if the end of the file is reached or the board is fully read.
                    if (currLine == null) break;

                    // assign to an array of type String the result of calling split on the line with the argument ","

                    // let's see what this array holds:

                    //Create a new job by parsing the arrival time and processing time out of the String array

                    //Offer it to jobSequence

                    if () {

                    } else if () {
                        System.out.println("File not of correct format.");
                        throw new IOException("File not of correct format.");
                    }
                } while (true);


                // Close the file reader.
                fileGetter.close();

                return jobSequence;
            } catch (IOException e) {
                System.out.println("Read Error.");
                System.out.println("JobReader.read():: error reading file " + fileName);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }

        }
    }
}
