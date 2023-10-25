import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The `Board` class represents a Sudoku game board and provides methods to
 * manipulate and interact with the board.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.1
 * @since 1.1
 */
public class Board {

    public boolean finished;
    private String fileSource;
    private Row[] rowList;
    private Column[] columnList;
    private HashMap<String, SubGrid> subGridList;
    private Cell[][] cellList;
    private LinkedList<Cell> cellSelections;
    private int[] completionState;
    private double completionRatio;
    private String readFile;
    private String writeFile;
    private Random rand;
    private int lockedCellCount;
    private int constructorUsed;
    private int initialLockedCells;
    private int sleepTime;
    private int resetRunCount;
    private Sudoku game;
    private LandscapeFrame frame;


    /**
     * Constructs an instance of the `Board` class.
     */
    public Board() {
        resetRunCount = 0;
        this.constructorUsed = 0;
        this.sleepTime = 100;
        reset();
    }

    /**
     * Constructs a `Board` instance with the specified number of initial locked cells.
     *
     * @param initialLockedCells The number of initial locked cells on the board.
     */
    public Board(int initialLockedCells) {
        this();
        this.constructorUsed = 1;
        lockedCellInit(initialLockedCells);
    }

    /**
     * Constructs a `Board` instance by reading Sudoku board data from a file.
     *
     * @param fileName The name of the file containing Sudoku board data.
     */
    public Board(String fileName) {
        this();
        this.constructorUsed = 2;
        boardFromFile(fileName);
    }

    /**
     * Reads Sudoku board data from a file and returns a 2D array representing
     * the board's values.
     *
     * @param fileName The name of the file containing Sudoku board data.
     * @return A 2D array representing the Sudoku board's values.
     */
    public static int[][] readFile(String fileName) {

        // Create a 2D array to store Sudoku board values.
        int[][] valueList = new int[9][9];

        // Create an empty board as a fallback in case of errors.
        int[][] emptyList = new int[9][9];

        // Flag to continue processing or not.
        boolean continueOn = true;

        // Initialize a file reader for reading the Sudoku board file.
        BufferedReader fileGetter = null;
        try {
            fileGetter = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            continueOn = false;
            System.out.println("File not found.");
            System.out.println("Board.read():: unable to open file " + fileName);
        }
        if (continueOn) {
            String currLine;
            int rowCount = 0;
            try {
                do {
                    // Read a line from the file.
                    currLine = fileGetter.readLine();

                    // Exit the loop if the end of the file is reached or the board is fully read.
                    if (currLine == null || rowCount > 9) break;

                    // Create a regular expression pattern to match digits.
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(currLine);
                    ArrayList<Integer> matchList = new ArrayList<>();

                    // Find and collect integers from the current line.
                    while (matcher.find()) {
                        matchList.add(Integer.valueOf(matcher.group()));
                    }

                    if (matchList.size() == 9) {
                        // Create a set of acceptable integers from 0 to 9.
                        Integer[] acceptableArray = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
                        HashSet<Integer> acceptableSet = new HashSet<>(Arrays.asList(acceptableArray));

                        // Check if all integers in the current line are acceptable.
                        if (acceptableSet.containsAll(matchList)) {
                            for (int i = 0; i < 9; i++) {
                                valueList[rowCount][i] = matchList.get(i);
                            }
                            rowCount++;
                        }
                    } else if (!matchList.isEmpty()) {
                        System.out.println("File not of correct format.");
                        throw new IOException("File not of correct format.");
                    }
                } while (true);

                // Close the file reader.
                fileGetter.close();

                // Return the parsed Sudoku board values.
                return valueList;

            } catch (IOException e) {
                System.out.println("Read Error.");
                System.out.println("Board.read():: error while reading " + fileName);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }

            // Output a default board if an error occurred during processing.
            System.out.println("Error occurred, outputting default file.");
        }

        // Return an empty board if the file is not found or in an incorrect format.
        return emptyList;
    }

    /**
     * Calculates and returns the subgrid coordinates (as an array) based on the given row and column numbers.
     *
     * @param rowNum    The row number within the Sudoku grid.
     * @param columnNum The column number within the Sudoku grid.
     * @return An array of subgrid coordinates in the format [row, column].
     */
    public static int[] defineSubGridNum(int rowNum, int columnNum) {
        return new int[]{Math.floorDiv(rowNum, 3) - 1, Math.floorDiv(columnNum, 3) - 1};
    }

