import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sudoku {

    public Board board;
    private String fileName;
    private ToroidalDoublyLinkedList<Boolean> incidenceMatrix;
    public Sudoku() {
        this.board = new Board(25);
    }


    /**
     * Constructor for AI Object.
     * <p>
     * Instantiates most variables and extracts data from text
     * files, creates files if not present.
     * <p>
     * All data is placed into nested hash maps. Which can
     * then be accessed via unique identifiers.
     *
     */
    public ToroidalDoublyLinkedList<Integer> readWriteFile() throws IOException {
        BufferedReader fileGetter = null;
        BufferedWriter fileWriter = null;
        ToroidalDoublyLinkedList<Integer> output;
        this.fileName = "incidenceMatrix.txt";
            try {
                fileGetter = new BufferedReader(new FileReader(fileName));
            } catch (Exception FileNotFoundException) {
                output = generateIncidenceMatrix();
                fileWriter = new BufferedWriter(new FileWriter(fileName)) ;
                fileWriter.write(output.arrayString());
                fileWriter.close();
                return output;
            }
        int valuePositions = (int) Math.pow(board.getRows(),3);
        int constraints = (int) Math.pow(board.getRows(),2);
        output = new ToroidalDoublyLinkedList<>(valuePositions,4*constraints);
        String currLine;
        int rowCount  = 0;
        do {
            currLine = fileGetter.readLine();
            if (currLine == null) break;
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(currLine);
            ArrayList<Integer> matchList = new ArrayList<>();
            while (matcher.find()) {
                matchList.add(Integer.valueOf(matcher.group()));
            }

            for (int i = 0; i < 4*constraints; i++ ) {
                output.setData(rowCount,i,matchList.get(i));
            }
            rowCount++;

        } while (true);
        fileGetter.close();
        return output;
    }


    public  void writeFileOutput(CircularLinkedList<CircularLinkedList<Integer>> input, String fileName) throws IOException {
        BufferedWriter fileWriter = null;
        fileWriter = new BufferedWriter(new FileWriter(fileName)) ;
        fileWriter.write(ToroidalDoublyLinkedList.arrayString(input));
        fileWriter.close();
    }


    public void writeFileOutput(ToroidalDoublyLinkedList<Integer> input, String fileName) throws IOException {
        BufferedWriter fileWriter = null;
        fileWriter = new BufferedWriter(new FileWriter(fileName)) ;
        fileWriter.write(input.arrayString());
        fileWriter.close();
    }


    public ToroidalDoublyLinkedList<Integer> generateIncidenceMatrix() {
        ToroidalDoublyLinkedList<Integer> outputMatrix;
        int valuePositions = (int) Math.pow(board.getRows(),3);
        int constraints = (int) Math.pow(board.getRows(),2);
        outputMatrix = new ToroidalDoublyLinkedList<>(valuePositions,4*constraints);


        CircularLinkedList<Integer> valueList1 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList1.addLast((i<9) ? 1 : 0);
        }
        for (int j = 0; j < constraints; j++) {
            outputMatrix.setColumn(j,valueList1);
            for (int i = 0; i < 9; i++) {
                valueList1.addFirst(valueList1.removeLast());
            }
        }


        CircularLinkedList<Integer> valueList2 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList2.addLast((i%9==0 && i<81) ? 1 : 0);
        }
        for (int j = 0; j < constraints; j++) {
            outputMatrix.setColumn(j+constraints,valueList2);
            valueList2.addFirst(valueList2.removeLast());
            if (j != 0 && j%9 == 0) {
                for (int i = 0; i < 72; i++) {
                    valueList2.addFirst(valueList2.removeLast());
                }
            }
        }

        CircularLinkedList<Integer> valueList3 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList3.addLast((i%81==0) ? 1 : 0);
        }
        for (int j = 0; j < constraints; j++) {
            outputMatrix.setColumn(j+2*constraints,valueList3);
            valueList3.addFirst(valueList3.removeLast());
        }

        CircularLinkedList<Integer> valueList4 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList4.addLast((i % 9 == 0 && i<243 && i%81 < 27) ? 1 : 0);
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

    public static void main(String[] args) throws IOException {
        Sudoku sudoku = new Sudoku();
        ToroidalDoublyLinkedList<Integer> outputMatrix = sudoku.readWriteFile();
        Solver solver = new Solver(sudoku.board, outputMatrix);
        int val = solver.getNextColumnToCover();
        System.out.println(val);
        int val2 = solver.nextRow(solver.operableMatrix.getColumn(val));
        System.out.println(val2);
        Integer val3 = solver.operableMatrix.getColumn(val).get(val2);
        System.out.println(val3);
        CircularLinkedList.Node<Integer> node = solver.operableMatrix.getColumn(val).nodeFetch(val2);
        System.out.println(node);
        solver.cover(node);
        System.out.println("Gap");
        sudoku.writeFileOutput(solver.solutionSet, "ss.txt");
        sudoku.writeFileOutput(solver.nullColumns, "nc.txt");
        sudoku.writeFileOutput(solver.nullRows, "nr.txt");
        sudoku.writeFileOutput(solver.deadRows, "dr.txt");
        sudoku.writeFileOutput(solver.operableMatrix, "operableMatrix2.txt");

        solver.uncover();
        sudoku.writeFileOutput(solver.operableMatrix, "operableMatrix3.txt");
    }

    public static class Solver {

        private final CircularLinkedList<Integer> valueList;
        private final CircularLinkedList<CircularLinkedList<Integer>> solutionSet;
        private final CircularLinkedList<CircularLinkedList<Integer>> nullRows;
        private final CircularLinkedList<CircularLinkedList<Integer>> nullColumns;
        private final CircularLinkedList<CircularLinkedList<Integer>> deadRows;
        private final ToroidalDoublyLinkedList<Integer> incidenceMatrix;
        private final ToroidalDoublyLinkedList<Integer> operableMatrix;

        public Solver(Board board, ToroidalDoublyLinkedList<Integer> incidenceMatrix) {
            this.incidenceMatrix = incidenceMatrix;
            this.operableMatrix = incidenceMatrix.clone();
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


        public int getNextColumnToCover() {
            AtomicInteger index = new AtomicInteger();
            int i = 1;
            AtomicBoolean continueCheck = new AtomicBoolean(true);
            do {
                final int finalI = i;
                CircularLinkedList.Node<Integer> node;
                Optional<Integer> val = valueList.stream().filter(x-> x == finalI).findFirst();
                val.ifPresent(integer -> {
                    index.set(valueList.indexFetch(val.get()));
                    System.out.println("val: "+val.get());
                    continueCheck.set(false);
                });
                i++;
            } while (continueCheck.get());
            return index.get();
        }


        public int nextRow(CircularLinkedList<Integer> column) {
            AtomicInteger index = new AtomicInteger();
            CircularLinkedList.Node<Integer> node;
            Optional<Integer> val = valueList.stream().filter(x-> x == 1).findFirst();
            val.ifPresent(integer -> {
                index.set(valueList.indexFetch(val.get()));
                System.out.println("val: "+val.get());
            });
            return index.get();
        }


        @SuppressWarnings("unchecked")
        public void cover(CircularLinkedList.Node<Integer> node) {
            int rowIndex = node.getParent().indexFetch(node);
            int columnIndex = ((CircularLinkedList<CircularLinkedList<Integer>>) node.getParent().getParent()).indexFetch(node.getParent());
            solutionSet.addFirst(operableMatrix.removeRow(rowIndex));
            CircularLinkedList<Integer> columnToCover = operableMatrix.getColumn(columnIndex).clone();
            nullColumns.addFirst(operableMatrix.removeColumn(columnIndex));
            do {
                int rowToRemoveIndexValue = columnToCover.indexOf(1);;
                System.out.println("reached");
                columnToCover.removeFirstOccurrence(1);
                nullRows.addFirst(operableMatrix.removeRow(rowToRemoveIndexValue));
                System.out.println(nullRows.peek());
            } while (columnToCover.contains(1));
        }

        public void uncover() {
            CircularLinkedList<Integer> removedSolutionRow = solutionSet.remove(0);
            assert removedSolutionRow != null : "Solution set is null.";
            deadRows.addFirst(removedSolutionRow);
            CircularLinkedList<Integer> nextRow = nullRows.remove();
            int rowIndex = incidenceMatrix.getRowList().indexFetch(nextRow);
            operableMatrix.addRow(rowIndex,nextRow);
        }
    }


}
