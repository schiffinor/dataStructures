import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * Create a class called Landscape. It serves the same purpose as the Landscape in the Game of Life simulation,
 * but is different enough that you probably don't want to copy the old one and edit it. Start with a new file.
 * The Landscape will need fields to store its width and height (as ints) and a LinkedList of Agents.
 * Use your implementation of a linked list.
 */

/**
 * The `Landscape` class represents a simulation landscape for agents.
 * It contains fields to store various properties, such as width, height, and agent information.
 * This class is used to manage agents on the landscape, update their state, and control the simulation.
 *
 * The landscape is divided into sectors for efficient neighbor search and interaction between agents.
 *
 * @see Agent
 * @see SocialAgent
 * @see AntiSocialAgent
 * @see LandscapeFrame
 */
public class Landscape implements Cloneable{

    // Fields to store agents and landscape properties
    private final Random rand;
    public LinkedList<Agent> agentList;
    public HashMap<String,LinkedList<Landscape>> previousGame;
    public HashMap<String,LinkedList<Agent>> sectorMap;
    public boolean paused;
    public int width;
    public int height;
    public int sleepTime;
    public int sectorSize;
    public int agentCount;
    private int antiSocialRadius;
    private int socialRadius;


    /**
     * Constructs a new landscape with the specified width and height.
     *
     * @param w The width of the landscape.
     * @param h The height of the landscape.
     */
    public Landscape(int w, int h) {
        this.rand = new Random();
        this.width = w;
        this.height = h;
        this.paused = true;
        this.sleepTime = 250;
        this.agentCount = 100;
        this.socialRadius = 25;
        this.antiSocialRadius = 25;
        reset();
    }

