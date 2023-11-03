/*
SO once again somewhat sparse documentation here as I didn't end up implementing this fully,
It is honestly tremendously close, and probably only needs at most 3 hours of bug fixing.
There are only one or two bugs, its just that as a result of the complexity of the code,
the size of the data, and the level of recursion the exact source of bugs is difficult to pinpoint.
However, I'm nearly certain it was a sloppy implementation of the constraint cover brought about by sleepy coding.
 */
import java.util.HashMap;

/**
 * The ToroidalDoublyLinkedList class represents a versatile data structure for managing a toroidal doubly linked matrix.
 * This matrix structure is designed to provide efficient manipulation and storage of data in a grid-like format,
 * offering functionalities suitable for various applications, including puzzle solving, data representation, and more.
 * <p>
 * The ToroidalDoublyLinkedList is the core of the Dancing Links algorithm.
 * <p>
 *     The class features methods for setting and retrieving data, inserting and removing rows and columns, as well as
 * maintaining both row and column-wise operations, making it a powerful tool for managing two-dimensional data.
 *
 * @param <E> The type of elements stored in the matrix.
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.1
 * @since 1.1
 */
@SuppressWarnings("FieldCanBeLocal")
public class ToroidalDoublyLinkedList<E> implements Cloneable {

    private final LinkedList<CircularLinkedList<E>> rowIdentifierCache;
    private final LinkedList<CircularLinkedList<E>> columnIdentifierCache;
    private final HashMap<String, CircularLinkedList<E>> rowNameCache;
    private final HashMap<String, CircularLinkedList<E>> columnNameCache;
    public CircularLinkedList<CircularLinkedList<E>> rowList;
    private int rowNum;
    private int columnNum;
    private int size;
    private CircularLinkedList<CircularLinkedList<E>> columnList;

    /**
     * Constructs a new ToroidalDoublyLinkedList with the specified number of rows and columns.
     *
     * @param rows    The number of rows in the matrix.
     * @param columns The number of columns in the matrix.
     */
    public ToroidalDoublyLinkedList(int rows, int columns) {
        this.rowNum = rows;
        this.columnNum = columns;
        this.size = rows * columns;
        this.rowIdentifierCache = new LinkedList<>();
        this.columnIdentifierCache = new LinkedList<>();
        this.rowNameCache = new HashMap<>();
        this.columnNameCache = new HashMap<>();

        this.rowList = new CircularLinkedList<>();
        this.rowList.setParent(this);
        this.rowList.setIdentifier(0);
        this.rowList.setName("rows");
        for (int i = 0; i < rows; i++) {
            final Integer iterCount = i;
            CircularLinkedList<E> row = new CircularLinkedList<>();
            row.setParent(this.rowList);
            row.setIdentifier(iterCount);
            rowIdentifierCache.add(i, row);
            rowList.addLast(row);
            for (int j = 0; j < columns; j++) {
                row.addLast(null);
            }
        }

        this.columnList = new CircularLinkedList<>();
        this.columnList.setParent(this);
        this.columnList.setIdentifier(1);
        this.columnList.setName("columns");
        for (int i = 0; i < columns; i++) {
            CircularLinkedList<E> column = new CircularLinkedList<>();
            column.setParent(this.columnList);
            column.setIdentifier(i);
            columnIdentifierCache.add(i, column);
            columnList.addLast(column);
            for (int j = 0; j < rows; j++) {
                column.addLast(null);
            }
        }
    }

