import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

/**
 * Represents a cell in a Sudoku board.
 * <p>
 * This class defines individual cells within a Sudoku board. Each cell has a value,
 * fill state, and lock state. It also tracks its row, column, and subgrid location
 * within the board.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.1
 * @since 1.1
 */
public class Cell {
    public int value;
    public boolean fillState;
    public boolean lockState;
    public int rowNum;
    public int columnNum;
    public int[] subGridNum;
    public String subGridString;
    public Row row;
    public Column column;
    public SubGrid subGrid;
    public Board board;

    /**
     * Constructs a cell with default values.
     */
    public Cell() {
        this(0, 0, 0);
    }

    /**
     * Constructs a cell with default values on specified board.
     *
     * @param board board to use.
     */
    public Cell(Board board) {
        this(0, 0, 0, board);
    }

    /**
     * Constructs a cell at specified row and column with default value, and unlocked.
     *
     * @param rowNum to assign to the cell.
     * @param columnNum to assign to the cell.
     */
    public Cell(int rowNum, int columnNum) {
        this(rowNum, columnNum, 0, false);
    }

    /**
     * Constructs a cell at specified row and column with specified value, and unlocked.
     *
     * @param rowNum to assign to the cell.
     * @param columnNum to assign to the cell.
     * @param value to assign to cell.
     */
    public Cell(int rowNum, int columnNum, int value) {
        this(rowNum, columnNum, value, false);
    }

    /**
     * Constructs a cell at specified row and column with specified value on specified board,
     * unlocked.
     *
     * @param rowNum to assign to the cell.
     * @param columnNum to assign to the cell.
     * @param value to assign to cell.
     * @param board board to assign cell to.
     */
    public Cell(int rowNum, int columnNum, int value, Board board) {
        this(rowNum, columnNum, value, false, board);
    }

    /**
     * Constructs a cell at specified row and column with specified value,
     * with specified lockState.
     *
     * @param rowNum to assign to the cell.
     * @param columnNum to assign to the cell.
     * @param value to assign to cell.
     * @param lockState cell lockState.
     */
    public Cell(int rowNum, int columnNum, int value, boolean lockState) {
        this(rowNum, columnNum, value, lockState, null);
    }

    /**
     * Constructs a cell at specified row and column with specified value on specified board,
     * with specified lockState.
     *
     *
     * @param rowNum to assign to the cell.
     * @param columnNum to assign to the cell.
     * @param value to assign to cell.
     * @param board board to assign cell to.
     * @param lockState cell lockState.
     */
    public Cell(int rowNum, int columnNum, int value, boolean lockState, Board board) {
        this.value = value;
        this.rowNum = rowNum;
        this.columnNum = columnNum;
        this.fillState = this.value != 0;
        this.lockState = false;
        setLocked(lockState);
        this.subGridNum = Board.defineSubGridNum(this.rowNum, this.columnNum);
        this.subGridString = Board.defineSubGridString(this.subGridNum);
        this.board = board;
        if (this.board != null) {
            this.row = this.board.getRow(this.rowNum);
            this.column = this.board.getColumn(this.columnNum);
            this.subGrid = this.board.getSubGrid(this.subGridNum);
        }
    }

    /**
     * Tests.
     *
     * @param args nothing
     */
    public static void main(String[] args) {
        Random rand = new Random();
        Board board = new Board();
        System.out.println(board);
        for (Cell[] cellRow : board.getCellList()) {
            for (Cell c : cellRow) {
                System.out.println(c.setValue(rand.nextInt(0, 10)));
            }
        }
        System.out.println(board);
        for (Cell[] cellRow : board.getCellList()) {
            for (Cell c : cellRow) {
                int val = rand.nextInt(1, 10);
                System.out.println(c.setValue(val));
            }
        }
        System.out.println(board);
    }

    /**
     * Retrieves the row number of the cell.
     *
     * @return The row number.
     */
    public int getRowNum() {
        return this.rowNum;
    }

