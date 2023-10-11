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
        if (neighbors.contains(new Cell(true))) {
            neighbors.forEach((Cell cell) -> {
                if (cell.equals(new Cell(true))) {
                    liveNeighbors.getAndIncrement();
                }
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
        //"Cell"+ hashCode()+" : " +
        return ((Integer) Boolean.compare(getAlive(), false)).toString();
    }

    /**
     * Overrides equals method to allow me to create more efficient methods.
     *
     * @return 1 if this Cell is alive, otherwise 0.
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Cell ){
            return ((Boolean) this.getAlive()).equals(((Cell) obj).getAlive());
        } else if (obj instanceof Boolean) {
            return ((Boolean) this.getAlive()).equals(obj);
        } else
            return false;
    }

    /**
     * Overrides clone method to allow me to create more efficient methods.
     *
     * @return 1 if this Cell is alive, otherwise 0.
     */
    @Override
    public Cell clone(){
        return new Cell(getAlive());
    }

}