import java.util.LinkedList;

/**
 * Breadth-First Search algorithm implementation for solving a maze.
 * <p>
 * This class extends {@link AbstractMazeSearch} and implements the Breadth-First Search
 * algorithm for finding the shortest path in a maze. It uses a queue to explore cells
 * in a breadth-first manner, ensuring that cells at the same distance from the start are
 * explored before moving on to cells at a greater distance.
 * <p>
 * The unexplored cells are maintained in a linked list acting as a queue to facilitate
 * the breadth-first exploration strategy.
 * <p>
 *
 * @see AbstractMazeSearch
 * @see LinkedList
 * @see Cell
 * @author Roman Schiffino &lt;rjschi24@colby.edu&gt;
 * @version 1.0
 * @since 1.0
 */
public class MazeBreadthFirstSearch extends AbstractMazeSearch {

    // Queue for maintaining unexplored cells based on A* search algorithm.
    public final LinkedList<Cell> unexplored;

    /**
     * Constructs a MazeBreadthFirstSearch instance for the given maze.
     *
     * @param maze the maze to be solved.
     */
    public MazeBreadthFirstSearch(Maze maze) {
        super(maze);

        // Initialize the queue for unexplored cells
        unexplored = new LinkedList<>();
    }

    /**
     * Finds the next cell to explore based on Breadth-First Search strategy.
     *
     * @return the next cell to explore.
     */
    @Override
    public Cell findNextCell() {
        return unexplored.poll();
    }

    /**
     * Adds the specified cell to the end of the unexplored cells queue.
     *
     * @param next the cell to be added.
     */
    @Override
    public void addCell(Cell next) {
        unexplored.addLast(next);
    }

    /**
     * Returns the number of remaining unexplored cells in the queue.
     *
     * @return the number of remaining unexplored cells.
     */
    @Override
    public int numRemainingCells() {
        return unexplored.size();
    }
}
