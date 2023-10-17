import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Board {

    private String fileSource;
    public boolean finished;
    public boolean paused;
    private Row[] rowList;
    private Column[] columnList;
    private HashMap<String,SubGrid> subGridList;
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


    public Board() {
        resetRunCount =0;
        this.constructorUsed = 0;
        this.sleepTime = 100;
        reset();
    }


    public Board(int initialLockedCells) {
        this();
        this.constructorUsed = 1;
        lockedCellInit(initialLockedCells);
    }


    public Board(String fileName) {
        this();
        this.constructorUsed = 2;
        boardFromFile(fileName);
    }


    public void lockedCellInit(int initialLockedCells) {
        this.initialLockedCells = initialLockedCells;
        boolean continueGenerating = true;
        do {
            try {
                LinkedList<Cell> cells = cellSelections.clone();
                LinkedList<Cell> lockedCells = new LinkedList<>();

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

                do {
                    Cell cell = lockedCells.pop();
                    LinkedList<Integer> possibleValues = getPossibleValues(cell);
                    if (possibleValues.isEmpty()) {
                        throw new RuntimeException("NO VALUE FOUND FOR " + cell.toString() + ".");
                    }
                    set(cell.getRowNum(), cell.getColNum(),
                            possibleValues.get(rand.nextInt(possibleValues.size())),true);
                } while (!lockedCells.isEmpty());
                continueGenerating = false;
            } catch (RuntimeException e) {
                System.out.println("Error during board generation. \n If issue persists try less locked Cells.");
                System.out.println(e.toString());
                clearBoard();
            }
        } while (continueGenerating);

    }


    public void clearBoard() {
        int prev = getConstructorUsed();
        setConstructorUsed(0);
        reset();
        setConstructorUsed(prev);
    }



    public int arrayToIndex(String arrayString) {
        int[] array = new int[2];
        array[0] = Integer.getInteger(arrayString.substring(1,1));
        array[1] = Integer.getInteger(arrayString.substring(4,4));
        return arrayToIndex(array);
    }


    public int arrayToIndex(int[] array) {
        return arrayToIndex(array[0], array[1]);
    }


    public int arrayToIndex(int row, int column) {
        return row * 9 + column;
    }



    public LinkedList<Integer> getPossibleValues(int row, int col) {
        return getPossibleValues(getCell(row, col));
    }


    public LinkedList<Integer> getPossibleValues(Cell cell) {
        return cell.getPossibleValues();
    }


    private double calculateCompletionRatio() {
        return Math.divideExact(this.completionState[0], this.completionState[1]);
    }


    public Row getRow(int rowNum) {
        return rowList[rowNum];
    }


    public Column getColumn(int columnNum) {
        return columnList[columnNum];
    }



    public SubGrid getSubGrid(int[] subGridNum) {
        return subGridList.get(defineSubGridString(subGridNum));
    }


    public Cell[][] getCellList() {
        return cellList;
    }



    public int getCols() {
        return columnList.length;
    }
    public int getRows() {
        return rowList.length;
    }
    public Cell get(int row, int col) {
        return getCell(row, col);
    }
    public boolean isLocked(int r, int c) {
        return cellList[r][c].isLocked();
    }

    public void tickLockedCells(boolean state) {
        if (state) this.lockedCellCount += 1;
        else this.lockedCellCount -= 1;
    }
    public int numLocked() {
        return this.lockedCellCount;
    }
    public int value(int row, int col) {
        return cellList[row][col].getValue();
    }
    public void set(int row, int col, int value) {
        cellList[row][col].setValue(value);
    }
    public void set(int row, int col, int value, boolean locked) {
        set(row, col, value);
        cellList[row][col].setLocked(locked);
    }


    public boolean validValue(int row, int col, int value) {

        if (row < 0 || row >= getRows() || col < 0 || col >= getCols()) return false;
        if (value < 0 || value > 9) return false;

        Cell cell = getCell(row, col);

        if (cell.getValue() == value) return true;
        if (cell.row.hasCell(value)) return false;
        if (cell.column.hasCell(value)) return false;
        return !cell.subGrid.hasCell(value);

    }


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


    public Cell getCell(int rowNum, int columnNum) {
        return cellList[rowNum][columnNum];
    }


    public int[] getCompletionState() {
        return completionState;
    }


    public double getCompletionRatio() {
        return completionRatio;
    }


    public String getReadFile() {
        return readFile;
    }


    public void setReadFile(String fileName) {
        this.readFile = fileName;
    }


    public String getWriteFile() {
        return writeFile;
    }


    public void setWriteFile(String fileName) {
        this.writeFile = fileName;
    }



    private void updateInheritedFields() {
        completionRatio = calculateCompletionRatio();
    }


    public static int[][] readFile(String fileName) {
        int[][] valueList = new int[9][9];
        int[][] emptyList = new int[9][9];
        boolean continueOn = true;
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
            int rowCount  = 0;
            try {
                do {
                    currLine = fileGetter.readLine();
                    if (currLine == null || rowCount > 9) break;
                    // assign to an array of Strings the result of splitting the line up by spaces (line.split("[ ]+"))
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(currLine);
                    ArrayList<Integer> matchList = new ArrayList<>();
                    while (matcher.find()) {
                        matchList.add(Integer.valueOf(matcher.group()));
                    }
                    if (matchList.size() == 9) {
                        Integer[] acceptableArray = new Integer[]{0,1,2,3,4,5,6,7,8,9};
                        HashSet<Integer> acceptableSet = new HashSet<>(Arrays.asList(acceptableArray));
                        if (acceptableSet.containsAll(matchList)) {
                            for (int i = 0; i < 9; i++ ) {
                                valueList[rowCount][i] = matchList.get(i);
                            }
                            rowCount++;
                        }
                    } else if (!matchList.isEmpty()) {
                        System.out.println("File not of correct format.");
                        throw new IOException("File not of correct format.");
                    }
                } while (true);
                fileGetter.close();
                return valueList;
            } catch (IOException e) {
                System.out.println("Read Error.");
                System.out.println("Board.read():: error while reading " + fileName);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
            System.out.println("Error occurred, outputting default file.");
        }
        return emptyList;
    }


    public static int[] defineSubGridNum(int rowNum, int columnNum) {
        return new int[]{Math.floorDiv(rowNum,3)-1,Math.floorDiv(columnNum,3)-1};
    }


     public static String defineSubGridString(int[] subGridNum) {
        return Arrays.toString(subGridNum);
    }


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
    }


    @Override
    public String toString() {
        StringBuilder outputBuild = new StringBuilder();
        outputBuild.append("┌───────┬───────┬───────┐ \n");
        for (int i = 0; i <9; i++) {
            outputBuild.append("│ ");
            for (int j = 0; j <9; j++) {
                outputBuild.append(cellList[i][j].toString());
                outputBuild.append(" ");
                if ((j+1)%3 == 0) {
                    outputBuild.append("│ ");
                }
            }
            outputBuild.append("\n");
            if ((i+1)%3 == 0 && i != 8) {
                outputBuild.append("├───────┼───────┼───────┤ \n");
            }
        }
        outputBuild.append("└───────┴───────┴───────┘ \n");
        return outputBuild.toString();
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

    public boolean getPaused() {
        return this.paused;
    }


    public void draw(Graphics g, int scale){
        g.drawRect(32, (int) (53-scale*0.95),getRows()*scale, getCols()*scale);
        g.drawRect(32, (int) (53-scale*0.95),getRows()/3*scale, getCols()/3*scale);
        g.drawRect(32, (int) (53-scale*0.95),getRows()/3*scale*2, getCols()/3*scale*2);
        g.drawRect(32+getRows()/3*scale, (int) (53-scale*0.95+getRows()/3*scale),getRows()/3*scale, getCols()/3*scale);
        g.drawRect(32+getRows()/3*scale, (int) (53-scale*0.95)+getRows()/3*scale,getRows()/3*scale*2, getCols()/3*scale*2);
        g.drawRect(32+getRows()/3*scale*2, (int) (53-scale*0.95+getRows()/3*scale*2),getRows()/3*scale, getCols()/3*scale);
        for(int i = 0; i<getRows(); i++){
            for(int j = 0; j<getCols(); j++){
                get(i, j).draw(g, j*scale+40, i*scale+50, scale);
            }
        } if(finished){
            if(validSolution()){
                g.setColor(new Color(0, 127, 0));
                g.drawChars("Hurray!".toCharArray(), 0, "Hurray!".length(), scale*3+5, scale*10+10);
            } else {
                g.setColor(new Color(127, 0, 0));
                g.drawChars("No solution!".toCharArray(), 0, "No Solution!".length(), scale*3+5, scale*10+10);
            }
        }
    }

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
                subGridList.put(subGridString,new SubGrid(subGridNum));
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell addCell = new Cell(i, j, 0,this);
                rowList[i].addCell(addCell);
                columnList[j].addCell(addCell);
                cellList[i][j] = addCell;
                cellSelections.addLast(addCell);
                int[] numberPair = defineSubGridNum(i, j);
                String pairString = defineSubGridString(numberPair);
                subGridList.get(pairString).addCell(addCell);
            }
        }
        this.completionState = new int[]{0,81};
        this.completionRatio = calculateCompletionRatio();
        this.lockedCellCount = 0;
        if (this.resetRunCount<0) {
            switch (this.constructorUsed) {
                case 0 -> {System.out.println("Base constructor. Empty case.");}
                case 1 -> {lockedCellInit(this.initialLockedCells);}
                case 2 -> {boardFromFile(this.fileSource);}
                default -> throw new IllegalStateException("Unexpected value: " + this.constructorUsed);
            }
        }
    }

    public void setSleepTime(int value) {
        this.sleepTime = value;
    }

    public void play(LandscapeFrame landscapeFrame) {
    }

    public void pause() {
    }

    public void advance() {
    }

    public void setInitialLock(int value) {
        this.initialLockedCells = value;
    }

    public int getInitialLock() {
        return this.initialLockedCells;
    }


    public void setFileSource(String path) {
        this.fileSource = path;
    }

    public void setConstructorUsed(int constructorUsed) {
        this.constructorUsed = constructorUsed;
    }

    public int getConstructorUsed() {
        return this.constructorUsed;
    }

    public String getFileSource() {
        return this.fileSource;
    }
}
