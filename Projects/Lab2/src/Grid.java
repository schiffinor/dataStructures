import java.util.ArrayList;
import java.util.Random;

/**
 *
 */
public class Grid {

    public String[][] grid;
    public int rowCount;
    public int columnCount;
    public static void main(String[] args) {
        //Insert Command line Validation stuff
        Grid myGrid = new Grid(3,5);
        System.out.println(myGrid);
        int[][] arr1 = new int[2][2];
        int[][] arr2 = new int[2][2];
        int[][] arr3;
        int[][] arr4 = new int[2][2];
        int[][] arr5 = new int[3][3];
        int[][] arr6 = new int[3][2];
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 2; j++){
                arr1[i][j] = i+j;
                arr2[i][j] = i+j;
                arr4[i][j] = i-j;
            }
        }
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                arr5[i][j] = i+j;
            }
        }
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 2; j++){
                arr6[i][j] = i+j;
            }
        }
        arr3 = arr1;
        System.out.println(arr1 == arr2);
        System.out.println(arr1 == arr3);
        System.out.println(gridEquals(arr1,arr2));
        System.out.println(gridEquals(arr1, arr3));
        System.out.println(gridEquals(arr1, arr4));
        System.out.println(gridEquals(arr1, arr5));
        System.out.println(toStringGetter(arr6));
        System.out.println(toStringGetter(rotate(arr6)));
    }

    public Grid(int rowCountParam, int columnCountParam) {
        Random r = new Random();
        rowCount = rowCountParam;
        columnCount = columnCountParam;
        grid = new String[rowCount][columnCount];
        for (int row=0;row<rowCount;row++) {
            for (int column=0;column<columnCount;column++) {
                grid[row][column] = String.valueOf(((char) r.nextInt(48,58)));
            }
        }
    }

    public static String toStringGetter(int[][] arr) {
        StringBuilder outString = new StringBuilder();

        for (int[] row : arr) {
            outString.append("[ ");
            for (int column: row) {
                outString.append(column).append(" ");
            }
            outString.append("]\n");
        }
        return outString.toString();
    }
    @Override
    public String toString() {
        StringBuilder outString = new StringBuilder();

        for (String[] row : grid) {
            outString.append("[ ");
            for (String column: row) {
                outString.append(column).append(" ");
                }
            outString.append("]\n");
            }
        return outString.toString();
    }

    public static boolean gridEquals(int[][] arr1, int[][] arr2) {
        boolean equivalent = false;
        ArrayList<Boolean> trueList = new ArrayList<>();
        try {
            trueList.add(arr1.length == arr2.length);
            int currentRow = 0;
            for (int[] row : arr1) {
                int currentColumn = 0;
                trueList.add(row.length==arr2[currentRow].length);
                for (int column : row) {
                    trueList.add(column == arr2[currentRow][currentColumn]);
                    currentColumn++;
                }
                currentRow++;
            }
            if (!trueList.contains(false)) {
                equivalent = true;
            }
        } catch (ArrayIndexOutOfBoundsException ignore) {

        }
        return equivalent;
    }

    public static int[][] rotate(int[][] arr) {
        int oriRows = arr.length;
        int oriColumns = arr[0].length;
        int[][] outArr = new int[oriColumns][oriRows];
        for (int row = oriColumns-1; row >= 0; row--) {
            for (int column = oriRows-1; column >= 0; column--) {
                outArr[row][oriRows-1-column] = arr[column][row];
            }
        }
        return outArr;
    }
}
