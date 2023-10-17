public class Sudoku {

    public Board board;
    public Sudoku() {
        this.board = new Board(25);
    }

    public ToroidalDoublyLinkedList<Boolean> generateIncidenceMatrix() {
        ToroidalDoublyLinkedList<Boolean> outputMatrix;
        int valuePositions = (int) Math.pow(board.getRows(),3);
        int constraints = (int) Math.pow(board.getRows(),2);
        outputMatrix = new ToroidalDoublyLinkedList<>(valuePositions,4*constraints);

        /*for (int i = 0; i < valuePositions; i++) {
            for (int j = 0; j < 4*constraints; j++) {
                outputMatrix.setData(i,j,false);
            }
        }*/

        CircularLinkedList<Boolean> valueList1 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList1.addLast(i<9);
        }
        for (int j = 0; j < constraints; j++) {
            outputMatrix.setColumn(j,valueList1);
            for (int i = 0; i < 9; i++) {
                valueList1.addFirst(valueList1.removeLast());
            }
        }


        CircularLinkedList<Boolean> valueList2 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList2.addLast(i%9==0 && i<81);
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

        CircularLinkedList<Boolean> valueList3 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList3.addLast(i%81==0);
        }
        for (int j = 0; j < constraints; j++) {
            outputMatrix.setColumn(j+2*constraints,valueList3);
            valueList3.addFirst(valueList3.removeLast());
        }

        CircularLinkedList<Boolean> valueList4 = new CircularLinkedList<>();
        for (int i = 0; i < valuePositions; i++) {
            valueList4.addLast(i % 9 == 0 && i<243 && i%81 < 27);
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
            System.out.println(outputMatrix.getColumn(j + 3 * constraints));
        }
        return outputMatrix;
    }

    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku();
        ToroidalDoublyLinkedList<Boolean> outputMatrix = sudoku.generateIncidenceMatrix();
        System.out.print(outputMatrix.getColumnNum()+" "+outputMatrix.getRowNum());
        //System.out.print(outputMatrix);
    }

}
