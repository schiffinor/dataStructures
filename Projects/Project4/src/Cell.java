import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

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


    public Cell() {
        this(0,0,0);
    }

    public Cell(Board board) {
        this(0,0,0, board);
    }


    public Cell(int rowNum, int columnNum) {
        this(rowNum, columnNum, 0, false);
    }


    public Cell(int rowNum, int columnNum, int value) {
        this(rowNum, columnNum, value, false);
    }

    public Cell(int rowNum, int columnNum, int value, Board board) {
        this(rowNum, columnNum, value, false, board);
    }


    public Cell(int rowNum, int columnNum, int value, boolean lockState) {
        this(rowNum, columnNum, value, lockState, null);
    }

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
        if (this.board!= null) {
            this.row = this.board.getRow(this.rowNum);
            this.column = this.board.getColumn(this.columnNum);
            this.subGrid = this.board.getSubGrid(this.subGridNum);
        }
    }





    public int getRowNum() {
        return this.rowNum;
    }


    public void setRowNum(int row) {
        this.rowNum = row;
    }


    public boolean hasRow() {
        return this.row!= null;
    }


    public Row getRow() {
        if (this.row != null) return this.row;
        else throw new IllegalStateException("Row not set");
    }


    public int getColNum() {
        return this.columnNum;
    }
    public void setColNum(int col) {
        this.columnNum = col;
    }


    public boolean hasColumn() {
        return this.column!= null;
    }


    public Column getColumn() {
        if (this.column != null) return this.column;
        else throw new IllegalStateException("Column not set");
    }


    public int[] getSubGridNum() {
        return this.subGridNum;
    }


    public String getSubGridString() {
        return this.subGridString;
    }


    public boolean hasSubGrid() {
        return this.subGrid!= null;
    }


    public SubGrid getSubGrid() {
        if (this.subGrid != null) return this.subGrid;
        else throw new IllegalStateException("SubGrid not set");
    }


    public boolean hasBoard() {
        return this.board!= null;
    }


    public Board getBoard() {
        if (this.board != null) return this.board;
        else throw new IllegalStateException("Board not set");
    }


    public void setBoard(Board board) {
        this.board = board;
    }


    public void updateInheritedFields() {
        this.fillState = this.value!= 0;
        this.subGridNum = Board.defineSubGridNum(this.rowNum, this.columnNum);
        this.subGridString = Board.defineSubGridString(this.subGridNum);
        if (!hasBoard()) {
            this.row = board.getRow(this.rowNum);
            this.column = board.getColumn(this.columnNum);
            this.subGrid = board.getSubGrid(this.subGridNum);
        }
    }


    public LinkedList<Integer> getPossibleValues() {
        LinkedList<Integer> output = new LinkedList<>();
        ArrayList<HashSet<Integer>> cellSets =
                new ArrayList<>(Arrays.asList(getRow().getCellSet(),
                        getColumn().getCellSet(), getSubGrid().getCellSet()));

        // Convert the list of numbers into a HashSet for fast lookups
        HashSet<Integer> numberSet = new HashSet<>(AbstractGroup.validValueList);

        for (int number : numberSet) {
            boolean inSets = false;
            for (HashSet<Integer> set : cellSets) {
                if (set.contains(number)) {
                    inSets = true;
                    break;
                }
            }
            if (!inSets) {
                output.add(number);
            }
        }

        return output;
    }


    public int getValue() {
        return this.value;
    }


    public boolean setValue(int newVal) {

        if (!isLocked()) {

            int prevValue = this.value;
            if (prevValue == newVal) return true;

            if (this.row.hasCell(newVal)) return false;
            if (this.column.hasCell(newVal)) return false;
            if (this.subGrid.hasCell(newVal)) return false;


            if (prevValue == 0) {
                this.row.getNoValueCells().remove(this);
            } else {
                this.row.getCellSet().remove(this.value);
                this.column.getCellSet().remove(this.value);
                this.subGrid.getCellSet().remove(this.value);

                if (newVal == 0) {
                    this.row.getNoValueCells().add(this);
                    this.value = newVal;
                    return true;
                }
            }
            this.value = newVal;
            this.row.getCellSet().add(this.value);
            this.column.getCellSet().add(this.value);
            this.subGrid.getCellSet().add(this.value);
            return true;
        }
        else return false;
    }


    public boolean isLocked() {
        return lockState;
    }


    public void setLocked(boolean lock) {
        this.lockState = lock;
        if (this.board!= null) {
            this.board.tickLockedCells(lock);
        }
    }


    public boolean isFilled() {
        return fillState;
    }

    @Override
    public String toString() {
        return (getValue() == 0)? "_" : String.valueOf(getValue());
    }


    public static void main(String[] args) {
        Random rand = new Random();
        Board board = new Board();
        System.out.println(board);
        for (Cell[] cellRow : board.getCellList()) {
            for (Cell c : cellRow) {
                System.out.println( c.setValue(rand.nextInt(0,10)) );
            }
        }
        System.out.println(board);
        for (Cell[] cellRow : board.getCellList()) {
            for (Cell c : cellRow) {
                int val = rand.nextInt(1,10);
                System.out.println( c.setValue(val) );
            }
        }
        System.out.println(board);
    }


    public void draw(Graphics g, int x, int y, int scale){
        char toDraw = (char) ((int) '0' + getValue());
        g.setColor(isLocked()? Color.BLUE : Color.RED);
        g.drawChars(new char[] {toDraw}, 0, 1, x, y);
    }
}
