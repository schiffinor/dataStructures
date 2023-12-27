import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
public class MazeSearchTest {

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

        // Delete existing data folder and create a new one
        deepDelete(new File("data/" + trialName));
        try {
            Files.createDirectories(Paths.get("data/" + trialName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

        // Toggle to Output GIF. Quite Slow for larger maps with multiple trials.
        boolean gifify = false;

        // Commands for creating GIFs
        LinkedList<String[]> commands = new LinkedList<>();

        //Last created searcher to add gifify shutdown hook to.
        AbstractMazeSearch lastSearcher = null;

        // Initialize count and initial coordinates.
        int count = 0;
        int[] startCoords = {0, 0};
        int[] targetCoords = {1, 1};

        // Iterate through maze searchers
        for (final AbstractMazeSearch mazeSearch : mazeSearchers) {
            lastSearcher = mazeSearch;
            String searchType = searchType(count);
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

            // Create directories for frames
            try {
                Files.createDirectories(Paths.get("data/" + trialName + "/" + searchType + Math.floorDiv(count, 3)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Perform maze search and generate frames
            String folderBase = searchType + Math.floorDiv(count, 3);
            mazeSearch.search(startCell, targetCell, true, 5, true,
                    "data/" + trialName + "/" + folderBase + "/frame");

            // Commands for creating GIFs
            String directory = "data\\" + trialName + "\\" + folderBase;
            String[] cmd = {"cmd.exe", "/C", "magick.exe", "convert", directory + "\\frame*.png", directory + "\\frameSim.gif"};
            commands.addLast(cmd);
            count++;
        }

        // Execute commands to create GIFs after the program finishes
        if (lastSearcher != null && gifify) {
            lastSearcher.getSearchDisplay().getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    for (String[] cmd : commands) {
                        ProcessBuilder execute = new ProcessBuilder();
                        execute.command(cmd);
                        execute.inheritIO();
                        execute.redirectErrorStream(true);
                        File log = new File("Project7.log");
                        execute.redirectOutput(log);
                        Process process = execute.start();
                        try {
                            process.waitFor();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
        }
    }
}

