import java.awt.*;
import java.util.Arrays;
import java.util.Random;

/**
 * The `SocialAgent` class represents a type of agent in a social simulation.
 * <p>
 * Social agents inherit from the `Agent` class and have an additional radius property,
 * which defines their social interaction range.
 * They are capable of updating their state based on the landscape and can be drawn on the landscape.
 *
 * @version 1.0
 */
public class SocialAgent extends Agent{
    private int radius;

    /**
     * Creates a social agent with the specified initial coordinates and social interaction radius.
     *
     * @param x0     The initial x-coordinate of the agent.
     * @param y0     The initial y-coordinate of the agent.
     * @param radius The social interaction radius of the agent.
     */
    public SocialAgent(double x0, double y0, int radius) {
        super(x0, y0);
        this.radius = radius;
    }

    /**
     * Gets the social interaction radius of the agent.
     *
     * @return The social interaction radius of the agent.
     */
    @Override
    public int getRadius() {
        return this.radius;
    }

    /**
     * Sets the social interaction radius of the agent and returns the previous radius.
     *
     * @param radius The new social interaction radius to set.
     * @return The previous social interaction radius of the agent.
     */
    public int setRadius(int radius) {
        int prev = this.radius;
        this.radius = radius;
        return prev;
    }

    /**
     * Updates the state of the social agent based on the provided landscape.
     * Social agents randomly move if they have fewer than four neighbors within their social radius.
     * Otherwise, they maintain their current position.
     *
     * @param scape The landscape on which the agent resides.
     */
    @Override
    public void updateState(Landscape scape) {

        //Generate a random number generator for making random movements
        Random rand = new Random();

        //Get a list of neighboring agents within the social interaction radius
        LinkedList<Agent> neighborList = scape.getNeighbors(getX(), getY(), getRadius());

        //Remove the current agent from the neighbor list
        neighborList.remove(this);


        if (neighborList.size()<4) {

            //Sets up conditions for moving the agent.
            int[] oldSector = scape.getSector(this);
            double newX = -1.;
            double newY = -1.;
            Double[] newPos = new Double[2];

            //Randomly generate new X coordinate within a specific range
            do {
                //Standard range
                double lowerBound = -10;
                double upperBound = 10;
                //Range if the agent is at the edge of the landscape
                if (getX()-10<0) {
                    lowerBound = -getX();
                }
                if (getX()+10>scape.getWidth()) {
                    upperBound = scape.getWidth()-getX();
                }
                //Random Generation
                newX = getX()+rand.nextDouble(lowerBound,upperBound);
            } while (newX<0||newX>scape.getWidth());
            //Randomly generate new Y coordinate within a specific range
            do {
                //Standard Range
                double lowerBound = -10;
                double upperBound = 10;
                //Range if the agent is at the edge of the landscape
                if (getY()-10<0) {
                    lowerBound = -getY();
                }
                if (getY()+10>scape.getHeight()) {
                    upperBound = scape.getHeight()-getY();
                }
                //Random Generation
                newY = getY()+rand.nextDouble(lowerBound,upperBound);
            } while (newY<0||newY>scape.getHeight());

            //Update the agent's position with the new coordinates
            newPos[0] = newX;
            newPos[1] = newY;
            setPos(newPos);

            //Get the new sector based on the updated position
            int[] newSector = scape.getSector(this);

            //Remove the agent from the old sector and add it to the new sector in the landscape
            scape.sectorMap.get(Arrays.toString(oldSector)).remove(this);
            scape.sectorMap.get(Arrays.toString(newSector)).add(this);
        }
        else {
            //The agent has enough neighbors, so it maintains its current position
            setPos(getPos());
        }
    }

    /**
     * Draws the social agent using the specified Graphics object with the given scale.
     *
     * @param g      The Graphics object to use for drawing.
     * @param scale  The scale to use for drawing the agent on a grid.
     */
    @Override
    public void draw(Graphics g, int scale) {
        if (!moved) {
            g.setColor(new Color(0, 0, 255));
        }
        else {
            g.setColor(new Color(125, 125, 255));
        }
        g.fillOval((int) getX()* scale, (int) getY()* scale, 5 * scale, 5 * scale);
    }
}