    /**
     * Entry point to demonstrate the functionality of the ToroidalDoublyLinkedList class.
     * <p>
     * Also just tests the functionality of the ToroidalDoublyLinkedList.
     *
     * @param args Command-line arguments (not used).
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        ToroidalDoublyLinkedList<Integer> list = new ToroidalDoublyLinkedList(5, 5);
        CircularLinkedList<Integer> list2 = new CircularLinkedList<>();
        for (int i = 5; i < 10; i++) {
            list2.addLast(i);
        }
        System.out.println(list2);
        System.out.println(list);
        list.setData(0, 0, 1);
        list.setData(2, 0, 1);
        list.setRow(1, list2);
        list.setColumn(4, list2);
        System.out.println("setColumn");
        System.out.println(list);
        list.removeRow(2);
        System.out.println("removeRow");
        System.out.println(list);
        list.addRow(2, list2);
        System.out.println("addRow");
        System.out.println(list);
        list.removeColumn(3);
        System.out.println("removeColumn");
        System.out.println(list);
        list.addColumn(3, list2);
        System.out.println("addColumn");
        System.out.println(list);
        System.out.println(list2);

        ToroidalDoublyLinkedList<Integer> list_1 = new ToroidalDoublyLinkedList(4, 6);
        CircularLinkedList<Integer> list_2 = new CircularLinkedList<>();
        CircularLinkedList<Integer> list_3 = new CircularLinkedList<>();
        for (int i = 6; i < 10; i++) {
            list_2.addLast(i);
        }
        for (int i = 5; i < 11; i++) {
            list_3.addLast(i);
        }
        System.out.println(list_2);
        System.out.println(list_3);
        System.out.println(list_1);
        list_1.setData(0, 0, 1);
        list_1.setData(2, 0, 1);
        System.out.println("pointSets");
        System.out.println(list_1);
        list_1.setRow(1, list_3);
        list_1.setColumn(4, list_2);
        System.out.println("setColumn");
        System.out.println(list_1);
        list_1.removeRow(2);
        System.out.println("removeRow");
        System.out.println(list_1);
        list_1.addRow(2, list_3);
        System.out.println("addRow");
        System.out.println(list_1);
        list_1.removeColumn(3);
        System.out.println("removeColumn");
        System.out.println(list_1);
        list_1.addColumn(3, list_2);
        System.out.println("addColumn");
        System.out.println(list_1);
        System.out.println(list_2);
        System.out.println(list_3);

    }


    /**
     * Generates a string representation of a matrix stored in a CircularLinkedList of CircularLinkedLists.
     *
     * @param input The CircularLinkedList of CircularLinkedLists representing the matrix.
     * @return A string representation of the matrix.
     */
    public static String arrayString(CircularLinkedList<CircularLinkedList<Integer>> input) {
        StringBuilder outString = new StringBuilder();
        for (CircularLinkedList<Integer> row : input) {
            outString.append(row.toString()).append("\n");
        }
        return outString.toString();
    }

    /**
     * Gets the CircularLinkedList of CircularLinkedLists representing the rows in the matrix.
     *
     * @return The CircularLinkedList of CircularLinkedLists representing the rows.
     */
    public CircularLinkedList<CircularLinkedList<E>> getRowList() {
        return rowList;
    }

    /**
     * Sets the CircularLinkedList of CircularLinkedLists representing the rows in the matrix.
     *
     * @param rowList The CircularLinkedList of CircularLinkedLists representing the rows to set.
     */
    public void setRowList(CircularLinkedList<CircularLinkedList<E>> rowList) {
        this.rowList = rowList;
    }

    /**
     * Gets the CircularLinkedList of CircularLinkedLists representing the columns in the matrix.
     *
     * @return The CircularLinkedList of CircularLinkedLists representing the columns.
     */
    public CircularLinkedList<CircularLinkedList<E>> getColumnList() {
        return columnList;
    }

    /**
     * Sets the CircularLinkedList of CircularLinkedLists representing the columns in the matrix.
     *
     * @param columnList The CircularLinkedList of CircularLinkedLists representing the columns to set.
     */
    public void setColumnList(CircularLinkedList<CircularLinkedList<E>> columnList) {
        this.columnList = columnList;
    }

    /**
     * Gets the number of rows in the matrix.
     *
     * @return The number of rows in the matrix.
     */
    public int getRowNum() {
        return rowNum;
    }

    /**
     * Gets the number of columns in the matrix.
     *
     * @return The number of columns in the matrix.
     */
    public int getColumnNum() {
        return columnNum;
    }

    /**
     * Gets the total number of cells (elements) in the matrix.
     *
     * @return The total number of cells in the matrix.
     */
    public int getSize() {
        return size;
    }


    private void setDataRowList(int row, int column, E value) {
        rowList.get(row).set(column, value);
    }


    private void setDataColList(int row, int column, E value) {
        columnList.get(column).set(row, value);
    }

    /**
     * Sets the data at the specified row and column with the given value.
     *
     * @param row    The row index where the data should be set.
     * @param column The column index where the data should be set.
     * @param value  The data value to set.
     */
    public void setData(int row, int column, E value) {
        setDataRowList(row, column, value);
        setDataColList(row, column, value);
    }

