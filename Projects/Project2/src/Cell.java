import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Cell {

    /**
     * The status of the Cell.
     */
    private boolean alive;

    /**
     * Constructs a dead cell.
     */
    public Cell() {
        alive = false;
    }

    /**
     * Constructs a cell with the specified status.
     * 
     * @param status a boolean to specify if the Cell is initially alive
     */
    public Cell(boolean status) {
        alive = status;
    }

    /**
     * Returns whether the cell is currently alive.
     * 
     * @return whether the cell is currently alive
     */
    public boolean getAlive() {
        return alive;
    }

    /**
     * Sets the current status of the cell to the specified status.
     * 
     * @param status a boolean to specify if the Cell is alive or dead
     */
    public void setAlive(boolean status) {
        alive = status;
    }

    /**
     * Updates the state of the Cell.
     * <p>
     * If this Cell is alive and if there are 2 or 3 alive neighbors,
     * this Cell stays alive. Otherwise, it dies.
     * <p>
     * If this Cell is dead and there are 3 alive neighbors,
     * this Cell comes back to life. Otherwise, it stays dead.
     * 
     * @param neighbors An ArrayList of Cells
     */
    public void updateState(ArrayList<Cell> neighbors) {
        AtomicInteger liveNeighbors = new AtomicInteger();
        if (neighbors.contains(true)) {
            neighbors.forEach((Cell cell) -> {
                if (cell.equals(true)) {
                    liveNeighbors.getAndIncrement();
                };
            }
            );
            if (alive) {
                if (liveNeighbors.intValue() < 2) {
                    setAlive(false);
                }
                else if (liveNeighbors.intValue() >= 4) {
                    setAlive(false);
                }
            }
            else {
                if (liveNeighbors.intValue() == 3) {
                    setAlive(true);
                }
            }
        }
        else {
            setAlive(false);
        }
    }

    /**
     * Returns a String representation of this Cell.
     * 
     * @return 1 if this Cell is alive, otherwise 0.
     */
    public String toString() {
        return ((Integer) Boolean.compare(getAlive(), false)).toString();
    }

    /**
     * Overrides equals method to allow me to create more efficient methods.
     *
     * @return 1 if this Cell is alive, otherwise 0.
     */
    @Override
    public boolean equals(Object cell){
        if(cell instanceof Cell){
            Cell checkCell = (Cell) cell;
            return ((Boolean) this.getAlive()).equals(checkCell.getAlive());
        } else
            return false;
    }

}