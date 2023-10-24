import java.util.Arrays;

/**
 * The `SubGrid` class represents a subGrid of cells in a Sudoku puzzle.
 * It provides methods to manage the group's cells, check validity, and reset its state.
 * Really simple kind-of the same as abstract group, column, and row.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.1
 * @since 1.1
 */
public class SubGrid extends AbstractGroup {
    public final int[] gridIdentifier;
    public final String stringGridIdentifier;
    public final Cell[][] cells;

    /**
     * Constructs a new subGrid.
     *
     * @param gridIdentifier corresponding double index.
     */
    public SubGrid(int[] gridIdentifier) {
        this.gridIdentifier = gridIdentifier;
        stringGridIdentifier = Arrays.toString(gridIdentifier);
        cells = new Cell[3][3];
    }

    /**
     * Adds a cell to the subGrid.
     *
     * @param cell The cell to add to the group.
     * @return whether added.
     */
    @Override
    public boolean addCell(Cell cell) {
        boolean cellAdded = super.addCell(cell);
        if (cellAdded) {
            cells[(cell.getRowNum() + 1) % 3][(cell.getColNum() + 1) % 3] = cell;
        }
        return cellAdded;
    }

    /**
     * Resets the subGrid.
     */
    public void resetState() {
        super.resetState();
        for (Cell[] cellRow : this.cells) {
            super.resetState();
            try {
                super.resetter(cellRow);
            } catch (RuntimeException e) {
                throw new RuntimeException("Row:resetState(): cellSet contains multiple cells of same value.", e);
            }
        }
    }
}