    /**
     * Sets the entire row at the specified index with the contents of the provided row source.
     *
     * @param rowIndex    The index of the row to set.
     * @param rowSource   The CircularLinkedList representing the row source.
     * @throws IllegalArgumentException If the size of the row source does not match the matrix's column count.
     */
    public void setRow(int rowIndex, CircularLinkedList<E> rowSource) {
        if (this.columnList.size() != rowSource.size()) {
            throw new IllegalArgumentException("Row size must be equal to original.");
        }

        CircularLinkedList<E> rowTarget = this.rowList.get(rowIndex);

        for (int i = 0; i < rowTarget.size(); i++) {
            rowTarget.set(i, rowSource.get(i));
        }
        int j = 0;
        for (CircularLinkedList<E> c : this.columnList) {
            c.set(rowIndex, rowSource.get(j));
            j++;
        }
    }

    /**
     * Sets the entire column at the specified index with the contents of the provided column source.
     *
     * @param columnIndex The index of the column to set.
     * @param columnSource The CircularLinkedList representing the column source.
     * @throws IllegalArgumentException If the size of the column source does not match the matrix's row count.
     */
    public void setColumn(int columnIndex, CircularLinkedList<E> columnSource) {
        if (this.rowList.size() != columnSource.size()) {
            throw new IllegalArgumentException("Column size must be equal to original.");
        }

        CircularLinkedList<E> columnTarget = this.columnList.get(columnIndex);

        for (int i = 0; i < columnTarget.size(); i++) {
            columnTarget.set(i, columnSource.get(i));
        }
        int j = 0;
        for (CircularLinkedList<E> r : this.rowList) {
            r.set(columnIndex, columnSource.get(j));
            j++;
        }
    }

    /**
     * Removes and returns a row from the matrix at the specified index.
     *
     * @param rowIndex The index of the row to remove.
     * @return The removed row as a CircularLinkedList.
     */
    public CircularLinkedList<E> removeRow(int rowIndex) {

        CircularLinkedList<E> dataLoad = this.rowList.remove(rowIndex);

        for (CircularLinkedList<E> c : this.columnList) {
            c.remove(rowIndex);
        }
        rowNum--;
        size = rowNum * columnNum;
        return dataLoad;
    }

    /**
     * Removes and returns a column from the matrix at the specified index.
     *
     * @param columnIndex The index of the column to remove.
     * @return The removed column as a CircularLinkedList.
     */
    public CircularLinkedList<E> removeColumn(int columnIndex) {

        CircularLinkedList<E> dataLoad = this.columnList.remove(columnIndex);

        for (CircularLinkedList<E> r : this.rowList) {
            r.remove(columnIndex);
        }
        columnNum--;
        size = rowNum * columnNum;
        return dataLoad;
    }

    /**
     * Restores a previously removed row to the matrix using the specified node.
     * <p>
     * Very important part of DLX algorithm.
     *
     * @param node The node representing the row to restore.
     */
    public void unremoveRow(CircularLinkedList.Node<CircularLinkedList<E>> node) {
        assert node.getParent() == this.rowList : ("Not a Row.");
        CircularLinkedList.Node<CircularLinkedList<E>> nextRef = node.getNext();
        CircularLinkedList.Node<CircularLinkedList<E>> prevRef = node.getPrev();
        nextRef.setPrev(node);
        prevRef.setNext(node);
        if (nextRef.getData().getIdentifier() > node.getData().getIdentifier() && nextRef == this.rowList.head) {
            this.rowList.head = node;
        }
        if (prevRef.getData().getIdentifier() < node.getData().getIdentifier() && prevRef == this.rowList.tail) {
            this.rowList.tail = node;
        }
        int rowIndex = rowList.indexFetch(node);

        int i = 0;
        for (CircularLinkedList<E> c : this.columnList) {
            c.add(rowIndex, node.data.get(i));
            i++;
        }
        rowNum++;
        rowList.setSize(rowNum);
        size = rowNum * columnNum;
    }

