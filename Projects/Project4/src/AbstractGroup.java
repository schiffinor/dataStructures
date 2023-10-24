import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * The `AbstractGroup` class represents an abstract group of cells in a Sudoku puzzle.
 * It provides methods to manage the group's cells, check validity, and reset its state.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.1
 * @since 1.1
 */
public abstract class AbstractGroup {

     // An array containing valid integers from 1 to 9.
    public static final Integer[] intList = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    // A list containing valid integer values for the group.
    public static final ArrayList<Integer> validValueList = new ArrayList<>(Arrays.asList(intList));
    // A set to store the values of cells in the group.
    public final HashSet<Integer> cellSet;
    // A list of cells in the group with no assigned values.
    public final LinkedList<Cell> noValueCells;

    /**
     * Constructs an instance of the abstract group, initializing the cell set
     * and the list of cells with no assigned values.
     */
    public AbstractGroup() {
        cellSet = new HashSet<>();
        noValueCells = new LinkedList<>();
    }


    /**
     * Gets the set of cell values in the group.
     *
     * @return The set of cell values in the group.
     */
    public HashSet<Integer> getCellSet() {
        return this.cellSet;
    }

    /**
     * Gets the list of cells with no assigned values in the group.
     *
     * @return The list of cells with no assigned values.
     */
    public LinkedList<Cell> getNoValueCells() {
        return this.noValueCells;
    }

    /**
     * Checks if the group contains a specific cell value.
     *
     * @param cellNum The cell value to check.
     * @return `true` if the group contains the cell value, `false` otherwise.
     */
    public boolean hasCell(int cellNum) {
        return cellSet.contains(cellNum);
    }

    /**
     * Adds a cell to the group, updating its state and values.
     *
     * @param cell The cell to add to the group.
     * @return `true` if the cell is successfully added, `false` if it cannot be added.
     */
    public boolean addCell(Cell cell) {
        int cellValue = cell.getValue();
        if (!hasCell(cellValue) && cellValue != 0) {
            cellSet.add(cellValue);
        } else if (!hasCell(cellValue)) {
            noValueCells.add(cell);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Gets the count of cells with no assigned values in the group.
     *
     * @return The count of cells with no assigned values.
     */
    public int getEmptyPlaces() {
        return noValueCells.size();
    }

    /**
     * Gets a list of possible values that can be assigned to cells in the group.
     *
     * @return A list of possible values for unassigned cells in the group.
     */
    public LinkedList<Integer> getPossibleValues() {
        LinkedList<Integer> possibleValues = new LinkedList<>(validValueList);
        possibleValues.removeAll(cellSet);
        return possibleValues;
    }

    /**
     * Resets the state of the group by clearing cell values and the list of cells with
     * no assigned values.
     */
    public void resetState() {
        this.cellSet.clear();
        this.noValueCells.clear();
    }

    /**
     * Resets the state of the group based on an array of cells.
     *
     * @param cellList An array of cells to reset the state of the group.
     * @throws RuntimeException If the group contains multiple cells with the same value.
     */
    public void resetter(Cell[] cellList) throws RuntimeException {
        for (Cell cell : cellList) {
            int val = cell.getValue();
            if (!hasCell(val) && val != 0) {
                cellSet.add(cell.getValue());
            } else if (!hasCell(val)) {
                noValueCells.add(cell);
            } else {
                throw new RuntimeException("resetState(): cellSet contains multiple: " + val);
            }
        }
    }

    /**
     * Checks if the group is valid by verifying if it contains all the valid integer values.
     *
     * @return `true` if the group contains all valid values, `false` otherwise.
     */
    public boolean isValid() {
        return cellSet.containsAll(validValueList);
    }
}