    /**
     * Sets the row number of the cell.
     * @param row number to set value to.
     */
    public void setRowNum(int row) {
        this.rowNum = row;
    }

    /**
     * Checks if the cell is part of a row.
     *
     * @return `true` if the cell is part of a row, `false` otherwise.
     */
    public boolean hasRow() {
        return this.row != null;
    }

    /**
     * Retrieves the row to which this cell belongs.
     *
     * @return The associated row.
     * @throws IllegalStateException if the row is not set.
     */
    public Row getRow() {
        if (this.row != null) return this.row;
        else throw new IllegalStateException("Row not set");
    }

    /**
     * Retrieves the column number of the cell.
     *
     * @return The column number.
     */
    public int getColNum() {
        return this.columnNum;
    }

    /**
     * Sets the column number of the cell.
     * @param col number to set value to.
     */
    public void setColNum(int col) {
        this.columnNum = col;
    }


    /**
     * Checks if the cell is part of a column.
     *
     * @return `true` if the cell is part of a row, `false` otherwise.
     */
    public boolean hasColumn() {
        return this.column != null;
    }

    /**
     * Retrieves the column to which this cell belongs.
     *
     * @return The associated column.
     * @throws IllegalStateException if the column is not set.
     */
    public Column getColumn() {
        if (this.column != null) return this.column;
        else throw new IllegalStateException("Column not set");
    }

    /**
     * Returns the subgrid number as an array of two integers.
     *
     * @return An array containing the subgrid's row and column numbers.
     */
    public int[] getSubGridNum() {
        return this.subGridNum;
    }

    /**
     * Retrieves the string representation of the subgrid location.
     *
     * @return The subgrid's string representation.
     */
    public String getSubGridString() {
        return this.subGridString;
    }

    /**
     * Checks if the cell is associated with a subgrid.
     *
     * @return `true` if the cell is part of a subgrid, `false` otherwise.
     */
    public boolean hasSubGrid() {
        return this.subGrid != null;
    }

    /**
     * Retrieves the subgrid to which this cell belongs.
     *
     * @return The associated subgrid.
     * @throws IllegalStateException if the subgrid is not set.
     */
    public SubGrid getSubGrid() {
        if (this.subGrid != null) return this.subGrid;
        else throw new IllegalStateException("SubGrid not set");
    }

    /**
     * Checks if the cell is associated with a Sudoku board.
     *
     * @return `true` if the cell is part of a board, `false` otherwise.
     */
    public boolean hasBoard() {
        return this.board != null;
    }

    /**
     * Retrieves the board to which this cell belongs.
     *
     * @return The associated Sudoku board.
     * @throws IllegalStateException if the board is not set.
     */
    public Board getBoard() {
        if (this.board != null) return this.board;
        else throw new IllegalStateException("Board not set");
    }

    /**
     * Associates a cell with a Sudoku board.
     *
     * @param board The associated Sudoku board.
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Updates the inherited fields of the cell, including fill state, subgrid number, subgrid string,
     * and associated row, column, and subgrid, if available.
     */
    public void updateInheritedFields() {
        this.fillState = this.value != 0;
        this.subGridNum = Board.defineSubGridNum(this.rowNum, this.columnNum);
        this.subGridString = Board.defineSubGridString(this.subGridNum);
        if (!hasBoard()) {
            this.row = board.getRow(this.rowNum);
            this.column = board.getColumn(this.columnNum);
            this.subGrid = board.getSubGrid(this.subGridNum);
        }
    }