    public static void main(String[] args) {
        Landscape scape = new Landscape(500, 500);
        Random gen = new Random();

        // Creates 100 SocialAgents and 100 AntiSocialAgents
        for (int i = 0; i < scape.agentCount; i++) {
            scape.addAgent(new SocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(),
                    5));
            scape.addAgent(new AntiSocialAgent(gen.nextDouble() * scape.getWidth(),
                    gen.nextDouble() * scape.getHeight(),
                    11));
        }
        LandscapeFrame display = new LandscapeFrame(scape,1);
        System.out.println(scape);
        System.out.println(scape.sectorMap);
    }

    /**
     * Resets the landscape to its initial state based on the specified agent count and radii.
     */
    public void reset() {
        this.sectorSize = 10;
        this.agentList = new LinkedList<>();
        this.previousGame = new HashMap<>();
        sectorMap = createSectorMap(this.width, this.height, this.sectorSize);
        for (int i = 0; i < agentCount; i++) {
            addAgent(new SocialAgent(rand.nextDouble() * getWidth(),
                    rand.nextDouble() * getHeight(), socialRadius));
            addAgent(new AntiSocialAgent(rand.nextDouble() * getWidth(),
                    rand.nextDouble() * getHeight(), antiSocialRadius));
        }

    }

    /**
     * Creates a sector map based on the specified width, height, and refinement size.
     *
     * @param width      The width of the landscape.
     * @param height     The height of the landscape.
     * @param refinement The sector refinement size.
     * @return A map of sectors with empty lists of agents.
     */
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

    /**
     * Returns the sector to which an agent belongs based on its position.
     *
     * @param agent The agent whose sector is to be determined.
     * @return An array of two integers representing the sector.
     */
    public int[] getSector(Agent agent) {
        return getSector(agent.getX(), agent.getY());
    }

    /**
     * Returns the sector to which a coordinate pair belongs.
     *
     * @param xCor The x-coordinate of the pair whose sector is to be determined.
     * @param yCor The y-coordinate of the pair whose sector is to be determined.
     * @return An array of two integers representing the sector.
     */
    public int[] getSector(double xCor, double yCor) {
        int[] output = new int[2];
        output[0] = (int) ((yCor - (yCor % this.sectorSize))/(this.sectorSize));
        output[1] = (int) ((xCor - (xCor % this.sectorSize))/(this.sectorSize));
        return output;
    }

    /**
     * Returns the height of the landscape.
     *
     * @return The height of the landscape.
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Sets the height of the landscape.
     *
     * @param height The new height to set.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns the width of the landscape.
     *
     * @return The width of the landscape.
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Sets the width of the landscape.
     *
     * @param width The new width to set.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Returns the sector size.
     *
     * @return The sector size.
     */
    public int getSectorSize() {
        return this.sectorSize;
    }

    /**
     * Sets the sector size.
     *
     * @param size The new sector size to set.
     */
    public void setSectorSize(int size) {
        this.sectorSize = size;
    }

    /**
     * Inserts an agent at the beginning of the agent list.
     *
     * @param agent The agent to add to the landscape.
     */
    public void addAgent( Agent agent) {
        this.agentList.addFirst(agent);
        this.sectorMap.get(Arrays.toString(getSector(agent))).addFirst(agent);
    }

    /**
     * Returns a string representation of the landscape, indicating the number of agents on the landscape.
     *
     * @return A string representation of the landscape.
     */
    public String toString() {
        return this.agentList.size() + " Agents: " + agentList;
    }

    /**
     * Returns a list of agents within a specified radius of a location (x0, y0).
     *
     * @param x0     The x-coordinate of the location.
     * @param y0     The y-coordinate of the location.
     * @param radius The radius for neighbor search.
     * @return A list of agents within the specified radius.
     */
    public LinkedList<Agent> getNeighbors(double x0, double y0, double radius) {
        ArrayList<String> sectorTracker = new ArrayList<>();
        int[] refSector = getSector(x0, y0);
        sectorTracker.add(Arrays.toString(refSector));
        //Constructor for boolean-array of directions to check for neighbors.
        if (radius <= this.sectorSize) {
            //Check whether ref-sector in the scope of array.
            if (sectorMap.get(Arrays.toString(refSector)) == null) {
                System.out.println("Point not within bounds of game. Neighbors DNE.");
            }
            else {
                boolean[] checkDirections = new boolean[4];
                //Up.
                checkDirections[0] = ((y0 % this.sectorSize) < ((double) this.sectorSize / 2));
                //Right.
                checkDirections[1] = ((x0 % this.sectorSize) >= ((double) this.sectorSize / 2));
                //Down.
                checkDirections[2] = ((y0 % this.sectorSize) >= ((double) this.sectorSize / 2));
                //Left.
                checkDirections[3] = ((x0 % this.sectorSize) < ((double) this.sectorSize / 2));
                //Get allotted sectors
                for (int i = 0; i < 4; i++) {
                    if (checkDirections[i]) {
                        //Handy function to indicate directions.
                        int sectorRow = (int) (refSector[0] - (Math.cos((Math.PI / 2) * i)));
                        int sectorColumn = (int) (refSector[1] - (Math.sin((Math.PI / 2) * i)));
                        String adjacentSector = "["+sectorRow+", "+sectorColumn+"]";
                        if (sectorMap.containsKey(adjacentSector)) {
                            sectorTracker.add(adjacentSector);
                        }
                        if (checkDirections[i] && checkDirections[(i + 1) % 4]) {
                            int sectorRowD = (int) (refSector[0] - (Math.cos((Math.PI / 2) * i)));
                            int sectorColumnD = (int) (refSector[1] - (Math.sin((Math.PI / 2) * ((i + 1) % 4))));
                            String diagSector = "["+sectorRowD+", "+sectorColumnD+"]";
                            if (sectorMap.containsKey(diagSector)) {
                                sectorTracker.add(diagSector);
                            }
                        }
                    }
                }
            }
        }
        else {
            int intRadius = (int) Math.floor(radius / (double) this.sectorSize);
            for (int i = (refSector[0]-intRadius); i < (refSector[0])+intRadius ; i++) {
                for (int j = (refSector[1]-intRadius); j < (refSector[1]+intRadius); j++) {
                    String addSector = "[" + i + ", " + j + "]";
                    if (sectorMap.containsKey(addSector)) {
                        sectorTracker.add(addSector);
                    }
                }
            }
        }
        LinkedList<Agent> combinedSectorMaster = new LinkedList<>();
        for (String sector : sectorTracker) {
            combinedSectorMaster.addAll(sectorMap.get(sector));
        }
        LinkedList<Agent> neighbors = new LinkedList<>();
        for (Agent agent : combinedSectorMaster) {
            if (Math.sqrt(Math.pow((agent.getX()-x0),2)+Math.pow((agent.getY()-y0),2)) <= radius) {
                neighbors.add(agent);
            }
        }
        return neighbors;
    }

    /**
     * Updates the state of all agents on the landscape.
     * This method iterates through the list of agents and calls their respective updateState methods,
     * allowing agents to interact or move based on the simulation rules.
     */
    public void updateAgents() {
        for (Agent agent : this.agentList) {
            agent.updateState(this);
        }

    }

    /**
     * Advances the simulation by updating agent states.
     * After updating agent states, it checks if any agent has moved.
     * If no agent has moved, the simulation is paused.
     */
    public void advance() {
        // Create a snapshot of the current landscape
        updateAgents();
        ArrayList<Boolean> moveList = new ArrayList<>();
        for (Agent agent : this.agentList) {
            moveList.add(agent.moved);
        }
        if (!moveList.contains(true)) {
            pause();
        }
    }

    /**
     * Pauses the Game of Life simulation.
     * Sets the 'paused' flag tco true and displays a message.
     */
    public void pause() {
        paused = true;
        System.out.println("paused");
    }

    /**
     * Pauses or unpauses the simulation based on the provided boolean value.
     *
     * @param bool `true` to pause the simulation, `false` to unpause it.
     */
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
     * Resumes the Agent simulation.
     * Sets the 'paused' flag to false, advances the simulation, and updates the window.
     *
     * @param window the JFrame used for displaying the simulation
     */
    public void play(LandscapeFrame window) {
        advance();
        SwingUtilities.invokeLater(window::repaint);
        try {
            Thread.sleep(this.sleepTime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * Draws the agents on the landscape with a specified scaling factor.
     *
     * @param g The graphics context to draw on.
     * @param scale The scaling factor for agent rendering.
     */
    public void draw(Graphics g, int scale) {
        for (Agent agent : agentList) {
            agent.draw(g,scale);
        }
    }

    /**
     * Creates a deep clone of the current landscape.
     * This method duplicates the landscape, including its agents and sector map, and returns the cloned landscape.
     * The clone is initially paused.
     *
     * @return A deep clone of the landscape.
     * @throws CloneNotSupportedException If the cloning process encounters an issue.
     */
    @Override
    public Landscape clone() throws CloneNotSupportedException {
        Landscape clone = (Landscape) super.clone();
        clone.agentList = new LinkedList<>(this.agentList);
        clone.sectorMap = new HashMap<>(this.sectorMap);
        for (String agentLinkedListKey : this.sectorMap.keySet()) {
            clone.sectorMap.put(agentLinkedListKey, new LinkedList<>(sectorMap.get(agentLinkedListKey)));
        }
        clone.setPause(true);
        clone.width = this.width;
        clone.height = this.height;
        clone.sleepTime = this.sleepTime;
        return clone;
    }

    /**
     * Copies the contents of another landscape to this landscape. This method overwrites the current landscape's data with the data from the source landscape. The copied landscape will be initially paused.
     *
     * @param source The source landscape to copy from.
     */
    public void copy(Landscape source) {
        this.agentList = new LinkedList<>(source.agentList);
        this.sectorMap = new HashMap<>(source.sectorMap);
        for (String agentLinkedListKey : source.sectorMap.keySet()) {
            this.sectorMap.put(agentLinkedListKey, new LinkedList<>(source.sectorMap.get(agentLinkedListKey)));
        }
        this.setPause(true);
        this.width = source.width;
        this.height = source.height;
    }

    public void setSleepTime(int value) {
        this.sleepTime = value;
    }

    public void setAgents(int value) {
        this.agentCount = value;
    }

    public int getSocialRadius() {
        return this.socialRadius;
    }

    public void setSocialRadius(int value) {
        this.socialRadius = value;
    }

    public int getAntiSocialRadius() {
        return this.antiSocialRadius;
    }

    public void setAntiSocialRadius(int value) {
        this.antiSocialRadius = value;
    }
}
