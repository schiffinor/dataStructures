import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayList;

public abstract class AbstractGroup {
    public HashSet<Integer> cellSet;
    public LinkedList<Cell> noValueCells;
    public static Integer[] intList = new Integer[]{1,2,3,4,5,6,7,8,9};

    public static ArrayList<Integer> validValueList = new ArrayList<>(Arrays.asList(intList));

    public AbstractGroup() {
        ;
        cellSet = new HashSet<Integer>();
        noValueCells = new LinkedList<Cell>();
    }


    public HashSet<Integer> getCellSet() {
        return this.cellSet;
    }


    public LinkedList<Cell> getNoValueCells() {
        return this.noValueCells;
    }


    public boolean hasCell(int cellNum) {
        return cellSet.contains(cellNum);
    }


    public boolean addCell(Cell cell) {
        int cellValue = cell.getValue();
        if (!hasCell(cellValue) && cellValue!= 0) {
            cellSet.add(cellValue);
        } else if (!hasCell(cellValue)) {
            noValueCells.add(cell);
        } else {
            return false;
        }
        return true;
    }


    public int getEmptyPlaces() {
        return noValueCells.size();
    }


    public LinkedList<Integer> getPossibleValues() {
        LinkedList<Integer> possibleValues = new LinkedList<>(validValueList);
        possibleValues.removeAll(cellSet);
        return possibleValues;
    }



    public void resetState() {
        this.cellSet.clear();
        this.noValueCells.clear();
    }

    public void resetter(Cell[] cellList) throws RuntimeException {
        for (Cell cell : cellList) {
            int val  = cell.getValue();
            if (!hasCell(val) && val != 0) {
                cellSet.add(cell.getValue());
            } else if (!hasCell(val)) {
                noValueCells.add(cell);
            } else{
                throw new RuntimeException("resetState(): cellSet contains multiple: " + val);
            }
        }
    }


    public boolean isValid() {
        return cellSet.containsAll(validValueList);
    }
}