    /**
     * Converts an array of subgrid coordinates into a string representation.
     *
     * @param subGridNum An array of subgrid coordinates in the format [row, column].
     * @return A string representation of the subgrid coordinates.
     */
    public static String defineSubGridString(int[] subGridNum) {
        return Arrays.toString(subGridNum);
    }

    public static void main(String[] args) {
        Board board = new Board();
        Board boardFromFile = new Board();
        Board boardByNum = new Board(30);
        /*for (int i = 1; i <3; i++) {
            String fileName = "board"+i+".txt";
            int[][] file;
            file = readFile(fileName);
            System.out.println(Arrays.deepToString(file));
            boardFromFile = new Board(fileName);
            System.out.println(boardFromFile);
        }
        System.out.println(board);
        System.out.println(boardFromFile);
        System.out.println(boardFromFile.get(2,5));
        System.out.println(boardFromFile.getPossibleValues(2,5));*/

        System.out.println(boardByNum);

    }

    /**
     * Gets the frame associated with the board.
     *
     * @return The landscape frame associated with the board.
     */
    public LandscapeFrame getFrame() {
        return frame;
    }

    /**
     * Sets the landscape frame associated with the board.
     *
     * @param frame The landscape frame to be set for the board.
     */
    public void setFrame(LandscapeFrame frame) {
        this.frame = frame;
    }

    /**
     * Initializes the locked cells on the board.
     *
     * @param initialLockedCells The number of cells to lock initially.
     * @throws RuntimeException if no valid value is found for a locked cell.
     */
    public void lockedCellInit(int initialLockedCells) {
        this.initialLockedCells = initialLockedCells;
        boolean continueGenerating = true;

        do {
            try {
                // Create a copy of the cell selections list.
                LinkedList<Cell> cells = cellSelections.clone();
                LinkedList<Cell> lockedCells = new LinkedList<>();

                // Randomly select and lock cells.
                for (int i = 0; i < this.initialLockedCells; i++) {
                    boolean next = false;
                    Cell lockCell;

                    do {
                        lockCell = cells.get(rand.nextInt(cells.size()));
                        next = !lockedCells.contains(lockCell);
                    } while (!next);

                    cells.remove(lockCell);
                    lockedCells.addLast(lockCell);
                }

                // Set valid values for locked cells.
                do {
                    Cell cell = lockedCells.pop();
                    LinkedList<Integer> possibleValues = getPossibleValues(cell);

                    if (possibleValues.isEmpty()) {
                        throw new RuntimeException("NO VALUE FOUND FOR " + cell + ".");
                    }

                    set(cell.getRowNum(), cell.getColNum(),
                            possibleValues.get(rand.nextInt(possibleValues.size())), true);
                } while (!lockedCells.isEmpty());

                continueGenerating = false;
            } catch (RuntimeException e) {
                System.out.println("Error during board generation. \n If issue persists try less locked Cells.");
                System.out.println(e);
                resetRunCount--;
                clearBoard();

            }
        } while (continueGenerating);
    }

    /**
     * Clears the board and resets it to its initial state.
     */
    public void clearBoard() {
        int prev = getConstructorUsed();
        setConstructorUsed(0);
        reset();
        setConstructorUsed(prev);
    }

    /**
     * Converts a string representation of an array into an index.
     *
     * @param arrayString The string representation of an array.
     * @return The computed index.
     */
    public int arrayToIndex(String arrayString) {
        int[] array = new int[2];
        array[0] = Integer.getInteger(arrayString.substring(1, 1));
        array[1] = Integer.getInteger(arrayString.substring(4, 4));
        return arrayToIndex(array);
    }

    /**
     * Converts an array of integers into an index.
     *
     * @param array The array containing row and column values.
     * @return The computed index.
     */
    public int arrayToIndex(int[] array) {
        return arrayToIndex(array[0], array[1]);
    }

    /**
     * Computes an index based on row and column values.
     *
     * @param row    The row value.
     * @param column The column value.
     * @return The computed index.
     */
    public int arrayToIndex(int row, int column) {
        return row * 9 + column;
    }

    /**
     * Retrieves a list of possible values for a cell at the specified row and column.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return A list of possible values for the cell.
     */
    public LinkedList<Integer> getPossibleValues(int row, int col) {
        return getPossibleValues(getCell(row, col));
    }

