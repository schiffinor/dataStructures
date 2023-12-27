/**
 * A* Search algorithm implementation for solving a maze.
 * <p>
 * This class extends {@link AbstractMazeSearch} and implements the A* search algorithm
 * for finding the shortest path in a maze. It uses a priority queue based on the combined
 * cost function, which considers both the distance from the start and an estimate of the
 * remaining distance to the target. This function is based on the Manhattan distance,
 * or taxi-cab metric distance, between cells and is calculated for each cell in the queue
 *
 * <p>
 * The unexplored cells are maintained in a priority queue to ensure that cells with the
 * lowest combined cost are explored first.
 *
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * Maze maze = new Maze(); // Initialize your maze
 * MazeAStarSearch aStarSearch = new MazeAStarSearch(maze);
 * Cell solution = aStarSearch.search();
 * }
 * </pre>
 *
 * @see AbstractMazeSearch
 * @see Heap
 * @see Cell
 * @author Roman Schiffino &lt;rjschi24@colby.edu&gt;
 * @version 1.0
 * @since 1.0
 */
public class MazeAStarSearch extends AbstractMazeSearch {


     // Priority queue for maintaining unexplored cells based on A* search algorithm.
    public final PriorityQueue<Cell> unexplored;

    /**
     * Constructs a MazeAStarSearch instance for the given maze with a custom comparator.
     * <p>
     * This constructor initializes a MazeAStarSearch instance with the provided maze and a
     * custom comparator passed as a lambda function. The comparator is used to determine the
     * priority of cells in the priority queue based on the A* search algorithm. The comparator
     * considers both the distance from the start and the taxi-cab metric distance to the target.
     * <p>
     *
     * @param maze the maze to be solved.
     * @since 1.0
     */
    public MazeAStarSearch(Maze maze) {
        super(maze);
        unexplored = new Heap<>((c1, c2) -> {
            int c1DistFromStart = traceback(c1).size();
            int c2DistFromStart = traceback(c2).size();
            int c1DistToTarget = Math.abs(((getTarget().getRow() - c1.getRow())) + Math.abs(((getTarget().getCol() - c1.getCol()))));
            int c2DistToTarget = Math.abs(((getTarget().getRow() - c2.getRow())) + Math.abs(((getTarget().getCol() - c2.getCol()))));
            return Integer.compare(c1DistFromStart + c1DistToTarget, c2DistFromStart + c2DistToTarget);
        });
    }

    /**
     * Finds the next cell to explore based on A* search priorities.
     *
     * @return the next cell to explore.
     */
    @Override
    public Cell findNextCell() {
        return unexplored.poll();
    }

    /**
     * Adds the specified cell to the unexplored cells priority queue.
     *
     * @param next the cell to be added.
     */
    @Override
    public void addCell(Cell next) {
        unexplored.offer(next);
    }

    /**
     * Returns the number of remaining unexplored cells in the priority queue.
     *
     * @return the number of remaining unexplored cells.
     */
    @Override
    public int numRemainingCells() {
        return unexplored.size();
    }
}
