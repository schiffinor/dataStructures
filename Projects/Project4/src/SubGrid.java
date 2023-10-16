import java.util.Arrays;

public class SubGrid extends AbstractGroup{
    public int[]  gridIdentifier;
    public String stringGridIdentifier;
    public Cell[][] cells;
    public SubGrid(int[] gridIdentifier) {
        this.gridIdentifier = gridIdentifier;
        stringGridIdentifier = Arrays.toString(gridIdentifier);
        cells = new Cell[3][3];
    }

    @Override
    public boolean addCell(Cell cell) {
        boolean cellAdded = super.addCell(cell);
        if (cellAdded) {
            cells[(cell.getRowNum()+1)%3][(cell.getColNum()+1)%3] = cell;
        }
        return cellAdded;
    }


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
