/*
 Author: Roman Schiffino

 IDK I guess we're doing Lab 1??? I assume we're using arrays.
 */

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Shuffle {
    public static void main(String[] args) {

        ArrayList<Integer> arr0 = new ArrayList<>();

        for(int i=0;i<10;i++) {
            Random ran = new Random();
            int randVal = ran.nextInt();
            arr0.add(randVal);
        }

        int count = 0;
        for(int x : arr0) {
            count++;
            System.out.println(count+": "+x);
        }
        ArrayList<Integer> arr1 = new ArrayList<>();
        for(int x : arr0) {
            arr1.add(x);
        }

        ArrayList<Integer> arr2 = arr0;

        System.out.println(arr0);
        System.out.println(arr1);
        System.out.println(arr2);

        System.out.printf("arr0 == arr1: %s\narr1 == arr2: %s\narr2 == arr0: %s%n", arr0 == arr1, arr1 == arr2,
                arr2 == arr0);
        System.out.printf("arr0.equals(arr1): %s\narr1.equals(arr2): %s\narr2.equals(arr0): %s", arr0.equals(arr1),
                arr1.equals(arr2), arr2.equals(arr0));

        for(int i=10;i>0;i--) {
            Random ran = new Random();
            int index = ran.nextInt(0,i);
            int val = arr0.get(index);
            arr0.remove(index);
            System.out.printf("\nRemoving: %s\n...\nNew List: %s\n", val,arr0);
        }

        System.out.println("Now introducing the shuffling function.");
        boolean permuterFlag = false;
        while(!permuterFlag) {
            System.out.println("Press Enter to Shuffle arr1: ");
            Scanner userInput = new Scanner(System.in);
            String input = userInput.nextLine();

            if(input.isEmpty()) {
                ArrayList<Integer> permutedArray = permuter(arr1);
                System.out.printf("\nOriginal Array: %s\nPermuted Array: %s\n",arr1,permutedArray);
                System.out.println("Re-permute?\nIf so press Enter, if not type NO and press Enter.");
                input = userInput.nextLine();
                if(input.isEmpty()) {
                    System.out.println("OK, Rerunning code.");
                }
                else {
                    System.out.println("OK, Bye.");
                    permuterFlag = true;
                }

            }
            else {
                System.out.println("Just press Enter, nothing else.");
            }
        }

    }

    public static ArrayList<Integer> permuter(ArrayList<Integer> argList) {
        ArrayList<Integer> tempList = new ArrayList<>(argList);
        ArrayList<Integer> outList = new ArrayList<>();
        for(int i=10;i>0;i--) {
            Random ran = new Random();
            int index = ran.nextInt(0,i);
            outList.add(tempList.get(index));
            tempList.remove(index);
        }
    return outList;
    }

}