/*
file name:      LandscapeTests.java
Authors:        Max Bender & Naser Al Madi
last modified:  9/18/2022

How to run:     java -ea LandscapeTests
*/

/*
Not gonna lie super low effort implementation here on my part. I wrote this entire thing in pretty much 8 hours and have
since been making slight modifications and tweaks here and there. I've been behind due to other work and some other
concerns. Won't happen again... hopefully.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author      Roman Schiffino <rjschi24@colby.edu>
 * @version     1.1
 * @since       1.1
 */
public class LandscapeTests {

    public static void landscapeTests() {

        // case 1: testing Landscape(int, int)
        {
            // set up
            Landscape l1 = new Landscape(2, 4);
            System.out.println(l1);
            Landscape l2 = new Landscape(10, 10);
            System.out.println(l2);

            // verify
            System.out.println(l1);
            System.out.println("\n");
            System.out.println(l2);

            // test
        }

        // Case 2: Testing reset()
        {
            // Set up
            LinkedList<Cell[][]> states = new LinkedList<>();
            Landscape l = new Landscape(15, 15, 100);

            // Reset the landscape
            System.out.println(l);
            states.add(l.stateList.getLast());
            l.advance();
            System.out.println(l);
            states.add(l.stateList.getLast());
            l.reset();
            System.out.println(l);
            states.add(l.stateList.getLast());
            l.advance();
            System.out.println(l);
            states.add(l.stateList.getLast());
            l.reset();
            System.out.println(l);
            states.add(l.stateList.getLast());

            // Verify
            ArrayList<Boolean> equalities = new ArrayList<>();

            System.out.println(Arrays.deepToString(states.toArray()));
            System.out.println(Arrays.deepToString(l.stateList.toArray()));
            equalities.add(Arrays.deepEquals(states.get(0), states.get(2)));
            equalities.add(Arrays.deepEquals(states.get(2), states.get(4)));
            System.out.println(equalities);

            if ((equalities.contains(false))) throw new AssertionError("Error in Landscape::reset()");
        }

        // Add more test cases for other methods here

        // Case 3: Testing getRows()
        {
            // Set up
            Landscape l = new Landscape(3, 4);

            // Test
            int rows = l.getRows();

            // Verify
            assert rows == 3 : "Error in Landscape::getRows()";
        }

        // case 4: testing getCols()
        {
            // Set up
            Landscape l = new Landscape(3, 4);

            // Test
            int cols = l.getCols();

            // Verify
            assert cols == 4 : "Error in Landscape::getCols()";
        }

        // case 5: testing getCell(int, int)
        {
            // set up


            // verify


            // test

        }

        // case 6: testing getNeighbors()
        {
            // set up


            // verify


            // test

        }

        // case 7: testing advance()
        {
            // set up
            Landscape l1 = new Landscape(10, 10,0);
            System.out.println(l1);
            Landscape l2 = new Landscape(10, 10,50);
            System.out.println(l2);
            String l3 = l2.toString();
            System.out.println(l3);

            // verify
            System.out.println(l1);
            System.out.println("\n");
            System.out.println(l2);
            l2.advance();
            System.out.println(l3+"\n"+l2);

            // test

        }

    }


    public static void main(String[] args) {

        landscapeTests();
    }
}
