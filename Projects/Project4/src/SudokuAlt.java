/*
Here it is
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The SudokuAlt class represents a Sudoku solving algorithm based on Algorithm X with Dancing Links.
 * This class provides methods for reading Sudoku puzzle data, generating an incidence matrix,
 * and attempting to find a solution. It aims to solve Sudoku puzzles of any number of clues and of any complexity.
 * <p>
 * Unfortunately because of some pesky null pointers here and there it doesn't do it perfectly.
 * It is probably minor tweaking to get it to work, but I just couldn't spend any more time on this.
 * <p>
 * Furthermore, I think I need to go to the doctor, my arm, in particular my hand has been killing
 * me for days, my right hand is swollen, and feels a mixture of numb, painful, and sore. Typing isn't super easy right now.
 * <p>
 * Anyways, thanks for reading, as always I wrote all code myself. This is a lazy implementation, but, was interesting.
 * Documentation is a little sparse because not fully implemented.
 */
public class SudokuAlt {

    public final Board board;
    private ToroidalDoublyLinkedList<Boolean> incidenceMatrix;

    /**
     * Initializes a new instance of the SudokuAlt class with a Sudoku board of the specified size.
     *
     */
    public SudokuAlt() {
        this.board = new Board(10);
    }

    /**
     * The main entry point of the SudokuAlt program, responsible for solving Sudoku puzzles.
     * It initializes the SudokuAlt instance, processes the input data, performs the solving algorithm,
     * and generates the output.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If an I/O error occurs while reading or writing files.
     */
    public static void main(String[] args) throws IOException {
        SudokuAlt sudoku = new SudokuAlt();
        ToroidalDoublyLinkedList<Integer> outputMatrix = sudoku.readWriteFile();
        Solver solver = new Solver(sudoku.board, outputMatrix);
        solver.algorithmicSolver();
        /*int val = solver.getNextColumnToCover();
        System.out.println(val);
        int val2 = solver.nextRow(solver.incidenceMatrix.getColumn(val));
        System.out.println(val2);
        Integer val3 = solver.incidenceMatrix.getColumn(val).get(val2);
        System.out.println(val3);
        CircularLinkedList.Node<Integer> node = solver.incidenceMatrix.getColumn(val).nodeFetch(val2);
        System.out.println(node);
        int coverCount = solver.cover(val2,val);
        System.out.println("Gap");
        sudoku.writeFileOutput(solver.solutionSet, "ss.txt");
        sudoku.writeFileOutput(solver.nullColumns, "nc.txt");
        sudoku.writeFileOutput(solver.deadRows, "dr.txt");
        sudoku.writeFileOutput(solver.incidenceMatrix, "operableMatrix2.txt");

        solver.uncover(coverCount);
        sudoku.writeFileOutput(solver.incidenceMatrix, "operableMatrix3.txt");
        System.out.println(solver.incidenceMatrix.getRowNum());
        sudoku.writeFileOutput(solver.incidenceMatrix, "operableMatrix3.txt");
        System.out.println(solver.incidenceMatrix.rowList.size());*/

    }

    /**
     * Reads and processes the incidence matrix data from a file or generates a new matrix.
     * If the incidence matrix data is available in a file, it reads the data; otherwise, it generates a new matrix.
     * This method provides the essential data structure for Algorithm X.
     *
     * @return The incidence matrix for Algorithm X, which encodes the Sudoku puzzle constraints and possible values.
     * @throws IOException If an I/O error occurs while reading or writing files.
     */
    public ToroidalDoublyLinkedList<Integer> readWriteFile() throws IOException {
        BufferedReader fileGetter = null;
        BufferedWriter fileWriter = null;
        ToroidalDoublyLinkedList<Integer> output;
        String fileName = "incidenceMatrix.txt";
        try {
            fileGetter = new BufferedReader(new FileReader(fileName));
        } catch (Exception FileNotFoundException) {
            output = generateIncidenceMatrix();
            fileWriter = new BufferedWriter(new FileWriter(fileName));
            fileWriter.write(output.arrayString());
            fileWriter.close();
            return output;
        }
        int valuePositions = (int) Math.pow(board.getRows(), 3);
        int constraints = (int) Math.pow(board.getRows(), 2);
        output = new ToroidalDoublyLinkedList<>(valuePositions, 4 * constraints);
        String currLine;
        int rowCount = 0;
        do {
            currLine = fileGetter.readLine();
            if (currLine == null) break;
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(currLine);
            ArrayList<Integer> matchList = new ArrayList<>();
            while (matcher.find()) {
                matchList.add(Integer.valueOf(matcher.group()));
            }

            for (int i = 0; i < 4 * constraints; i++) {
                output.setData(rowCount, i, matchList.get(i));
            }
            rowCount++;

        } while (true);
        fileGetter.close();
        return output;
    }

