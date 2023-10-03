/*
Hi this is the main backbone for things, I'll comment this up a little better later, but I made some effort here to
make this run relatively efficiently. Hope it worked well enough.
 */
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;


/**
 * @author      Roman Schiffino <rjschi24@colby.edu>
 * @version     1.1
 * @since       1.1
 */
public class Landscape {

    /**
     * The underlying grid of Cells for Conway's Game
     */
    public Cell[][] landscape;
    private final Random rand;
    // Keeps a history of game states
    public final LinkedList<Cell[][]> stateList;
    //Stores previous state, not yet implemented.
    public final HashMap<String,LinkedList<Cell[][]>> previousGame;
    //Stores unique row column data per cell.
    private HashMap<Cell,Integer[]> cellHashMap;
    public static boolean paused;
    public int rowCount;
    public int columnCount;

    /**
     * The original probability each individual Cell is alive
     */
    private final double initialChance;

    /**
     * Constructs a Landscape of the specified number of rows and columns.
     * <p>
     * All Cells are initially dead.
     * 
     * @param rows    the number of rows in the Landscape
     * @param columns the number of columns in the Landscape
     */
    public Landscape(int rows, int columns) {
        this(rows, columns, 0);
    }

    /**
     * Constructs a Landscape of the specified number of rows and columns.
     * <p>
     * Each Cell is initially alive with probability specified by chance.
     * 
     * @param rows    the number of rows in the Landscape
     * @param columns the number of columns in the Landscape
     * @param chance  the probability each individual Cell is initially alive
     */
    public Landscape(int rows, int columns, double chance) {
        rowCount = rows;
        columnCount = columns;
        initialChance = chance;
        rand = new Random();
        stateList = new LinkedList<>();
        previousGame = new HashMap<>();
        paused = true;
        reset();
    }

    /**
     * Recreates the Landscape according to the specifications given in its initial construction.
     */
    public void reset() {
        landscape = new Cell[rowCount][columnCount];
        cellHashMap = new HashMap<>();
        int curRow = 0;
        for (Cell[] row : landscape) {
            int curCol = 0;
            for (Cell column: row) {
                double choiceDouble = rand.nextDouble(0,100);
                landscape[curRow][curCol] = new Cell(choiceDouble < initialChance);
                Integer[] identifier = new Integer[2];
                identifier[0] = curRow;
                identifier[1] = curCol;
                cellHashMap.put(landscape[curRow][curCol], identifier.clone());
                curCol++;
            }
            curRow++;
        }
        stateList.clear();
        stateList.addLast(landscape);
        previousGame.put("previousGame",stateList);
    }

    /**
     * Returns the number of rows in the Landscape.
     * 
     * @return the number of rows in the Landscape
     */
    public int getRows() {
        return rowCount;
    }

    /**
     * Returns the number of columns in the Landscape.
     * 
     * @return the number of columns in the Landscape
     */
    public int getCols() {
        return columnCount;
    }

    /**
     * Sets row count.
     */
    public void setRows(int rows) {
        rowCount = rows;
    }

    /**
     * Sets column count.
     */
    public void setCols(int columns) {
        columnCount = columns;
    }

    /**
     * Returns the Cell specified the given row and column.
     *
     * @param input Array of cell identifier.
     * @return the Cell specified the given row and column
     */
    public Cell getCell(Integer[] input) {
        return getCell(input[0],input[1]);
    }

    /**
     * Returns the Cell specified the given row and column.
     * 
     * @param row the row of the desired Cell
     * @param col the column of the desired Cell
     * @return the Cell specified the given row and column
     */
    public Cell getCell(int row, int col) {
        return landscape[row][col];
    }

    /**
     * Returns a String representation of the Landscape.
     *
     * @return String representation of the Landscape.
     */
    public String toString() {
        return arrayString(landscape);
    }

