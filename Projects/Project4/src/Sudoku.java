import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Sudoku {

    public Board board;
    public LinkedList<Integer> constraintChecker;

    public Sudoku() {
        this.board = new Board(10);
    }

    public int findNextValue(Cell cell) {
        int row = cell.getRowNum();
        int column = cell.getColNum();
        for (int i = cell.getValue()+1; i < 10; i++) {
            if (board.validValue(row,column,i)) return i;
        }
        return 0;
    }


    public void generateConstraintCheck() {
        this.constraintChecker = new LinkedList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell refCell = board.getCell(i,j);
                int constraintNum;
                constraintNum = ((!refCell.isLocked()) ? refCell.getPossibleValues().size() : 0);
                constraintChecker.add(constraintNum);
            }
        }
    }

    public Cell findNextCell() {
        Cell cell;
        AtomicInteger index = new AtomicInteger(-1);
        int i = 1;
        AtomicBoolean continueCheck = new AtomicBoolean(true);
        do {
            final int finalI = i;
            CircularLinkedList.Node<Integer> node;
            Optional<Integer> val = constraintChecker.stream().filter(x-> x == finalI).findFirst();
            val.ifPresent(integer -> {
                int value = constraintChecker.indexOf(val.get());
                index.set(value);
                continueCheck.set(false);
            });
            i++;
            if (i>9) return null;
        } while (continueCheck.get());
        cell = board.getCell(Math.floorDiv(index.get(),9),index.get()%9);
        int val = findNextValue(cell);
        if (val == 0) return null;
        cell.setValue(val);
        cell.setLocked(true);
        generateConstraintCheck();
        return cell;
    }

    public boolean solve() {
        System.out.println("Solving");
        generateConstraintCheck();
        LinkedList<Cell> stack = new LinkedList<>();
        do {
            Cell next = findNextCell();
            while (next == null && !stack.isEmpty()) {
                Cell operator = stack.pop();
                operator.setLocked(false);
                operator.setValue(findNextValue(operator));
                generateConstraintCheck();
                if (operator.getValue() != 0) next = operator;
            }
            if (next == null) return false;
            else {
                next.setLocked(true);
                stack.push(next);
                generateConstraintCheck();
            }
        } while (stack.size() < (81-this.board.getInitialLock()));
       return true;
    }

    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku();
        System.out.println(sudoku.solve());
        System.out.println(sudoku.board);
        if (sudoku.board.validSolution()) System.out.println("Victory");
    }
}