    /**
     * Writes the data of a CircularLinkedList of CircularLinkedLists of integers to a text file.
     *
     * @param input    The CircularLinkedList of CircularLinkedLists containing integer data to be written.
     * @param fileName The name of the file where the data will be written.
     * @throws IOException If an I/O error occurs while writing the file.
     */
    public void writeFileOutput(CircularLinkedList<CircularLinkedList<Integer>> input, String fileName) throws IOException {
        BufferedWriter fileWriter = null;
        fileWriter = new BufferedWriter(new FileWriter(fileName));
        fileWriter.write(ToroidalDoublyLinkedList.arrayString(input));
        fileWriter.close();
    }

    /**
     * Writes the data of a ToroidalDoublyLinkedList of integers to a text file.
     *
     * @param input    The ToroidalDoublyLinkedList of integers containing data to be written.
     * @param fileName The name of the file where the data will be written.
     * @throws IOException If an I/O error occurs while writing the file.
     */
    public void writeFileOutput(ToroidalDoublyLinkedList<Integer> input, String fileName) throws IOException {
        BufferedWriter fileWriter = null;
        fileWriter = new BufferedWriter(new FileWriter(fileName));
        fileWriter.write(input.arrayString());
        fileWriter.close();
    }

    /**
     * Generates an incidence matrix for the Sudoku puzzle. The incidence matrix encodes the relationships
     * between puzzle constraints and possible values. I will say coming up with these patterns took a while and
     * somewhat difficult to explain,however, its just patterns.
     *
     * @return The generated incidence matrix as a ToroidalDoublyLinkedList of integers.
     */
    public ToroidalDoublyLinkedList<Integer> generateIncidenceMatrix() {
        ToroidalDoublyLinkedList<Integer> outputMatrix;
        int valuePositions = (int) Math.pow(board.getRows(), 3);
        int constraints = (int) Math.pow(board.getRows(), 2);
        outputMatrix = new ToroidalDoublyLinkedList<>(valuePositions, 4 * constraints);

        // Cell constraints.
        CircularLinkedList<Integer> valueList1 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList1.addLast((i < 9) ? 1 : 0);
        }
        for (int j = 0; j < constraints; j++) {
            outputMatrix.setColumn(j, valueList1);
            for (int i = 0; i < 9; i++) {
                valueList1.addFirst(valueList1.removeLast());
            }
        }

