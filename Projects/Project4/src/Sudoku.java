import java.util.ListIterator;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * The Sudoku class represents a Sudoku game and provides methods for solving it.
 * It contains a Sudoku board, a graphical representation of the board,
 * and logic for solving the Sudoku puzzle.
 * <p>
 * Sudoku puzzles are solved using backtracking.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.0
 * @since 1.0
 */
public class Sudoku {
    public final Board board;
    public final LandscapeFrame landscapeFrame;
    final LinkedList<Cell> stack = new LinkedList<>();
    public LinkedList<Integer> constraintChecker;
    int count = 0;

    /**
     * Initializes a Sudoku game with the specified number of initial filled cells.
     *
     * @param initialCells The number of initially filled cells in the Sudoku puzzle.
     */
    public Sudoku(int initialCells) {
        if (initialCells > 0) this.board = new Board(initialCells);
        else this.board = new Board();
        board.setSleepTime(10);
        LandscapeFrame display = new LandscapeFrame(this.board, 30);
        System.out.println(this.board);
        this.landscapeFrame = display;
        this.board.setGame(this);
        this.board.setFrame(this.landscapeFrame);

    }

    /**
     * The main method to run the Sudoku solver.
     *
     * @param args Command-line arguments (not used).
     * @throws InterruptedException If an error occurs during thread sleep.
     */
    public static void main(String[] args) throws InterruptedException {
        final Sudoku sudoku = new Sudoku(5);
        System.out.println("Wait 3 Seconds");
        Thread.sleep(3000);
        sudoku.board.setSleepTime(1);
        sudoku.solve(sudoku.board.getSleepTime());
        if (sudoku.board.validSolution()) System.out.println("Victory");
        sudoku.landscapeFrame.repaint();
        while (sudoku.board.finished && sudoku.count<20000000) {
            sudoku.board.reset();
            sudoku.solve(0);
        };
    }

    /**
     * Finds the next value that can be placed in the given cell without
     * violating Sudoku rules.
     *
     * @param cell The cell for which to find the next valid value.
     * @return The next valid value for the cell, or 0 if no valid value is found.
     */
    public int findNextValue(Cell cell) {
        int row = cell.getRowNum();
        int column = cell.getColNum();
        for (int i = cell.getValue() + 1; i < 10; i++) {
            if (board.validValue(row, column, i)) return i;
        }
        return 0;
    }

    /**
     * Generates a list of constraint numbers for all cells on the board.
     * Constraint numbers represent the number of possible values for each cell.
     */
    public void generateConstraintCheck() {
        this.constraintChecker = new LinkedList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell refCell = board.getCell(i, j);
                int constraintNum;
                constraintNum = ((!refCell.isLocked()) ? refCell.getPossibleValues().size() : 0);
                constraintChecker.addLast(constraintNum);
            }
        }
    }

    /**
     * Finds the next cell to solve based on constraint numbers.
     * <p>
     * Two different implementations replaced original with
     * something I thought would be more efficient.
     *
     * @return The next cell to solve or null if no suitable cell is found.
     */
    public Cell findNextCell() {

        /*
        Cell cell;
        AtomicInteger index = new AtomicInteger(-1);
        int i = 1;
        AtomicBoolean continueCheck = new AtomicBoolean(true);
        do {
            final int finalI = i;
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
         */
        Cell cell;
        int min = 10;
        int mIndex = -1;
        for (Integer curr : constraintChecker) {
            if (curr < min && curr > 0) {
                min = curr;
                mIndex = constraintChecker.indexOf(curr);
            }
        }
        if (min == 10) return null;
        cell = board.getCell(Math.floorDiv(mIndex, 9), mIndex % 9);
        int val = findNextValue(cell);
        if (val == 0) return null;
        cell.setValue(val);
        cell.setLocked(true);
        generateConstraintCheck();
        return cell;
    }

    /**
     * Solves the Sudoku puzzle with a specified delay between steps.
     *
     * @param delay The delay between steps in milliseconds.
     * @return True if the puzzle is successfully solved, false otherwise.
     * @throws InterruptedException If an error occurs during thread sleep.
     */
    public boolean solve(int delay) throws InterruptedException {
        count=0;
        System.out.println("Solving");
        generateConstraintCheck();
        LinkedList<Cell> stack = new LinkedList<>();
        do {
            if (delay > 0) {
                System.out.println(board);
                TimeUnit.MILLISECONDS.sleep(delay);
                if (landscapeFrame != null) {
                    landscapeFrame.repaint();
                }
            }
            Cell next = findNextCell();
            while (next == null && !stack.isEmpty()) {
                Cell operator = stack.pop();
                operator.setLocked(false);
                operator.setValue(findNextValue(operator));
                generateConstraintCheck();
                if (operator.getValue() != 0) next = operator;
                count++;
                System.out.println(count);
            }
            if (next == null) {
                board.finished = true;
                return false;
            } else {
                next.setLocked(true);
                stack.push(next);
                generateConstraintCheck();
            }
        } while (stack.size() < (81 - this.board.getInitialLock()));
        assert landscapeFrame != null;
        landscapeFrame.repaint();
        board.finished = true;
        System.out.println("iterCount: "+count);
        return true;
    }

    /**
     * Solves a single step of the Sudoku puzzle.
     *
     * @return True if a step is successfully solved, false otherwise.
     */
    public boolean solveStep() {

        if (count == 0) {
            count++;
            System.out.println("Solving");
            generateConstraintCheck();
        } else if (stack.size() < (81 - this.board.getInitialLock())) {
            count++;
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
        }
        return true;
    }
}
