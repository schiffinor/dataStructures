/**
 * The `Row` class represents a row of cells in a Sudoku puzzle.
 * It provides methods to manage the group's cells, check validity, and reset its state.
 * Really simple same as abstract group, column, and kind-of subgrid.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.1
 * @since 1.1
 */
public class Row extends AbstractGroup {
    public final int rowNumber;
    final Cell[] cells;

    /**
     * Constructs a new row.
     *
     * @param rowNumber corresponding index.
     */
    public Row(int rowNumber) {
        super();
        this.rowNumber = rowNumber;
        this.cells = new Cell[9];
    }

    /**
     * Adds a cell to the row.
     *
     * @param cell The cell to add to the group.
     * @return whether added.
     */
    @Override
    public boolean addCell(Cell cell) {
        boolean cellAdded = super.addCell(cell);
        if (cellAdded) {
            cells[cell.getColNum()] = cell;
        }
        return cellAdded;
    }

    /**
     * Resets the row.
     */
    public void resetState() {
        super.resetState();
        try {
            super.resetter(this.cells);
        } catch (RuntimeException e) {
            throw new RuntimeException("Row:resetState(): cellSet contains multiple cells of same value.", e);
        }
    }
}