    /**
     * Retrieves a list of possible values for a given cell.
     *
     * @param cell The cell for which to find possible values.
     * @return A list of possible values for the cell.
     */
    public LinkedList<Integer> getPossibleValues(Cell cell) {
        return cell.getPossibleValues();
    }

    /**
     * Calculates the completion ratio of the board.
     *
     * @return The completion ratio as a decimal value.
     */
    private double calculateCompletionRatio() {
        return Math.divideExact(this.completionState[0], this.completionState[1]);
    }

    /**
     * Gets the row at the specified index.
     *
     * @param rowNum The index of the row.
     * @return The row object.
     */
    public Row getRow(int rowNum) {
        return rowList[rowNum];
    }

    /**
     * Gets the column at the specified index.
     *
     * @param columnNum The index of the column.
     * @return The column object.
     */
    public Column getColumn(int columnNum) {
        return columnList[columnNum];
    }

    /**
     * Gets the subgrid with the specified coordinates.
     *
     * @param subGridNum An array containing the subgrid coordinates.
     * @return The subgrid object.
     */
    public SubGrid getSubGrid(int[] subGridNum) {
        return subGridList.get(defineSubGridString(subGridNum));
    }

    /**
     * Retrieves the 2D array of cells representing the board.
     *
     * @return The 2D array of cells representing the board.
     */
    public Cell[][] getCellList() {
        return cellList;
    }

    /**
     * Gets the number of columns in the board.
     *
     * @return The number of columns in the board.
     */
    public int getCols() {
        return columnList.length;
    }

    /**
     * Gets the number of rows in the board.
     *
     * @return The number of rows in the board.
     */
    public int getRows() {
        return rowList.length;
    }

    /**
     * Gets the cell at the specified row and column.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The cell object.
     */
    public Cell get(int row, int col) {
        return getCell(row, col);
    }

    /**
     * Checks if a cell at the specified row and column is locked.
     *
     * @param r The row of the cell.
     * @param c The column of the cell.
     * @return True if the cell is locked; false otherwise.
     */
    public boolean isLocked(int r, int c) {
        return cellList[r][c].isLocked();
    }

    /**
     * Updates the count of locked cells based on the given state.
     *
     * @param state True to increment the count, false to decrement.
     */
    public void tickLockedCells(boolean state) {
        if (state) this.lockedCellCount += 1;
        else this.lockedCellCount -= 1;
    }


    /**
     * Retrieves the count of locked cells on the board.
     *
     * @return The count of locked cells.
     */
    public int numLocked() {
        return this.lockedCellCount;
    }

    /**
     * Gets the value of the cell at the specified row and column.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The value of the cell.
     */
    public int value(int row, int col) {
        return cellList[row][col].getValue();
    }

/**
 * Sets the value of the cell at the specified row and column.
 *
 * @param row   The row of the cell.
 * @param col   The column of the cell.
 */
    public void set(int row, int col, int value) {
        cellList[row][col].setValue(value);
    }

    /**
     * Sets the value of the cell at the specified row and column and locks it.
     *
     * @param row   The row of the cell.
     * @param col   The column of the cell.
     */
    public void set(int row, int col, int value, boolean locked) {
        set(row, col, value);
        cellList[row][col].setLocked(locked);
    }

    /**
     * Checks if a given value is valid for a specific cell on the board.
     *
     * @param row The row index of the cell.
     * @param col The column index of the cell.
     * @param value The value to check for validity.
     * @return {@code true} if the value is valid for the cell; {@code false} otherwise.
     */
    public boolean validValue(int row, int col, int value) {

        if (row < 0 || row >= getRows() || col < 0 || col >= getCols()) return false;
        if (value < 0 || value > 9) return false;

        Cell cell = getCell(row, col);

        if (cell.getValue() == value) return true;
        if (cell.row.hasCell(value)) return false;
        if (cell.column.hasCell(value)) return false;
        return !cell.subGrid.hasCell(value);

    }

    /**
     * Checks if the current board represents a valid Sudoku solution.
     *
     * @return {@code true} if the board is a valid solution; {@code false} otherwise.
     */
    public boolean validSolution() {
        for (Row row : rowList) {
            if (!row.isValid()) return false;
        }
        for (Column col : columnList) {
            if (!col.isValid()) return false;
        }
        for (SubGrid subGrid : subGridList.values()) {
            if (!subGrid.isValid()) return false;
        }
        return true;
    }

