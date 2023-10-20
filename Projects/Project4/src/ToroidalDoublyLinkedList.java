import java.util.HashMap;

public class ToroidalDoublyLinkedList<E> implements Cloneable{

    private int rowNum;
    private int columnNum;
    private int size;
    private CircularLinkedList<CircularLinkedList<E>> rowList;
    private CircularLinkedList<CircularLinkedList<E>> columnList;
    private LinkedList<CircularLinkedList<E>> rowIdentifierCache;
    private LinkedList<CircularLinkedList<E>> columnIdentifierCache;
    private HashMap<String,CircularLinkedList<E>> rowNameCache;
    private HashMap<String,CircularLinkedList<E>> columnNameCache;

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
            rowIdentifierCache.add(i,row);
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
            columnIdentifierCache.add(i,column);
            columnList.addLast(column);
            for (int j = 0; j < rows; j++) {
                column.addLast(null);
            }
        }
    }

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

    public static String arrayString(CircularLinkedList<CircularLinkedList<Integer>> input) {
        StringBuilder outString = new StringBuilder();
        for (CircularLinkedList<Integer> row : input) {
            outString.append(row.toString()).append("\n");
        }
        return outString.toString();
    }

    public CircularLinkedList<CircularLinkedList<E>> getRowList() {
        return rowList;
    }

    public void setRowList(CircularLinkedList<CircularLinkedList<E>> rowList) {
        this.rowList = rowList;
    }

    public CircularLinkedList<CircularLinkedList<E>> getColumnList() {
        return columnList;
    }

    public void setColumnList(CircularLinkedList<CircularLinkedList<E>> columnList) {
        this.columnList = columnList;
    }

    public int getRowNum() {
        return rowNum;
    }

    public int getColumnNum() {
        return columnNum;
    }

    public int getSize() {
        return size;
    }

    private void setDataRowList(int row, int column, E value) {
        rowList.get(row).set(column, value);
    }

    private void setDataColList(int row, int column, E value) {
        columnList.get(column).set(row, value);
    }

    public void setData(int row, int column, E value) {
        setDataRowList(row, column, value);
        setDataColList(row, column, value);
    }

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

    public CircularLinkedList<E> removeRow(int rowIndex) {

        CircularLinkedList<E> dataLoad = this.rowList.remove(rowIndex);

        for (CircularLinkedList<E> c : this.columnList) {
            c.remove(rowIndex);
        }
        rowNum--;
        size = rowNum * columnNum;
        return dataLoad;
    }

    public CircularLinkedList<E> removeColumn(int columnIndex) {

        CircularLinkedList<E> dataLoad = this.columnList.remove(columnIndex);

        for (CircularLinkedList<E> r : this.rowList) {
            r.remove(columnIndex);
        }
        columnNum--;
        size = rowNum * columnNum;
        return dataLoad;
    }

    public void unremoveRow(CircularLinkedList.Node<CircularLinkedList<E>> node) {
        assert node.getParent() == this.rowList : ("Not a Row.");
        CircularLinkedList.Node<CircularLinkedList<E>> nextRef = node.getNext();
        CircularLinkedList.Node<CircularLinkedList<E>> prevRef = node.getPrev();
        nextRef.setPrev(node);
        prevRef.setNext(node);
        int rowIndex = rowList.indexFetch(node);

        int i = 0;
        for (CircularLinkedList<E> c : this.columnList) {
            c.add(rowIndex, node.data.get(i));
            i++;
        }
        rowNum++;
        size = rowNum * columnNum;
    }

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

    public CircularLinkedList<E> getRow(int rowIndex) {
        return this.rowList.get(rowIndex);
    }

    public CircularLinkedList<E> getColumn(int columnIndex) {
        return this.columnList.get(columnIndex);
    }

    public int rowFrequency(int rowIndex, E value) {
        return getRow(rowIndex).getFrequency(value);
    }

    public int columnFrequency(int columnIndex, E value) {
        return getColumn(columnIndex).getFrequency(value);
    }

    public String arrayString() {
        StringBuilder outString = new StringBuilder();
        for (CircularLinkedList<E> row : this.rowList) {
            outString.append(row.toString()).append("\n");
        }
        return outString.toString();
    }

    @Override
    public String toString() {
        return "RowList: \n" +
                this.rowList.toString() +
                "\nColumnList: \n" +
                this.columnList.toString();
    }

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

        clone.rowList = this.rowList.clone();

        clone.columnList = this.columnList.clone();

        return clone;
    }
}
