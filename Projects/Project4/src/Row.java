public class Row extends AbstractGroup{
    public int rowNumber;
    Cell[] cells;
    public Row(int rowNumber) {
        super();
        this.rowNumber = rowNumber;
        this.cells = new Cell[9];
    }


    @Override
    public boolean addCell(Cell cell) {
        boolean cellAdded = super.addCell(cell);
        if (cellAdded) {
            cells[cell.getColNum()] = cell;
        }
        return cellAdded;
    }


    public void resetState() {
        super.resetState();
        try {
            super.resetter(this.cells);
        } catch (RuntimeException e) {
            throw new RuntimeException("Row:resetState(): cellSet contains multiple cells of same value.", e);
        }
    }
}
