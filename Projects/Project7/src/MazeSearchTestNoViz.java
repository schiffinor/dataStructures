import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Handler Class to collect data and run several trials on similar maps.
 *
 * <p>
 * This class utilizes a combination of basic data structures and simple algorithms
 * to fully automate data collection and organization. To use this class you must,
 * the minimum pass in a trial name to use as a storage directory and row and column
 * dimensions. You can then optionally choose to vary, either density or scale.
 * If you choose a variable to vary, you must then specify a lower and upper bound.
 * This interval will be treated as a compact interval, and, as such, infimum and
 * supremum (bounds) are contained. Finally, random start and end can be enabled to
 * place start and target randomly.
 *
 * <p>
 * There is a boolean variable "gifify". Disable gifify to disable automatic gif
 * conversion via Image Magick. Disabled by default, and recommended in most circumstances
 * as gif conversion is very slow. Irrespective of gifification this program stores
 * images of each algorithm step in a directory corresponding to trial name algorithm
 * and step count. {@code /data/trialName/DSF(count)/}
 * <p>
 * As a side note, gifify relies on shutdown hooks so its effectively not cancelable without
 * killing magick in task manager.
 * <p>
 *
 * @author Roman Schiffino &lt;rjschi24@colby.edu&gt;
 * <p>
 * @version 1.0
 * <p>
 * @since 1.0
 */
public class MazeSearchTestNoViz {

    /**
     * Determines the type of search algorithm based on the count value.
     *
     * @param count The count value used to determine the search type.
     * @return A string representing the search type (DFS, BFS, AStar).
     */
    public static String searchType(int count) {
        return switch (count % 3) {
            case 0 -> "DFS";
            case 1 -> "BFS";
            case 2 -> "AStar";
            default -> throw new IllegalStateException("Unexpected value: " + count % 3);
        };
    }

    /**
     * Recursively deletes a directory and its contents.
     *
     * @param toDelete The directory to be deleted.
     */
    static void deepDelete(File toDelete) {
        File[] folderContents = toDelete.listFiles();
        if (folderContents != null) {
            for (File file : folderContents) {
                deepDelete(file);
            }
        }
        toDelete.delete();
    }

    // Print the table
    private static void printTable(List<TableRow> table) {
        System.out.println("| DFS                                                                 | BFS                                                                 | AStar                                                               |");
        System.out.println("| winState | winningPathSize | remainingCells | cellSum | searchRatio | winState | winningPathSize | remainingCells | cellSum | searchRatio | winState | winningPathSize | remainingCells | cellSum | searchRatio |");
        System.out.println("|----------|-----------------|----------------|---------|-------------|----------|-----------------|----------------|---------|-------------|----------|-----------------|----------------|---------|-------------|");
        StringBuilder concat = null;
        for (int i = 0; i < table.size(); i++) {
            TableRow row = table.get(i);
            if (i % 3 == 0 ) {
                if (concat != null) {
                    System.out.println(concat+"\n");
                }
                concat = new StringBuilder("|");
            }
            concat.append(String.format(" %-8b | %-15d | %-14s | %-7f | %-11s |",
                    row.isWinState(), row.getWinningPathSize(), row.getRemainingCells(), row.getCellSum(), row.getSearchRatio()));
        }
    }

