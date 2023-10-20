import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SudokuAlt {

    public Board board;
    private String fileName;
    private ToroidalDoublyLinkedList<Boolean> incidenceMatrix;
    public SudokuAlt() {
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
            if ( (j+1)%9 == 0) {
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

    public static class Solver {

        boolean uncoverState = false;

        private final CircularLinkedList<Integer> valueList;
        private final CircularLinkedList<CircularLinkedList<Integer>> solutionSet;
        private final CircularLinkedList<CircularLinkedList.Node<?>> nullRows;
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

            AtomicInteger index = new AtomicInteger(-1);
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
            AtomicInteger index = new AtomicInteger(-1);
            CircularLinkedList.Node<Integer> node;
            Optional<Integer> val = valueList.stream().filter(x-> x == 1).findFirst();
            val.ifPresent(integer -> {
                index.set(valueList.indexFetch(val.get()));
                System.out.println("val: "+val.get());
            });
            return index.get();
        }


        @SuppressWarnings("unchecked")
        public int cover(int row, int column) {
            int coveredRows = 0;

            CircularLinkedList<Integer> removedRow = incidenceMatrix.removeRow(row);
            solutionSet.addFirst(removedRow);
            CircularLinkedList.LLIterator iterator = (CircularLinkedList.LLIterator) removedRow.listIterator();
            CircularLinkedList<Integer> columnToCover = incidenceMatrix.getColumn(column).clone();
            nullColumns.addFirst(incidenceMatrix.removeColumn(column));
            if (columnToCover.size() == 1) {
              System.out.println("why");
            };


            if (iterator.getCurrent().getData().equals(1)) {
                do {
                    if (columnToCover.size() == 1) {
                        System.out.println("why");
                    };
                    int rowToRemoveIndexValue = columnToCover.indexOf(1);;
                    System.out.println("reached");
                    columnToCover.removeFirstOccurrence(1);
                    nullRows.addFirst(incidenceMatrix.removeRow(rowToRemoveIndexValue).getContainer());
                    System.out.println(nullRows.peek());
                    coveredRows++;
                } while (columnToCover.contains(1));
            }
            while (iterator.hasNext() && iterator.getCurrent().getNext() != iterator.getInitial())
                if (iterator.next().equals(1)) {
                    coveredRows += constraintCover(iterator.previousIndex());
                }


            return coveredRows;
        }


        public int constraintCover(int index) {
            int coverCount = 0;

            CircularLinkedList<Integer> columnToCover = incidenceMatrix.getColumn(index).clone();


            do {
                if (columnToCover.size() == 1) {
                    System.out.println("why");
                };
                int rowToRemoveIndexValue = columnToCover.indexOf(1);
                if (rowToRemoveIndexValue == -1) break;
                System.out.println("reached");
                System.out.println(rowToRemoveIndexValue);
                System.out.println(columnToCover);
                if (columnToCover.size()==1) System.out.println(incidenceMatrix.arrayString());
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


        @SuppressWarnings("rawtypes")
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


        public void algorithmicSolver() {
            int coverCount = 0;

            while (incidenceMatrix.getColumnNum() > 0) {

                int columnToCover = getNextColumnToCover();
                if (columnToCover == -1) {
                    break;
                }
                int solutionRow = nextRow(incidenceMatrix.getColumn(columnToCover));
                if (uncoverState) {
                    uncover(coverCount);
                }
                cover(solutionRow, columnToCover);
            }
            System.out.println("Solution");
        }
    }


}