    /**
     * Get the cell at the specified row and column indices.
     *
     * @param rowNum The row index.
     * @param columnNum The column index.
     * @return The cell at the specified indices.
     */
    public Cell getCell(int rowNum, int columnNum) {
        return cellList[rowNum][columnNum];
    }

    /**
     * Get the current completion state of the Sudoku board.
     *
     * @return An array representing the completion state, where the first element is the
     * number of filled cells, and the second element is the total number of cells on the board.
     */
    public int[] getCompletionState() {
        return completionState;
    }

    /**
     * Get the completion ratio of the Sudoku board, indicating the percentage of filled cells.
     *
     * @return The completion ratio as a double value between 0.0 and 1.0.
     */
    public double getCompletionRatio() {
        return completionRatio;
    }

    /**
     * Get the name of the file from which the Sudoku board was read.
     *
     * @return The name of the input file.
     */
    public String getReadFile() {
        return readFile;
    }

    /**
     * Set the name of the file from which the Sudoku board was read.
     *
     * @param fileName The name of the input file.
     */
    public void setReadFile(String fileName) {
        this.readFile = fileName;
    }

    /**
     * Get the name of the file to which the Sudoku board is written.
     *
     * @return The name of the output file.
     */
    public String getWriteFile() {
        return writeFile;
    }

    /**
     * Set the name of the file to which the Sudoku board is written.
     *
     * @param fileName The name of the output file.
     */
    public void setWriteFile(String fileName) {
        this.writeFile = fileName;
    }

    /**
     * Update the inherited fields of the Sudoku board, including the completion ratio.
     */
    private void updateInheritedFields() {
        completionRatio = calculateCompletionRatio();
    }

    /**
     * Load a Sudoku board from a file and update the board state accordingly.
     *
     * @param fileName The name of the file containing Sudoku board data.
     */
    public void boardFromFile(String fileName) {
        this.fileSource = fileName;
        int[][] boardState = readFile(fileName);
        for (Cell[] cellRow : cellList) {
            for (Cell cell : cellRow) {
                int value = boardState[cell.getRowNum()][cell.getColNum()];
                cell.setValue(boardState[cell.getRowNum()][cell.getColNum()]);
                if (value != 0) {
                    this.completionState[0]++;
                    cell.setLocked(true);
                }
                updateInheritedFields();
            }
        }
        System.out.println(game);
        System.out.println(game.board);
    }

    /**
     * Get a string representation of the Sudoku board, formatted as a text-based grid.
     *
     * @return A string representing the Sudoku board's current state.
     */
    @Override
    public String toString() {
        StringBuilder outputBuild = new StringBuilder();
        outputBuild.append("┌───────┬───────┬───────┐ \n");
        for (int i = 0; i < 9; i++) {
            outputBuild.append("│ ");
            for (int j = 0; j < 9; j++) {
                outputBuild.append(cellList[i][j].toString());
                outputBuild.append(" ");
                if ((j + 1) % 3 == 0) {
                    outputBuild.append("│ ");
                }
            }
            outputBuild.append("\n");
            if ((i + 1) % 3 == 0 && i != 8) {
                outputBuild.append("├───────┼───────┼───────┤ \n");
            }
        }
        outputBuild.append("└───────┴───────┴───────┘ \n");
        return outputBuild.toString();
    }

