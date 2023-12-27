import java.awt.*;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

public abstract class AbstractMazeSearch {
    private final Maze maze;
    public Deque<Cell> unexplored;
    private Cell start;
    private Cell target;
    private Cell cur;
    private MazeSearchDisplay display;

    public AbstractMazeSearch(Maze maze) {
        this.maze = maze;
        this.start = null;
        this.target = null;
        this.cur = null;
        this.display = null;
    }

    public abstract Cell findNextCell();

    public abstract void addCell(Cell next);

    public abstract int numRemainingCells();

    public Maze getMaze() {
        return maze;
    }

    public Cell getTarget() {
        return target;
    }

    public void setTarget(Cell target) {
        this.target = target;
    }

    public Cell getCur() {
        return cur;
    }

    public void setCur(Cell cell) {
        this.cur = cell;
    }

    public MazeSearchDisplay getSearchDisplay() {
        return display;
    }

    public Cell getStart() {
        return start;
    }

    public void setStart(Cell start) {
        this.start = start;
        this.start.setPrev(start);
    }

    public void reset() {
        this.cur = this.start = this.target = null;
    }

    public LinkedList<Cell> traceback(Cell cell) {
        return traceback(cell, false, null, false, null);
    }

    public LinkedList<Cell> traceback(Cell cell, boolean draw, MazeSearchDisplay display, boolean photos, String filename) {
        Cell curCell = cell;
        LinkedList<Cell> path = new LinkedList<>();

        while (curCell != null) {
            path.addFirst(curCell);
            if (curCell.equals(start)) {
                if (draw) {
                    display.repaint();
                    if (photos) {
                        display.saveImage(Objects.requireNonNull(filename) + "Last.png");
                    }
                }
                return path; // we've completed the path from the start to the specified cell
            }
            curCell = curCell.getPrev();
        }
        return null; // we weren't able to find a path, so we return null
    }

    public LinkedList<Cell> search(Cell start, Cell target) {
        return search(start, target, false, 0);
    }

    public LinkedList<Cell> search(Cell start, Cell target, boolean display, int delay) {
        return search(start, target, display, delay, false, null);
    }

    @SuppressWarnings("BusyWait")
    public LinkedList<Cell> search(Cell start, Cell target, boolean display, int delay, boolean photos, String filename) {
        MazeSearchDisplay mazeDisplay = null;
        setStart(start);
        setTarget(target);
        setCur(start);


        addCell(getStart());

        if (display) {
            mazeDisplay = new MazeSearchDisplay(this, Math.floorDiv(700, maze.getRows()));
            this.display = mazeDisplay;
        }
        int count = 0;
        while (numRemainingCells() > 0) {
            count++;
            if (display) {
                try {
                    if (photos) {
                        mazeDisplay.saveImage(Objects.requireNonNull(filename) + count + ".png");
                    }
                    mazeDisplay.repaint();
                    if (delay > 0)
                        Thread.sleep(delay);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            setCur(findNextCell());

            for (Cell neighbor : getMaze().getNeighbors(getCur())) {
                if (neighbor.getPrev() == null) {
                    neighbor.setPrev(cur);
                    addCell(neighbor);
                    if (neighbor.equals(getTarget())) {
                        return traceback(target, display, mazeDisplay, photos, filename); // we found the target, we're done
                    }
                }
            }
        }

        return null; // we couldn't find the target, but we're done
    }

    public void draw(Graphics g, int scale) {
        // Draws the base version of the maze
        getMaze().draw(g, scale);
        // Draws the paths taken by the searcher
        getStart().drawAllPrevs(getMaze(), g, scale, Color.RED);
        // Draws the start cell
        getStart().draw(g, scale, Color.BLUE);
        // Draws the target cell
        getTarget().draw(g, scale, Color.RED);
        // Draws the current cell
        getCur().draw(g, scale, Color.MAGENTA);

        // If the target has been found, draws the path taken by the searcher to reach
        // the target sans backtracking.
        if (getTarget().getPrev() != null) {
            Cell traceBackCur = getTarget().getPrev();
            while (!traceBackCur.equals(getStart())) {
                traceBackCur.draw(g, scale, Color.GREEN);
                traceBackCur = traceBackCur.getPrev();
            }
            getTarget().drawPrevPath(g, scale, Color.BLUE);
        }
    }

}