    /**
     * Adds a row to the matrix at the specified index with the contents of the provided row source.
     *
     * @param rowIndex  The index at which to add the row.
     * @param rowSource The CircularLinkedList representing the row source.
     * @throws IllegalArgumentException If the size of the row source does not match the matrix's column count.
     */
    public void addRow(int rowIndex, CircularLinkedList<E> rowSource) {

        if (this.columnList.size() != rowSource.size()) {
            throw new IllegalArgumentException("Column size must be equal to standard.");
        }

        CircularLinkedList<E> rowObject = rowSource.clone();

        this.rowList.add(rowIndex, rowObject);

        int i = 0;
        for (CircularLinkedList<E> c : this.columnList) {
            c.add(rowIndex, rowObject.get(i));
            i++;
        }
        rowNum++;
        size = rowNum * columnNum;
    }

    /**
     * Adds a column to the matrix at the specified index with the contents of the provided column source.
     *
     * @param columnIndex The index at which to add the column.
     * @param columnSource The CircularLinkedList representing the column source.
     * @throws IllegalArgumentException If the size of the column source does not match the matrix's row count.
     */
    public void addColumn(int columnIndex, CircularLinkedList<E> columnSource) {

        if (this.rowList.size() != columnSource.size()) {
            throw new IllegalArgumentException("Column size must be equal to standard.");
        }

        CircularLinkedList<E> columnObject = columnSource.clone();

        this.columnList.add(columnIndex, columnObject);

        int i = 0;
        for (CircularLinkedList<E> r : this.rowList) {
            r.add(columnIndex, columnObject.get(i));
            i++;
        }
        columnNum++;
        size = rowNum * columnNum;
    }

    /**
     * Gets a specific row from the matrix at the specified index.
     *
     * @param rowIndex The index of the row to retrieve.
     * @return The row as a CircularLinkedList.
     */
    public CircularLinkedList<E> getRow(int rowIndex) {
        return this.rowList.get(rowIndex);
    }

    /**
     * Retrieves the CircularLinkedList of elements from the specified column index.
     *
     * @param columnIndex The index of the column to retrieve.
     * @return The CircularLinkedList containing elements from the specified column.
     */
    public CircularLinkedList<E> getColumn(int columnIndex) {
        return this.columnList.get(columnIndex);
    }

    /**
     * Returns the frequency of a specific value within the row identified by the given index.
     *
     * @param rowIndex The index of the row to search within.
     * @param value   The value to count occurrences of.
     * @return The frequency of the specified value in the given row.
     */
    public int rowFrequency(int rowIndex, E value) {
        return getRow(rowIndex).getFrequency(value);
    }

    /**
     * Returns the frequency of a specific value within the column identified by the given index.
     *
     * @param columnIndex The index of the column to search within.
     * @param value      The value to count occurrences of.
     * @return The frequency of the specified value in the given column.
     */
    public int columnFrequency(int columnIndex, E value) {
        return getColumn(columnIndex).getFrequency(value);
    }

    /**
     * Generates a string representation of the ToroidalDoublyLinkedList as a 2D array.
     * Each row is represented as a line, and elements are separated by spaces.
     *
     * @return A string representing the ToroidalDoublyLinkedList as a 2D array.
     */
    public String arrayString() {
        StringBuilder outString = new StringBuilder();
        for (CircularLinkedList<E> row : this.rowList) {
            outString.append(row.toString()).append("\n");
        }
        return outString.toString();
    }

    /**
     * Returns a string representation of the ToroidalDoublyLinkedList, including both row and column lists.
     *
     * @return A string representation of the class including row and column lists.
     */
    @Override
    public String toString() {
        return "RowList: \n" +
                this.rowList.toString() +
                "\nColumnList: \n" +
                this.columnList.toString();
    }

    /**
     * Creates a shallow clone of the ToroidalDoublyLinkedList, copying the matrix structure, rows, and columns.
     *
     * @return A shallow clone of the ToroidalDoublyLinkedList.
     * @throws InternalError If cloning is not supported.
     */
    @Override
    @SuppressWarnings("unchecked")
    public ToroidalDoublyLinkedList<E> clone() {
        ToroidalDoublyLinkedList<E> clone;
        try {
            clone = (ToroidalDoublyLinkedList<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
        clone.rowNum = getRowNum();
        clone.columnNum = getColumnNum();
        clone.size = getSize();

        clone.rowList = this.rowList;

        clone.columnList = this.columnList;

        return clone;
    }
}