    /**
     * Draw the Sudoku board on a graphics context with a given scaling factor.
     *
     * @param g     The graphics context to draw on.
     * @param scale The scaling factor for the board's dimensions.
     */
    public void draw(Graphics g, int scale) {
        g.drawRect(32, (int) (53 - scale * 0.95), getRows() * scale, getCols() * scale);
        g.drawRect(32, (int) (53 - scale * 0.95), getRows() / 3 * scale, getCols() / 3 * scale);
        g.drawRect(32, (int) (53 - scale * 0.95), getRows() / 3 * scale * 2, getCols() / 3 * scale * 2);
        g.drawRect(32 + getRows() / 3 * scale, (int) (53 - scale * 0.95 + getRows() / 3 * scale), getRows() / 3 * scale, getCols() / 3 * scale);
        g.drawRect(32 + getRows() / 3 * scale, (int) (53 - scale * 0.95) + getRows() / 3 * scale, getRows() / 3 * scale * 2, getCols() / 3 * scale * 2);
        g.drawRect(32 + getRows() / 3 * scale * 2, (int) (53 - scale * 0.95 + getRows() / 3 * scale * 2), getRows() / 3 * scale, getCols() / 3 * scale);
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                get(i, j).draw(g, j * scale + 40, i * scale + 50, scale);
            }
        }
        if (finished) {
            if (validSolution()) {
                g.setColor(new Color(0, 127, 0));
                g.drawChars("Hurray!".toCharArray(), 0, "Hurray!".length(), scale * 3 + 5, scale * 10 + 30);
            } else {
                g.setColor(new Color(127, 0, 0));
                g.drawChars("No solution!".toCharArray(), 0, "No Solution!".length(), scale * 3 + 5, scale * 10 + 30);
            }
        }
    }

    /**
     * Reset the Sudoku board by clearing the current state and initializing a new puzzle.
     */
    public void reset() {
        resetRunCount++;
        rand = new Random();
        rowList = new Row[9];
        columnList = new Column[9];
        subGridList = new HashMap<>();
        cellList = new Cell[9][9];
        cellSelections = new LinkedList<>();
        for (int i = 0; i < 9; i++) {
            Row row = new Row(i);
            Column column = new Column(i);
            rowList[i] = row;
            columnList[i] = column;
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                int[] subGridNum = new int[]{i, j};
                String subGridString = defineSubGridString(subGridNum);
                subGridList.put(subGridString, new SubGrid(subGridNum));
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell addCell = new Cell(i, j, 0, this);
                rowList[i].addCell(addCell);
                columnList[j].addCell(addCell);
                cellList[i][j] = addCell;
                cellSelections.addLast(addCell);
                int[] numberPair = defineSubGridNum(i, j);
                String pairString = defineSubGridString(numberPair);
                subGridList.get(pairString).addCell(addCell);
            }
        }
        this.completionState = new int[]{0, 81};
        this.completionRatio = calculateCompletionRatio();
        this.lockedCellCount = 0;
        if (this.resetRunCount > 0) {
            switch (this.constructorUsed) {
                case 0 -> System.out.println("Base constructor. Empty case.");
                case 1 -> lockedCellInit(this.initialLockedCells);
                case 2 -> boardFromFile(this.fileSource);
                default -> throw new IllegalStateException("Unexpected value: " + this.constructorUsed);
            }
        }
        if (this.resetRunCount > 1) getFrame().repaint();
    }

    /**
     * Get the associated Sudoku game object.
     *
     * @return The Sudoku game object to which this board belongs.
     */
    private Sudoku getGame() {
        return game;
    }

    /**
     * Set the associated Sudoku game object for this board.
     *
     * @param game The Sudoku game object to which this board belongs.
     */
    public void setGame(Sudoku game) {
        this.game = game;
    }

    /**
     * Get the sleep time between steps in the Sudoku-solving process.
     *
     * @return The sleep time in milliseconds.
     */
    public int getSleepTime() {
        return this.sleepTime;
    }

    /**
     * Set the sleep time between steps in the Sudoku-solving process.
     *
     * @param value The sleep time in milliseconds.
     */
    public void setSleepTime(int value) {
        this.sleepTime = value;
    }

    /**
     * Advance the Sudoku-solving process by performing the next solving step in the associated game.
     */
    public void advance() {
        this.game.solveStep();

    }

    /**
     * Get the initial number of locked cells in the Sudoku board.
     *
     * @return The initial number of locked cells.
     */
    public int getInitialLock() {
        return this.initialLockedCells;
    }

    /**
     * Set the initial number of locked cells in the Sudoku board.
     *
     * @param value The initial number of locked cells.
     */
    public void setInitialLock(int value) {
        this.initialLockedCells = value;
    }

    /**
     * Get the type of constructor used to initialize the Sudoku board.
     *
     * @return An integer representing the constructor type (e.g., 0, 1, 2).
     */
    public int getConstructorUsed() {
        return this.constructorUsed;
    }

    /**
     * Set the type of constructor used to initialize the Sudoku board.
     *
     * @param constructorUsed An integer representing the constructor type.
     */
    public void setConstructorUsed(int constructorUsed) {
        this.constructorUsed = constructorUsed;
    }

    /**
     * Get the source file from which the Sudoku board data was loaded.
     *
     * @return The file path or source identifier.
     */
    public String getFileSource() {
        return this.fileSource;
    }

    /**
     * Set the source file or identifier for the Sudoku board data.
     *
     * @param path The file path or source identifier.
     */
    public void setFileSource(String path) {
        this.fileSource = path;
    }
}
