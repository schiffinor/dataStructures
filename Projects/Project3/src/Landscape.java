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
    public LinkedList<Agent> agentList;
    private final Random rand;
    // Keeps a history of game states
    //Stores previous state, not yet implemented.
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
     * a constructor that sets the width and height fields, and initializes the agent list.
     * @param w
     * @param h
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


    /**
     * Recreates the Landscape according to the specifications given in its initial construction.
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
        return getSector(agent.getX(), agent.getY());
    }
    public int[] getSector(double xCor, double yCor) {
        int[] output = new int[2];
        output[0] = (int) ((yCor - (yCor % this.sectorSize))/(this.sectorSize));
        output[1] = (int) ((xCor - (xCor % this.sectorSize))/(this.sectorSize));
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
        return this.agentList.size() + " Agents: " + agentList;
    }

    /**
     * returns a list of the Agents within radius distance of the location x0, y0.
     * @param x0
     * @param y0
     * @param radius
     * @return
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



    public void updateAgents() {
        for (Agent agent : this.agentList) {
            agent.updateState(this);
        }

    }

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

    public void draw(Graphics g, int scale) {
        for (Agent agent : agentList) {
            agent.draw(g,scale);
        }
    }
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

    public void setSleepTime(int value) {
        this.sleepTime = value;
    }

    public void setAgents(int value) {
        this.agentCount = value;
    }

    public void setSocialRadius(int value) {
        this.socialRadius = value;
    }

    public void setAntiSocialRadius(int value) {
        this.antiSocialRadius = value;
    }

    public int getSocialRadius() {
        return this.socialRadius;
    }

    public int getAntiSocialRadius() {
        return this.antiSocialRadius;
    }
}
