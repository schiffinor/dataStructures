import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
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
        solver.getNext();
    }

    public static class Solver {

        private CircularLinkedList<Integer> valueList;
        private CircularLinkedList<Integer> solutionSet;
        private ToroidalDoublyLinkedList<Integer> incidenceMatrix;

        public Solver(Board board, ToroidalDoublyLinkedList<Integer> incidenceMatrix) {

            this.incidenceMatrix = incidenceMatrix;
            valueList = new CircularLinkedList<>();
            solutionSet = new CircularLinkedList<>();
            for (int i = 0; i < this.incidenceMatrix.getColumnNum(); i++) {
                Integer frequency = this.incidenceMatrix.columnFrequency(i, 1);
                valueList.addLast(frequency);
            }
            valueList.addLast(1);
        }


        public void getNext() {
            for (int i = 1; i < 10; i++) {
                AtomicInteger index = new AtomicInteger();
                final int finalI = i;
                CircularLinkedList.Node<Integer> node;
                Optional<Integer> val = valueList.stream().filter(x-> x == finalI).findFirst();
                val.ifPresent(integer -> {
                    index.set(valueList.indexOf(integer));
                });
            }

        }

        public void cover(CircularLinkedList.Node<Integer> node) {


        }

        public void uncover() {


        }
    }


}