    /**
     * Generates a formatted string representation of a two-dimensional array.
     * This method creates a string representation of the input array, where each
     * element is enclosed in square brackets and separated by spaces. Rows are
     * separated by newline characters.
     *
     * @param arr the two-dimensional array to convert to a string
     * @return a formatted string representation of the input array
     */
    public static String arrayString(Object[][] arr) {
        StringBuilder outString = new StringBuilder();
        for (Object[] row : arr) {
            outString.append("[ ");
            for (Object column: row) {
                try {
                    outString.append(column.toString()).append(" ");
                } catch (NullPointerException NullPointerException) {
                    outString.append("null").append(" ");
                }
            }
            outString.append("]\n");
        }
        return outString.toString();
    }

    /**
     * Retrieves the neighboring cells of a specified location.
     *
     * @param integers an array containing row and column indices
     * @return an ArrayList of neighboring cells
     */
    public ArrayList<Cell> getNeighbors(Integer[] integers) {
        return getNeighbors(integers[0],integers[1]);
    }

    /**
     * Returns an ArrayList of the neighboring Cells to the specified location.
     * 
     * @param row the row of the specified Cell
     * @param col the column of the specified Cell
     * @return an ArrayList of the neighboring Cells to the specified location
     */
    public ArrayList<Cell> getNeighbors(int row, int col) {
        //Clones array.
        Cell[][] landscapeSnapshot = landscape.clone();
        ArrayList<Cell> neighbors = new ArrayList<>();
        //Constructor for boolean-array of directions to check for neighbors.
        boolean[] checkDirections = new boolean[4];
        //Up.
        checkDirections[0] = (row > 0);
        //Right.
        checkDirections[1] = (col > 0);
        //Down.
        checkDirections[2] = (row < getRows()-1);
        //Left.
        checkDirections[3] = (col < getCols()-1);
        //Check whether ref-cell in the scope of array.
        try {
            Cell refCell = landscapeSnapshot[row][col];
        } catch (Exception ArrayIndexOutOfBoundsException) {
            System.out.println("Cell not within bounds of game. Neighbors DNE.");
        }
        //Checks allotted directions.
        for (int i = 0; i < 4; i++) {
            if (checkDirections[i]) {
                //Handy function to indicate directions.
                neighbors.add((landscapeSnapshot[row-((int) Math.cos((Math.PI / 2) * i))][col-((int) Math.sin((Math.PI / 2) * i))]));
            }
        }
        return neighbors;
    }

    /**
     * Advances the current Landscape by one step following the rules of Conway's Game of Life.
     * This method creates a snapshot of the current landscape, calculates the next state
     * for each living cell based on its neighbors, and updates the landscape accordingly.
     * If the landscape returns to a previous state, the simulation is paused.
     * <p>
     * Also, P.S. I managed to track down that bug to here effectively when the array was large enough, the likelihood
     * of having cells with no neighbors increases and I just wasn't handling a potential null value.
     */
    public void advance() {
        // Create a snapshot of the current landscape
        Cell[][] landscapeFreeze = new Cell[getRows()][getCols()];

        // Copy the current state to the snapshot
        int cR = 0;
        for (Cell[] row : landscapeFreeze) {
            int cC = 0;
            for (Cell column: row) {
                landscapeFreeze[cR][cC] = (landscape[cR][cC]).clone();
                cC++;
            }
            cR++;
        }

        // Add the snapshot to the state history
        stateList.addLast(landscapeFreeze);

        // Get a list of living cells in the current landscape
        ArrayList<Cell> livingCells = new ArrayList<>();
        HashMap<Cell,ArrayList<Cell>> neighborMap = new HashMap<>();
        for (Cell[] row : landscape) {
            List<Cell> tempRow = Arrays.asList(row);
            HashSet<Cell> rowSet = new HashSet<>(tempRow);

            // Remove dead cells from the row
            rowSet.removeIf(cell -> cell.equals(new Cell(false)));

            // Add the living cells to the list. One of the potential nulls in question.
            if (!rowSet.isEmpty()) {
                livingCells.addAll(rowSet);
            }

        }

        // Calculate neighbors for each living cell and store them in neighborMap
        for (Cell cell : livingCells) {
            Integer[] identity = cellHashMap.get(cell);
            neighborMap.put(cell, getNeighbors(identity));
        }
        // Update the state of each living cell based on its neighbors
        for (Cell cell : livingCells) {
            ArrayList<Cell> reference = neighborMap.get(cell);
            //Second null value.
            if (reference == null) {
                reference = new ArrayList<>();
            }
            cell.updateState(reference);
        }
        // Check if the landscape has returned to a previous state, and pause the simulation if so
        if (this.equals(stateList.getLast())) {
            pause();
        }
    }

