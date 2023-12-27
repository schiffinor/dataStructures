import java.awt.Graphics;
import java.util.*;

public class Maze implements Iterable<Cell>, Cloneable {

    /**
     * The number of rows and columns in this Maze.
     */
    private int rows, cols;
    /**
     * The density of this Maze. Each Cell independently has probability
     * {@code density} of being an OBSTACLE.
     */
    private final double density;
    /**
     * The 2-D array of Cells making up this Maze.
     */
    private Cell[][] landscape;

    /**
     * Constructs a Maze with the given number of rows and columns. Each Cell
     * independently has probability {@code density} of being an OBSTACLE.
     *
     * @param rows    the number of rows.
     * @param columns the number of columns.
     * @param density the probability of any individual Cell being an OBSTACLE.
     */
    public Maze(int rows, int columns, double density) {
        this.rows = rows;
        this.cols = columns;
        this.density = density;
        landscape = new Cell[rows][columns];
        reinitialize();
    }

    public static void main(String[] args) {
        Maze ls = new Maze(7, 7, .2);
        System.out.println(ls);
    }

    /**
     * An iterator which iterates through all the Cells in the Maze row by row and
     * column by column.
     */
    public Iterator<Cell> iterator() {
        return new Iterator<>() {
            int r, c;

            public boolean hasNext() {
                return r < getRows();
            }

            public Cell next() {
                Cell next = get(r, c);
                c++;
                if (c == getCols()) {
                    r++;
                    c = 0;
                }
                return next;
            }
        };
    }

    /**
     * Initializes every Cell in the Maze.
     */
    public void reinitialize() {
        Random rand = new Random();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                landscape[r][c] = new Cell(r, c, rand.nextDouble() < density ? CellType.OBSTACLE : CellType.FREE);
            }
        }
    }

    /**
     * Calls {@code reset} on every Cell in this Maze.
     */
    public void reset() {
        for (Cell cell : this)
            cell.reset();
    }

    /**
     * Returns the number of rows in the Maze.
     *
     * @return the number of rows in the Maze.
     */
    public int getRows() {
        return rows;
    }

    /**
     * Returns the number of columns in the Maze.
     *
     * @return the number of columns in the Maze.
     */
    public int getCols() {
        return cols;
    }

    /**
     * Returns the Cell at the specified row and column in the Maze.
     *
     * @param row the row
     * @param col the column
     * @return the Cell at the specified row and column in the Maze.
     */
    public Cell get(int row, int col) {
        return landscape[row][col];
    }

    /**
     * Returns a LinkedList of the non-OBSTACLE Cells neighboring the specified
     * Cell.
     *
     * @param c the Cell to explore around.
     * @return a LinkedList of the non-OBSTACLE Cells neighboring the specified
     * Cell.
     */
    public LinkedList<Cell> getNeighbors(Cell c) {
        LinkedList<Cell> cells = new LinkedList<>();
        int[][] steps = new int[][]{{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        for (int[] step : steps) {
            int nextRow = c.getRow() + step[0];
            int nextCol = c.getCol() + step[1];
            if (nextRow >= 0 && nextRow < getRows() && nextCol >= 0 && nextCol < getCols()
                    && get(nextRow, nextCol).getType() != CellType.OBSTACLE)
                cells.addLast(get(nextRow, nextCol));
        }
        return cells;
    }

    public String toSString() {
        StringBuilder output = new StringBuilder();
        output.append("-".repeat(cols + 3)).append("\n");
        for (Cell[] cells : landscape) {
            output.append("| ");
            for (Cell cell : cells) {
                output.append(cell.getType() == CellType.OBSTACLE ? 'X' : ' ');
            }
            output.append("|\n");
        }
        return output.append("-".repeat(cols + 3)).toString();
    }

    /**
     * Get a string representation of the Sudoku board, formatted as a text-based grid.
     *
     * @return A string representing the Sudoku board's current state.
     */
    @Override
    public String toString() {
        StringBuilder outputBuild = new StringBuilder();
        int rows = getRows();
        int columns = getCols();
        outputBuild.append("┌─").append(String.join("", Collections.nCopies(rows, "─"))).append("─┐\n");
        for (int i = 0; i < rows; i++) {
            outputBuild.append("│ ");
            for (int j = 0; j < columns; j++) {
                outputBuild.append((landscape[i][j].getType() == CellType.FREE) ? " " : "█");
            }
            outputBuild.append(" │\n");
        }
        outputBuild.append("└─").append(String.join("", Collections.nCopies(rows, "─"))).append("─┘\n");
        return outputBuild.toString();
    }

    /**
     * Calls {@code drawType} on every Cell in this Maze.
     *
     * @param g
     * @param scale
     */
    public void draw(Graphics g, int scale) {
        for (Cell cell : this)
            cell.drawType(g, scale);
    }

    @Override
    public Maze clone() {
        try {
            Maze clone = (Maze) super.clone();
            clone.cols = this.cols;
            clone.rows = this.rows;
            clone.landscape = new Cell[rows][cols];
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    clone.landscape[r][c] = new Cell(r, c, get(r, c).getType());
                }
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
