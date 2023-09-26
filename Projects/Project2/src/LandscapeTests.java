/*
file name:      LandscapeTests.java
Authors:        Max Bender & Naser Al Madi
last modified:  9/18/2022

How to run:     java -ea LandscapeTests
*/


import java.util.ArrayList;

public class LandscapeTests {

    public static void landscapeTests() {

        // case 1: testing Landscape(int, int)
        {
            // set up
            Landscape l1 = new Landscape(2, 4);
            Landscape l2 = new Landscape(10, 10);

            // verify
            //System.out.println(l1);
            //System.out.println("\n");
            //System.out.println(l2);

            // test
            assert l1 != null : "Error in Landscape::Landscape(int, int)";
            assert l2 != null : "Error in Landscape::Landscape(int, int)";
        }

        // case 2: testing reset()
        {
            // set up


            // verify


            // test
            
        }

        // case 3: testing getRows()
        {
            // set up


            // verify


            // test

        }

        // case 4: testing getCols()
        {
            // set up


            // verify


            // test

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
            assert l1 != null : "Error in Landscape::Landscape(int, int)";
            assert l2 != null : "Error in Landscape::Landscape(int, int)";

        }

    }


    public static void main(String[] args) {

        landscapeTests();
    }
}
