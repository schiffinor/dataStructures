import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Create a class called Landscape. It serves the same purpose as the Landscape in the Game of Life simulation,
 * but is different enough that you probably don't want to copy the old one and edit it. Start with a new file.
 * The Landscape will need fields to store its width and height (as ints) and a LinkedList of Agents.
 * Use your implementation of a linked list.
 */
public class Landscape implements Cloneable{
    public Landscape currentGame;
    public LinkedList<Agent> agentList;
    private Random rand;
    // Keeps a history of game states
    public LinkedList<Landscape> stateList;
    //Stores previous state, not yet implemented.
    public HashMap<String,LinkedList<Landscape>> previousGame;
    public HashMap<String,LinkedList<Agent>> sectorMap;
    public boolean paused;
    public int width;
    public int height;
    public int sectorSize;


    /**
     * a constructor that sets the width and height fields, and initializes the agent list.
     * @param w
     * @param h
     */
    public Landscape(int w, int h) {
        this.rand = new Random();
        this.width = w;
        this.height = h;
        this.sectorSize = 10;
        this.agentList = new LinkedList<>();
        this.stateList = new LinkedList<>();
        this.previousGame = new HashMap<>();
        this.paused = true;
        reset();
    }

    /**
     * a constructor that sets the width and height fields, and initializes the agent list.
     *
     */
    public Landscape() {
        //TODO
    }

    /**
     * Recreates the Landscape according to the specifications given in its initial construction.
     */
    public void reset() {
        //TODO
        sectorMap = createSectorMap(this.width, this.height, this.sectorSize);
        stateList.clear();
        stateList.addLast(currentGame);
        previousGame.put("previousGame",stateList);
    }

    public HashMap<String,LinkedList<Agent>> createSectorMap(int width, int height, int refinement) {
        HashMap<String,LinkedList<Agent>> output = new HashMap<>();
        if ((width % refinement != 0 || height % refinement != 0)) {
            throw new ArithmeticException(width+", "+height+" are not both divisible by "+refinement);
        }
        else {
            for (int i = 0; i < height/refinement; i++) {
                for (int j = 0; j < width/refinement; j++) {
                    int[] identifier = new int[2];
                    identifier[0] = i;
                    identifier[1] = j;
                    output.put(Arrays.toString(identifier), new LinkedList<>());
                }
            }
        }
        return output;
    }


    public int[] getSector(Agent agent) {
        return getSector((int) agent.getX(),(int) agent.getY());
    }
    public int[] getSector(int xCor, int yCor) {
        int[] output = new int[2];
        output[0] = ((yCor - (yCor % this.sectorSize))/(this.sectorSize));
        output[1] = ((xCor - (xCor % this.sectorSize))/(this.sectorSize));
        return output;
    }

