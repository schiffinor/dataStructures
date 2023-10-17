public class ToroidalDoublyLinkedList<E> {

    public int getRowNum() {
        return rowNum;
    }

    public int getColumnNum() {
        return columnNum;
    }

    public int getSize() {
        return size;
    }

    private int rowNum;
    private int columnNum;
    private int size;
    private CircularLinkedList<CircularLinkedList<E>> rowList;
    private CircularLinkedList<CircularLinkedList<E>> columnList;

    public ToroidalDoublyLinkedList(int rows, int columns) {
        this.rowNum = rows;
        this.columnNum = columns;
        this.size = rows * columns;

        this.rowList = new CircularLinkedList<>();
        for (int i = 0; i < rows; i++) {
            CircularLinkedList<E> row = new CircularLinkedList<>();
            rowList.addLast(row);
            for (int j = 0; j < columns; j++) {
                row.addLast(null);
            }
        }

        this.columnList = new CircularLinkedList<>();
        for (int i = 0; i < columns; i++) {
            CircularLinkedList<E> column = new CircularLinkedList<>();
            columnList.addLast(column);
            for (int j = 0; j < rows; j++) {
                column.addLast(null);
            }
        }
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

    public static void main(String[] args) {
        ToroidalDoublyLinkedList<Integer> list = new ToroidalDoublyLinkedList(5,5);
        CircularLinkedList<Integer> list2 = new CircularLinkedList<>();
        for (int i = 5; i < 10; i++) {
            list2.addLast(i);
        }
        System.out.println(list2);
        System.out.println(list);
        list.setData(0, 0, 1);
        list.setData(2, 0, 1);
        list.setRow(1,list2);
        list.setColumn(4,list2);
        System.out.println("setColumn");
        System.out.println(list);
        list.removeRow(2);
        System.out.println("removeRow");
        System.out.println(list);
        list.addRow(2,list2);
        System.out.println("addRow");
        System.out.println(list);
        list.removeColumn(3);
        System.out.println("removeColumn");
        System.out.println(list);
        list.addColumn(3,list2);
        System.out.println("addColumn");
        System.out.println(list);
        System.out.println(list2);

        ToroidalDoublyLinkedList<Integer> list_1 = new ToroidalDoublyLinkedList(4,6);
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
        list_1.setRow(1,list_3);
        list_1.setColumn(4,list_2);
        System.out.println("setColumn");
        System.out.println(list_1);
        list_1.removeRow(2);
        System.out.println("removeRow");
        System.out.println(list_1);
        list_1.addRow(2,list_3);
        System.out.println("addRow");
        System.out.println(list_1);
        list_1.removeColumn(3);
        System.out.println("removeColumn");
        System.out.println(list_1);
        list_1.addColumn(3,list_2);
        System.out.println("addColumn");
        System.out.println(list_1);
        System.out.println(list_2);
        System.out.println(list_3);

    }
}