    // Write the table to a CSV file
    private static void writeTableToFile(List<TableRow> table, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write header
            writer.write("DFS_winState,DFS_winningPathSize,DFS_remainingCells,DFS_cellSum,DFS_searchRatio," +
                    "BFS_winState,BFS_winningPathSize,BFS_remainingCells,BFS_cellSum,BFS_searchRatio," +
                    "AStar_winState,AStar_winningPathSize,AStar_remainingCells,AStar_cellSum,AStar_searchRatio\n");

            // Write rows
            for (int i = 0; i < table.size(); i += 3) {
                TableRow dfsRow = table.get(i);
                TableRow bfsRow = table.get(i + 1);
                TableRow aStarRow = table.get(i + 2);

                writer.write(String.format("%b,%d,%s,%.2f,%s,",
                        dfsRow.isWinState(), dfsRow.getWinningPathSize(), dfsRow.getRemainingCells(), dfsRow.getCellSum(), dfsRow.getSearchRatio()));

                writer.write(String.format("%b,%d,%s,%.2f,%s,",
                        bfsRow.isWinState(), bfsRow.getWinningPathSize(), bfsRow.getRemainingCells(), bfsRow.getCellSum(), bfsRow.getSearchRatio()));

                writer.write(String.format("%b,%d,%s,%.2f,%s\n",
                        aStarRow.isWinState(), aStarRow.getWinningPathSize(), aStarRow.getRemainingCells(), aStarRow.getCellSum(), aStarRow.getSearchRatio()));
            }

            System.out.println("Table written to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main entry point for the MazeSearchTest program.
     *
     * @param args Command-line arguments. See the Javadoc for usage details.
     */
    public static void main(String[] args) {
        // Check if the required number of command-line arguments is provided
        if (args.length < 3) {
            throw new IllegalArgumentException("You must provide a trial name to use as output folder. \n" +
                    "Run program as follows: MazeSearchTest <String trialName> <int rows> <int columns> " +
                    "?<String variableToVary: density / scale> ?<int/double variableFloor> " +
                    "?<int/double variableCeiling> ?<int steps> ?<boolean randomStart> ?<boolean randomTarget>");
        }

        // Extract command-line arguments
        String trialName = args[0];
        int rows = Integer.parseInt(args[1]);
        int columns = Integer.parseInt(args[2]);


        // Optional arguments
        String variableToVary;
        double variableFloor = 0.0;
        double variableCeiling = 0.0;
        int steps = 0;
        boolean randomStart = false;
        boolean randomTarget = false;
        Boolean densityOrScale = null;

        // Validate and process optional arguments
        if (3 < args.length && args.length < 7) {
            throw new IllegalArgumentException("If variable to vary is stated, floor ceiling and step count must also be stated.");
        }
        if (6 < args.length) {
            variableToVary = args[3];
            if (variableToVary.equals("density")) {
                densityOrScale = true;
                variableFloor = Double.parseDouble(args[4]);
                variableCeiling = Double.parseDouble(args[5]);
            } else if (variableToVary.equals("scale")) {
                densityOrScale = false;
                variableFloor = Integer.parseInt(args[4]);
                variableCeiling = Integer.parseInt(args[5]);
            } else {
                throw new IllegalArgumentException("Variable to vary must either be density or scale.");
            }
            steps = Integer.parseInt(args[6]);
        }

        if (7 < args.length) randomStart = Boolean.parseBoolean(args[7]);

        if (8 < args.length) randomTarget = Boolean.parseBoolean(args[8]);

        if (9 < args.length) throw new IllegalArgumentException("Too many Arguments");


        // Initialize random and data structures for maze searchers
        Random random = new Random();
        LinkedList<AbstractMazeSearch> mazeSearchers = new LinkedList<>();
        HashMap<String, Maze> DFSMazes = new HashMap<>();
        HashMap<String, Maze> BFSMazes = new HashMap<>();
        HashMap<String, Maze> AStarMazes = new HashMap<>();

        // Generate mazes based on specified parameters
        if (densityOrScale == null) {
            Maze maze = new Maze(rows, columns, .3);
            DFSMazes.put("maze0", maze.clone());
            BFSMazes.put("maze0", maze.clone());
            AStarMazes.put("maze0", maze.clone());
        } else if (densityOrScale) {
            for (int i = 0; i <= steps; i++) {
                Maze tempMaze = new Maze(rows, columns, variableFloor + i * (variableCeiling - variableFloor) / steps);
                DFSMazes.put("maze" + i, tempMaze.clone());
                BFSMazes.put("maze" + i, tempMaze.clone());
                AStarMazes.put("maze" + i, tempMaze.clone());
            }
        } else {
            for (int i = 0; i <= steps; i++) {
                double variableStep = variableFloor + i * (variableCeiling - variableFloor) / steps;
                int stepRows = (int) Math.floor(((double) rows) * variableStep);
                int stepCols = (int) Math.floor(((double) columns) * variableStep);
                Maze tempMaze = new Maze(stepRows, stepCols, 0.3);
                DFSMazes.put("maze" + i, tempMaze.clone());
                BFSMazes.put("maze" + i, tempMaze.clone());
                AStarMazes.put("maze" + i, tempMaze.clone());
            }
        }

        // Add maze searchers to the list
        for (int i = 0; i < DFSMazes.size(); i++) {
            mazeSearchers.addLast(new MazeDepthFirstSearch(DFSMazes.get("maze" + i)));
            mazeSearchers.addLast(new MazeBreadthFirstSearch(BFSMazes.get("maze" + i)));
            mazeSearchers.addLast(new MazeAStarSearch(AStarMazes.get("maze" + i)));
        }

        // Initialize count and initial coordinates.
        int count = 0;
        int[] startCoords = {0, 0};
        int[] targetCoords = {1, 1};


        // Create a list to store the rows of the table
        List<TableRow> table = new ArrayList<>();
        // Iterate through maze searchers
        for (final AbstractMazeSearch mazeSearch : mazeSearchers) {
            int rowCount = mazeSearch.getMaze().getRows();
            int colCount = mazeSearch.getMaze().getCols();

            // Set up maze start and target cells
            // Coords are picked once every three iterations to ensure random coordinate mode maintains
            // coordinates per maze.
            if (count % 3 == 0) {
                startCoords = (randomStart) ?
                        new int[]{random.nextInt(0, rowCount), random.nextInt(0, colCount)} :
                        new int[]{0, 0};
                targetCoords = (randomTarget) ?
                        new int[]{random.nextInt(0, rowCount), random.nextInt(0, colCount)} :
                        new int[]{rowCount - 1, colCount - 1};
            }
            Cell startCell = mazeSearch.getMaze().get(startCoords[0], startCoords[1]);
            startCell.setType(CellType.FREE);

            Cell targetCell = mazeSearch.getMaze().get(targetCoords[0], targetCoords[1]);
            startCell.setType(CellType.FREE);
            targetCell.setType(CellType.FREE);


            // Perform maze search and generate frames
            LinkedList<Cell> outputPath =  mazeSearch.search(startCell, targetCell);
            boolean winState = outputPath != null && (outputPath.contains(targetCell));
            int winningPathSize = (winState) ? outputPath.size() : 0;
            Double remainingCells = (winState) ? (double) mazeSearch.numRemainingCells() : null;
            double cellSum = 0;
            if (winState) {
                for (Cell cell: mazeSearch.getMaze()) {
                    if (cell.getType() == CellType.FREE) cellSum++;
                }
            }
            double checkedCells = 0;
            if (winState) {
                for (Cell cell: mazeSearch.getMaze()) {
                    if (cell.getPrev() != null && cell.getType() == CellType.FREE) checkedCells++;
                }
            }
            Double searchRatio = (winState) ? checkedCells / cellSum : null;
            count++;

            // Create a TableRow object and add it to the list
            TableRow row = new TableRow(winState, winningPathSize, remainingCells, cellSum, searchRatio);
            table.add(row);
        }
        printTable(table);
        try {
            Files.createDirectories(Paths.get("data/" + trialName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String fileName = "output.csv";
        while (Files.exists(Path.of("data/" + trialName,fileName))) {
            fileName = fileName.split("\\.")[0]+"(1).csv";
        }
        writeTableToFile(table, "data/" + trialName+"/"+fileName);
    }

    // Define TableRow class to represent a row in the table
    static class TableRow {
        private boolean winState;
        private int winningPathSize;
        private Double remainingCells;
        private double cellSum;
        private Double searchRatio;

        public TableRow(boolean winState, int winningPathSize, Double remainingCells, double cellSum, Double searchRatio) {
            this.winState = winState;
            this.winningPathSize = winningPathSize;
            this.remainingCells = remainingCells;
            this.cellSum = cellSum;
            this.searchRatio = searchRatio;
        }

        public boolean isWinState() {
            return winState;
        }

        public int getWinningPathSize() {
            return winningPathSize;
        }

        public Double getRemainingCells() {
            return remainingCells;
        }

        public double getCellSum() {
            return cellSum;
        }

        public Double getSearchRatio() {
            return searchRatio;
        }
    }
}

