/**
 * The `Column` class represents a column of cells in a Sudoku puzzle.
 * It provides methods to manage the group's cells, check validity, and reset its state.
 * Really simple same as abstract group, Row, and kind-of subgrid.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.1
 * @since 1.1
 */
public class Column extends AbstractGroup {
    public final int colNumber;
    public final Cell[] cells;

    /**
     * Constructs a new column.
     *
     * @param columnNumber corresponding index.
     */
    public Column(int columnNumber) {
        super();
        this.colNumber = columnNumber;
        this.cells = new Cell[9];
    }

    /**
     * Adds a cell to the column.
     *
     * @param cell The cell to add to the group.
     * @return whether added.
     */
    @Override
    public boolean addCell(Cell cell) {
        boolean cellAdded = super.addCell(cell);
        if (cellAdded) {
            cells[cell.getRowNum()] = cell;
        }
        return cellAdded;
    }

    /**
     * Resets the column.
     */
    public void resetState() {
        super.resetState();
        try {
            super.resetter(this.cells);
        } catch (RuntimeException e) {
            throw new RuntimeException("Column:resetState(): cellSet contains multiple cells of same value.", e);
        }
    }
}
