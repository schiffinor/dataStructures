import java.util.*;

/**
 * Depth-First Search algorithm implementation for solving a maze.
 * <p>
 * This class extends {@link AbstractMazeSearch} and implements the Depth-First Search
 * algorithm for finding a path in a maze. It uses a stack (implemented as a linked list)
 * to explore cells in a depth-first manner, going as far as possible along each branch
 * before backtracking.
 * <p>
 * The unexplored cells are maintained in a linked list acting as a stack to facilitate
 * the depth-first exploration strategy.
 * <p>

 * @author Roman Schiffino &lt;rjschi24@colby.edu&gt;
 * @version 1.0
 * @since 1.0
 */
public class MazeDepthFirstSearch extends AbstractMazeSearch {

    // Stack for maintaining unexplored cells based on A* search algorithm.
    public final LinkedList<Cell> unexplored;

    /**
     * Constructs a MazeDepthFirstSearch instance for the given maze.
     *
     * @param maze the maze to be solved.
     */
    public MazeDepthFirstSearch(Maze maze) {
        super(maze);

        // Initialize the stack for unexplored cells
        unexplored = new LinkedList<>();
    }

    /**
     * Finds the next cell to explore based on Depth-First Search strategy.
     *
     * @return the next cell to explore.
     */
    @Override
    public Cell findNextCell() {
        return unexplored.poll();
    }

    /**
     * Adds the specified cell to the beginning of the unexplored cells stack.
     *
     * @param next the cell to be added.
     */
    @Override
    public void addCell(Cell next) {
        unexplored.addFirst(next);
    }

    /**
     * Returns the number of remaining unexplored cells in the stack.
     *
     * @return the number of remaining unexplored cells.
     */
    @Override
    public int numRemainingCells() {
        return unexplored.size();
    }
}