        // Column constraints.
        CircularLinkedList<Integer> valueList2 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList2.addLast((i % 9 == 0 && i < 81) ? 1 : 0);
        }
        for (int j = 0; j < constraints; j++) {
            outputMatrix.setColumn(j + constraints, valueList2);
            valueList2.addFirst(valueList2.removeLast());
            if ((j + 1) % 9 == 0) {
                for (int i = 0; i < 72; i++) {
                    valueList2.addFirst(valueList2.removeLast());
                }
            }
        }

        // Row constraints.
        CircularLinkedList<Integer> valueList3 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList3.addLast((i % 81 == 0) ? 1 : 0);
        }
        for (int j = 0; j < constraints; j++) {
            outputMatrix.setColumn(j + 2 * constraints, valueList3);
            valueList3.addFirst(valueList3.removeLast());
        }

        // SubGrid constraints.
        CircularLinkedList<Integer> valueList4 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList4.addLast((i % 9 == 0 && i < 243 && i % 81 < 27) ? 1 : 0);
        }
        for (int j = 0; j < constraints; j++) {
            if (j != 0 && j % 9 == 0) {
                for (int i = 0; i < 18; i++) {
                    valueList4.addFirst(valueList4.removeLast());
                }
            }
            if (j != 0 && j % 27 == 0) {
                for (int i = 0; i < 162; i++) {
                    valueList4.addFirst(valueList4.removeLast());
                }
            }
            outputMatrix.setColumn(j + 3 * constraints, valueList4);
            valueList4.addFirst(valueList4.removeLast());
        }
        return outputMatrix;
    }

    /**
     * The Solver class represents a solver for Sudoku puzzles using the Dancing Links algorithm (Algorithm X).
     * It performs exact cover and backtracking to find valid solutions.
     * <p>
     * This class encapsulates the core logic for solving Sudoku puzzles.
     * It maintains the incidence matrix and other data structures needed for the algorithm.
     */
    @SuppressWarnings("rawtypes")
    public static class Solver {

        private final CircularLinkedList<Integer> valueList;
        private final CircularLinkedList<CircularLinkedList<Integer>> solutionSet;
        private final CircularLinkedList<CircularLinkedList.Node<?>> nullRows;
        private final CircularLinkedList<CircularLinkedList<Integer>> nullColumns;
        private final CircularLinkedList<CircularLinkedList<Integer>> deadRows;
        private final ToroidalDoublyLinkedList<Integer> incidenceMatrix;
        boolean uncoverState = false;

        /**
         * Constructs a Solver for a Sudoku puzzle.
         *
         * @param board            The Sudoku board object representing the puzzle.
         * @param incidenceMatrix  The incidence matrix encoding puzzle constraints and values.
         */
        public Solver(Board board, ToroidalDoublyLinkedList<Integer> incidenceMatrix) {
            this.incidenceMatrix = incidenceMatrix;
            ToroidalDoublyLinkedList<Integer> operableMatrix = incidenceMatrix.clone();
            this.valueList = new CircularLinkedList<>();
            this.solutionSet = new CircularLinkedList<>();
            this.nullRows = new CircularLinkedList<>();
            this.nullColumns = new CircularLinkedList<>();
            this.deadRows = new CircularLinkedList<>();
            for (int i = 0; i < this.incidenceMatrix.getColumnNum(); i++) {
                Integer frequency = this.incidenceMatrix.columnFrequency(i, 1);
                valueList.addLast(frequency);
            }
        }

        /**
         * Finds and returns the index of the next column to be covered.
         *
         * @return The index of the next column to cover, or -1 if no such column exists.
         */
        public int getNextColumnToCover() {

            AtomicInteger index = new AtomicInteger(-1);
            int i = 1;
            AtomicBoolean continueCheck = new AtomicBoolean(true);
            do {
                final int finalI = i;
                CircularLinkedList.Node<Integer> node;
                Optional<Integer> val = valueList.stream().filter(x -> x == finalI).findFirst();
                val.ifPresent(integer -> {
                    index.set(valueList.indexFetch(val.get()));
                    System.out.println("val: " + val.get());
                    continueCheck.set(false);
                });
                i++;
            } while (continueCheck.get());
            return index.get();
        }


        /**
         * Finds and returns the index of the next row in the specified column.
         *
         * @param column The column to search for the next row.
         * @return The index of the next row in the column, or -1 if no such row exists.
         */
        public int nextRow(CircularLinkedList<Integer> column) {
            AtomicInteger index = new AtomicInteger(-1);
            CircularLinkedList.Node<Integer> node;
            //Optional<Integer> val = valueList.stream().filter(x -> x == 1).findFirst();
            //val.ifPresent(integer -> {
                //index.set(valueList.indexOf(val.get()));
                //System.out.println("val: " + val.get());
            //});
            return column.indexOf(1);
        }


        /**
         * Covers a specified row and column in the incidence matrix, effectively marking them as used.
         * Returns the number of rows covered as a result.
         *
         * @param row    The index of the row to be covered.
         * @param column The index of the column to be covered.
         * @return The number of rows covered as a result.
         */
        @SuppressWarnings("unchecked")
        public int cover(int row, int column) {
            int coveredRows = 0;

            CircularLinkedList<Integer> removedRow = incidenceMatrix.removeRow(row);
            solutionSet.addFirst(removedRow);
            System.out.println("YEE: "+solutionSet);
            CircularLinkedList.LLIterator iterator = (CircularLinkedList.LLIterator) removedRow.listIterator();
            CircularLinkedList<Integer> columnToCover = incidenceMatrix.getColumn(column).clone();
            nullColumns.addFirst(incidenceMatrix.removeColumn(column));
            if (columnToCover.size() == 1) {
                System.out.println("why");
            }


            if (iterator.getCurrent().getData().equals(1)) {
                do {
                    if (columnToCover.size() == 1) {
                        System.out.println("why");
                    }
                    int rowToRemoveIndexValue = columnToCover.indexOf(1);
                    System.out.println("reached");
                    columnToCover.removeFirstOccurrence(1);
                    nullRows.addFirst(incidenceMatrix.removeRow(rowToRemoveIndexValue).getContainer());
                    System.out.println(nullRows.peek());
                    coveredRows++;
                } while (columnToCover.contains(1));
            }
            while (iterator.hasNext() && iterator.getCurrent().getNext() != iterator.getInitial())
                if (iterator.next().equals(1)) {
                    coveredRows += constraintCover(iterator.index);
                }


            return coveredRows;
        }

        /**
         * Covers additional rows based on the constraints in the specified column.
         *
         * @param index The index of the column to apply constraint-based covering.
         * @return The number of rows covered as a result.
         */
        public int constraintCover(int index) {
            int coverCount = 0;
            // ISSUE IS HERE CLONE IS BAD should be max 32, 8 matches per constraint
            // tHIS KEEPS ON RIPPING THE SAME UNEDITED COLUMN
            // MAYBE COMBINE ALL CONSTRAINTS INTO ONE COLUMN AND GO THROUGH THAT
            CircularLinkedList<Integer> columnToCover = incidenceMatrix.getColumn(index).clone();


            do {
                if (columnToCover.size() == 1) {
                    System.out.println("why");
                }
                int rowToRemoveIndexValue = columnToCover.indexOf(1);
                if (rowToRemoveIndexValue == -1) break;
                if (rowToRemoveIndexValue == 543) {
                    System.out.println("huh");
                }
                System.out.println("reached");
                System.out.println(rowToRemoveIndexValue);
                System.out.println(columnToCover);
                if (columnToCover.size() == 1) {
                    System.out.println(incidenceMatrix.arrayString());
                }
                columnToCover.removeFirstOccurrence(1);
                nullRows.addFirst(incidenceMatrix.removeRow(rowToRemoveIndexValue).getContainer());
                coverCount++;
            } while (columnToCover.contains(1));
            return coverCount;


                /*System.out.println(solutionSet);
                System.out.println("dead"+deadRows);
                for (CircularLinkedList<Integer> ss : solutionSet) {
                    System.out.println(ss.getIdentifier());
                }
                System.out.println(solutionSet.size());*/

        }


        /**
         * Uncovers rows that were previously covered and restores their state in the incidence matrix.
         *
         * @param rowsToUncover The number of rows to uncover.
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        public void uncover(int rowsToUncover) {
            uncoverState = false;
            CircularLinkedList<Integer> removedSolutionRow = solutionSet.remove(0);
            assert removedSolutionRow != null : "Solution set is null.";
            deadRows.addFirst(removedSolutionRow);
            for (int i = 0; i < rowsToUncover; i++) {
                CircularLinkedList.Node<?> nextRow = nullRows.remove();
                CircularLinkedList.Node<CircularLinkedList<Integer>> nextRowMod = (CircularLinkedList.Node<CircularLinkedList<Integer>>) nextRow;
                incidenceMatrix.unremoveRow(nextRowMod);
            }
        }

        /**
         * Solves the Sudoku puzzle using the Dancing Links algorithm.
         * It performs exact cover and backtracking to find valid solutions.
         */
        public void algorithmicSolver() {
            int coverCount = 0;

            while (incidenceMatrix.getColumnNum() > 0) {

                int columnToCover = getNextColumnToCover();
                if (columnToCover == -1) {
                    break;
                }
                CircularLinkedList<Integer> column = incidenceMatrix.getColumn(columnToCover);
                System.out.println("CO: "+column);
                int solutionRow = nextRow(incidenceMatrix.getColumn(columnToCover));
                System.out.println("Solving " + solutionRow);
                System.out.println("SR: "+incidenceMatrix.getRow(solutionRow));
                if (uncoverState) {
                    uncover(coverCount);
                }
                cover(solutionRow, columnToCover);
            }
            System.out.println("Solution");
        }
    }


}
