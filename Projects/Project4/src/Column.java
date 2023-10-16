public class Column extends AbstractGroup{
    public int colNumber;
    public Cell[] cells;


    public Column(int columnNumber) {
        super();
        this.colNumber = columnNumber;
        this.cells = new Cell[9];
    }


    @Override
    public boolean addCell(Cell cell) {
        boolean cellAdded = super.addCell(cell);
        if (cellAdded) {
            cells[cell.getRowNum()] = cell;
        }
        return cellAdded;
    }


    public void resetState() {
        super.resetState();
        super.resetState();
        try {
            super.resetter(this.cells);
        } catch (RuntimeException e) {
            throw new RuntimeException("Column:resetState(): cellSet contains multiple cells of same value.", e);
        }
    }
}