    /**
     * returns the height.
     * @return
     */
    public int getHeight() {
        return this.height;
    }
    public int getWidth() {
        return this.width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getSectorSize() {
        return this.sectorSize;
    }

    public void setSectorSize(int size) {
        this.sectorSize = size;
    }

    /**
     * inserts an agent at the beginning of its list of agents.
     * @param agent
     */
    public void addAgent( Agent agent) {
        this.agentList.addFirst(agent);
        this.sectorMap.get(Arrays.toString(getSector(agent))).addFirst(agent);
    }

    /**
     * returns a String representing the Landscape.
     * It can be as simple as indicating the number of Agents on the Landscape.
     * @return
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.agentList.size());
        builder.append(" Agents: ");
        builder.append(agentList.toString());
        return builder.toString();
    }

    /**
     * returns a list of the Agents within radius distance of the location x0, y0.
     * @param x0
     * @param y0
     * @param radius
     * @return
     */
    public LinkedList<Agent> getNeighbors(double x0, double y0, double radius) {

        return new LinkedList<>();
    }

    public void advance() {
        //TODO
        /*
        // Create a snapshot of the current landscape
        Landscape landscapeFreeze = new Landscape();

        // Copy the current state to the snapshot
        int cR = 0;
        for (Cell[] row : landscapeFreeze) {
            int cC = 0;
            for (Cell column: row) {
                landscapeFreeze[cR][cC] = (landscape[cR][cC]).clone();
                cC++;
            }
            cR++;
        }

        // Add the snapshot to the state history
        stateList.addLast(landscapeFreeze);

        // Get a list of living cells in the current landscape
        ArrayList<Cell> livingCells = new ArrayList<>();
        HashMap<Cell,ArrayList<Cell>> neighborMap = new HashMap<>();
        for (Cell[] row : landscape) {
            List<Cell> tempRow = Arrays.asList(row);
            HashSet<Cell> rowSet = new HashSet<>(tempRow);

            // Remove dead cells from the row
            rowSet.removeIf(cell -> cell.equals(new Cell(false)));

            // Add the living cells to the list. One of the potential nulls in question.
            if (!rowSet.isEmpty()) {
                livingCells.addAll(rowSet);
            }

        }

        // Calculate neighbors for each living cell and store them in neighborMap
        for (Cell cell : livingCells) {
            Integer[] identity = cellHashMap.get(cell);
            neighborMap.put(cell, getNeighbors(identity));
        }
        // Update the state of each living cell based on its neighbors
        for (Cell cell : livingCells) {
            ArrayList<Cell> reference = neighborMap.get(cell);
            //Second null value.
            if (reference == null) {
                reference = new ArrayList<>();
            }
            cell.updateState(reference);
        }
        // Check if the landscape has returned to a previous state, and pause the simulation if so
        if (this.equals(stateList.getLast())) {
            pause();
        }
         */
    }

    /**
     * Reverts the current Landscape to the previous state stored in the state history.
     * This method undoes the last step of the simulation by restoring the landscape
     * to its previous state.
     */
    public void revert() {
        //TODO
        /*
        // Retrieve the previous state from the state history
        Cell[][] landscapeFreeze = stateList.pollLast();

        // Restore the landscape to the previous state
        int cR = 0;
        if (landscapeFreeze != null) {
            for (Cell[] row : landscapeFreeze) {
                int cC = 0;
                for (Cell column: row) {
                    landscape[cR][cC].setAlive(column.getAlive());
                    cC++;
                }
                cR++;
            }
        }
         */
    }


    /**
     * Pauses the Game of Life simulation.
     * Sets the 'paused' flag to true and displays a message.
     */
    public void pause() {
        paused = true;
        System.out.println("paused");
    }

    public void setPause(boolean bool) {
        paused = bool;
        System.out.println("paused");
    }

    /**
     * Getter for paused state.
     * @return boolean state of pause variable.
     */
    public boolean getPaused() {
        return paused;
    }


    /**
     * Resumes the Game of Life simulation.
     * Sets the 'paused' flag to false, advances the simulation, and updates the window.
     *
     * @param window the JFrame used for displaying the simulation
     */
    public void play(LandscapeFrame window) {
        paused = false;
        advance();
        SwingUtilities.invokeLater(window::repaint);
        if (!paused) {
            SwingUtilities.invokeLater(() -> play(window));
        }

    }

    /**
     * Calls the draw method of all the agents on the Landscape.
     * @param g
     */
    public void draw(Graphics g) {
        draw(g,1);
    }

    public void draw(Graphics g, int scale) {
        for (Agent agent : agentList) {
            agent.draw(g,scale);
        }
    }
    @Override
    public Landscape clone() throws CloneNotSupportedException {
        Landscape clone = (Landscape) super.clone();
        clone.currentGame = this.currentGame.clone();
        clone.agentList = new LinkedList<>(this.agentList);
        clone.stateList = new LinkedList<>(this.stateList);
        clone.setPause(true);
        clone.width = this.width;
        clone.height = this.height;
        return clone;
    }

    public static void main(String[] args) {
        Landscape scape = new Landscape(500, 500);
        Random gen = new Random();

        // Creates 100 SocialAgents and 100 AntiSocialAgents
        for (int i = 0; i < 100; i++) {
            scape.addAgent(new SocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(),
                    25));
            scape.addAgent(new AntiSocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(),
                    50));
        }
        LandscapeFrame display = new LandscapeFrame(scape,1);
        System.out.println(scape);
    }
}