    /**
     * Reverts the current Landscape to the previous state stored in the state history.
     * This method undoes the last step of the simulation by restoring the landscape
     * to its previous state.
     */
    public void revert() {
        // Retrieve the previous state from the state history
        Cell[][] landscapeFreeze = stateList.pollLast();

        // Restore the landscape to the previous state
        int cR = 0;
        if (landscapeFreeze != null) {
            for (Cell[] row : landscapeFreeze) {
                int cC = 0;
                for (Cell column: row) {
                    landscape[cR][cC].setAlive(column.getAlive());
                    cC++;
                }
                cR++;
            }
        }
    }


    /**
     * Pauses the Game of Life simulation.
     * Sets the 'paused' flag to true and displays a message.
     */
    public void pause() {
        paused = true;
        System.out.println("paused");
    }

    public void setPause(boolean bool) {
        paused = bool;
        System.out.println("paused");
    }

    /**
     * Getter for paused state.
     * @return boolean state of pause variable.
     */
    public boolean getPaused() {
        return paused;
    }


    /**
     * Resumes the Game of Life simulation.
     * Sets the 'paused' flag to false, advances the simulation, and updates the window.
     *
     * @param window the JFrame used for displaying the simulation
     */
    public void play(Object window) {
        if (window instanceof LandscapeFrame || window instanceof LandscapeDisplay) {
            final AbstractLandscapePresenter window1;
            paused = false;
            if (window instanceof LandscapeFrame) {
                window1 = (LandscapeFrame) window;
            }
            else {
                window1 = (LandscapeDisplay) window;
            }
            advance();
            SwingUtilities.invokeLater(window1::repaint);
            if (!paused) {
                SwingUtilities.invokeLater(() -> play(window));
            }
        }

    }

    /*
    Quick note this was implemented slightly wrong in the original,
    rows and columns were switched, so I rewrote it.
     */
    /**
     * Draws the Cell to the given Graphics object at the specified scale.
     * <p>
     * An alive Cell is drawn with a black color; a dead Cell is drawn gray.
     * 
     * @param g     the Graphics object on which to draw
     * @param scale the scale of the representation of this Cell
     */
    public void draw(Graphics g, int scale) {
        for (Cell[] row : landscape) {
            for (Cell col : row) {
                Integer[] identifier = cellHashMap.get(col);
                g.setColor(getCell(identifier).getAlive() ? Color.BLACK : Color.gray);
                g.fillOval((identifier[1] + 2) * scale, (identifier[0] + 2) * scale, scale, scale);
            }
        }
    }


    /**
     * Compares two two-dimensional arrays for equality.
     * Returns true if the arrays have the same dimensions and corresponding elements are equal.
     *
     * @param arr1 the first two-dimensional array
     * @param arr2 the second two-dimensional array
     * @return true if the arrays are equal, false otherwise
     */
    public static Boolean gridEquals(Object[][] arr1, Object[][] arr2) {
        boolean equivalent = false;
        HashSet<Boolean> trueList = new HashSet<>();
        try {
            trueList.add(arr1.length == arr2.length);
            int currentRow = 0;
            for (Object[] row : arr1) {
                int currentColumn = 0;
                trueList.add(row.length==arr2[currentRow].length);
                for (Object column : row) {
                    trueList.add(column.equals(arr2[currentRow][currentColumn]));
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


    /**
     * Overrode equals for some convenience. Pretty basic implementation.
     * @param input object to compare against.
     * @return boolean value representing whether input is equal to stored gameState.
     */
    @Override
    public boolean equals(Object input) {
        Boolean output;
        if (input instanceof Object[][]) {
            output = gridEquals(landscape, (Object[][]) input);
        }
        else {
            output = false;
        }
        return output;
    }


}