    /**
     * Retrieves a list of possible values that can be assigned to this cell without violating the Sudoku rules.
     * <p>
     * This method calculates the possible values by checking the current cell's row, column, and subgrid to find
     * numbers that have not been used in any of these groups.
     *
     * @return A linked list containing the possible values for this cell.
     */
    public LinkedList<Integer> getPossibleValues() {

        // Create a linked list to store possible values
        LinkedList<Integer> output = new LinkedList<>();

        // Create an array list of sets to store the cell sets of the row, column, and subgrid
        ArrayList<HashSet<Integer>> cellSets =
                new ArrayList<>(Arrays.asList(getRow().getCellSet(),
                        getColumn().getCellSet(), getSubGrid().getCellSet()));

        // Convert the list of numbers into a HashSet for fast lookups
        HashSet<Integer> numberSet = new HashSet<>(AbstractGroup.validValueList);

        // Iterate through the numbers (1 to 9) to find the possible values
        for (int number : numberSet) {
            boolean inSets = false;

            // Check if the number is present in any of the cell sets (row, column, or subgrid)
            for (HashSet<Integer> set : cellSets) {
                if (set.contains(number)) {
                    inSets = true;
                    break;
                }
            }

            // If the number is not found in any of the cell sets, add it to the list of possible values
            if (!inSets) {
                output.add(number);
            }
        }
        return output;
    }

    /**
     * Retrieves the value of the cell.
     *
     * @return The current value of the cell.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Sets the value of the cell, if it is not locked, and updates the relevant data structures.
     *
     * @param newVal The new value to set for the cell.
     * @return True if the value was successfully set, false otherwise.
     */
    public boolean setValue(int newVal) {

        // Check if the cell is not locked
        if (!isLocked()) {

            // If the new value is the same as the current value, return true
            int prevValue = this.value;
            if (prevValue == newVal) return true;

            // Check if the new value already exists in the cell's row, column, or subgrid
            if (this.row.hasCell(newVal)) return false;
            if (this.column.hasCell(newVal)) return false;
            if (this.subGrid.hasCell(newVal)) return false;

            // If the previous value was 0, remove the cell from the row's list of cells with no value
            if (prevValue == 0) {
                this.row.getNoValueCells().remove(this);
            } else {
                // Remove the previous value from the cell sets of the row, column, and subgrid
                this.row.getCellSet().remove(this.value);
                this.column.getCellSet().remove(this.value);
                this.subGrid.getCellSet().remove(this.value);

                // If the new value is 0, add the cell to the row's list of cells with no value
                if (newVal == 0) {
                    this.row.getNoValueCells().add(this);
                    this.value = newVal;
                    return true;
                }
            }

            // Set the new value and add it to the cell sets of the row, column, and subgrid
            this.value = newVal;
            this.row.getCellSet().add(this.value);
            this.column.getCellSet().add(this.value);
            this.subGrid.getCellSet().add(this.value);
            return true;
        } else return false;
    }

    /**
     * Checks whether the cell is locked.
     *
     * @return True if the cell is locked, false if it is not locked.
     */
    public boolean isLocked() {
        return lockState;
    }

    /**
     * Sets the lock state of the cell.
     *
     * @param lock True to lock the cell, false to unlock it.
     */
    public void setLocked(boolean lock) {
        this.lockState = lock;
        if (this.board != null) {
            this.board.tickLockedCells(lock);
        }
    }

    /**
     * Checks whether the cell is filled (has a non-zero value).
     *
     * @return True if the cell is filled, false if it is empty.
     */
    public boolean isFilled() {
        return fillState;
    }

    /**
     * Converts the cell to its string representation, which is the value if not 0, or an underscore (_) if 0.
     *
     * @return The string representation of the cell.
     */
    @Override
    public String toString() {
        return (getValue() == 0) ? "_" : String.valueOf(getValue());
    }

    /**
     * Draws the cell on the graphics context at the specified coordinates with the given scale.
     *
     * @param g     The graphics context to draw on.
     * @param x     The x-coordinate of the cell's position.
     * @param y     The y-coordinate of the cell's position.
     * @param scale The scale (size) of the cell.
     */
    public void draw(Graphics g, int x, int y, int scale) {
        char toDraw = (char) ((int) '0' + getValue());
        g.setColor(isLocked() ? Color.BLUE : Color.RED);
        g.drawChars(new char[]{toDraw}, 0, 1, x, y);
    }
}
