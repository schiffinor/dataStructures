import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

public class Landscape {

    /**
     * The underlying grid of Cells for Conway's Game
     */
    public Cell[][] landscape;
    private Random rand;
    public LinkedList<Cell[][]> stateList;
    public HashMap<String,LinkedList<Cell[][]>> previousGame;

    public HashMap<Cell,Integer[]> cellHashMap;

    /**
     * The original probability each individual Cell is alive
     */
    private double initialChance;

    /**
     * Constructs a Landscape of the specified number of rows and columns.
     * <p>
     * All Cells are initially dead.
     * 
     * @param rows    the number of rows in the Landscape
     * @param columns the number of columns in the Landscape
     */
    public Landscape(int rows, int columns) {
        new Landscape(rows, columns, 0);
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
        initialChance = chance;
        rand = new Random();
        landscape = new Cell[rows][columns];
        stateList = new LinkedList<>();
        previousGame = new HashMap<>();
        reset();
    }

    /**
     * Recreates the Landscape according to the specifications given in its initial construction.
     */
    public void reset() {
        int curRow = 0;
        cellHashMap = new HashMap<>();
        for (Cell[] row : landscape) {
            int curCol = 0;
            for (Cell column: row) {
                double choiceDouble = rand.nextDouble(0,100);
                landscape[curRow][curCol] = new Cell(choiceDouble < initialChance);
                Integer[] identifier = new Integer[2];
                identifier[0] = curRow;
                identifier[1] = curCol;
                cellHashMap.put(landscape[curRow][curCol], identifier);
                curCol++;
            }
            curRow++;
        }
        stateList.clear();
        stateList.addFirst(landscape);
        previousGame.put("previousGame",stateList);
    }

    /**
     * Returns the number of rows in the Landscape.
     * 
     * @return the number of rows in the Landscape
     */
    public int getRows() {
        return landscape.length;
    }

    /**
     * Returns the number of columns in the Landscape.
     * 
     * @return the number of columns in the Landscape
     */
    public int getCols() {
        return landscape[0].length;
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
     */
    public String toString() {
        return arrayString(landscape);
    }

    public static String arrayString(Object[][] arr) {
        StringBuilder outString = new StringBuilder();

        for (Object[] row : arr) {
            outString.append("[ ");
            for (Object column: row) {
                try {
                    outString.append(column.toString()).append(" ");
                } catch (Exception NullPointerException) {
                    outString.append("null").append(" ");
                }
            }
            outString.append("]\n");
        }
        return outString.toString();
    }

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
        Cell[][] landscapeSnapshot = landscape.clone();
        ArrayList<Cell> neighbors = new ArrayList<>();
        boolean[] checkDirections = new boolean[4];
        checkDirections[0] = (row > 0);
        checkDirections[1] = (col > 0);
        checkDirections[2] = (row < getRows()-1);
        checkDirections[3] = (col < getCols()-1);
        try {
            Cell refCell = landscapeSnapshot[row][col];
        } catch (Exception ArrayIndexOutOfBoundsException) {
            System.out.println("Cell not within bounds of game. Neighbors DNE.");
        }
        for (int i = 0; i < 4; i++) {
            if (checkDirections[i]) {
                neighbors.add((landscapeSnapshot[row-((int) Math.cos((Math.PI / 2) * i))][col-((int) Math.sin((Math.PI / 2) * i))]).clone());
            }
        }
        return neighbors;
    }

    /**
     * Advances the current Landscape by one step. 
     */
    public void advance() {
        ArrayList<Cell> livingCells = new ArrayList<>();
        HashMap<Cell,ArrayList<Cell>> neighborMap = new HashMap<>();
        for (Cell[] row : landscape) {
            List<Cell> tempRow = Arrays.asList(row);
            HashSet<Cell> rowSet = new HashSet<>(tempRow);
            rowSet.removeIf(cell -> cell.equals(new Cell(false)));
            livingCells.addAll(rowSet);
        }
        for (Cell cell : livingCells) {
            neighborMap.put(cell, (ArrayList<Cell>) getNeighbors(cellHashMap.get(cell)).clone());
        }
        for (Cell cell : livingCells) {
            cell.updateState(neighborMap.get(cell));
        }
        Cell[][] landscapeSnapshot = landscape.clone();
        int curRow = 0;
        for (Cell[] row : landscape) {
            int curCol = 0;
            for (Cell column: row) {
                landscapeSnapshot[curRow][curCol] = (landscape[curRow][curCol]).clone();
                curCol++;
            }
            curRow++;
        }
        stateList.addLast(landscapeSnapshot);
    }

    public void revert() {
        Cell[][] landscapeSnapshot = stateList.getLast();
        stateList.removeLast();
        int curRow = 0;
        for (Cell[] row : landscapeSnapshot) {
            int curCol = 0;
            for (Cell column: row) {
                landscape[curRow][curCol] = column.clone();
                curCol++;
            }
            curRow++;
        }
    }


    /**
     * Draws the Cell to the given Graphics object at the specified scale.
     * <p>
     * An alive Cell is drawn with a black color; a dead Cell is drawn gray.
     * 
     * @param g     the Graphics object on which to draw
     * @param scale the scale of the representation of this Cell
     */
    public void draw(Graphics g, int scale) {
        for (int x = 0; x < getRows(); x++) {
            for (int y = 0; y < getCols(); y++) {
                g.setColor(getCell(x, y).getAlive() ? Color.BLACK : Color.gray);
                g.fillOval(x * scale, y * scale, scale, scale);
            }
        }
    }

    public static void main(String[] args) {
    }
}
