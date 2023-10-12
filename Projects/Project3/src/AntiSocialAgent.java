import java.awt.*;
import java.util.Arrays;
import java.util.Random;

/**
 * The `AntiSocialAgent` class represents a type of agent in a social simulation.
 * <p>
 * Social agents inherit from the `Agent` class and have an additional radius property,
 * which defines their social interaction range.
 * They are capable of updating their state based on the landscape and can be drawn on the landscape.
 *
 * @version 1.0
 */
public class AntiSocialAgent extends Agent{
    int radius;

    /**
     * Creates an antiSocial agent with the specified initial coordinates and social interaction radius.
     *
     * @param x0     The initial x-coordinate of the agent.
     * @param y0     The initial y-coordinate of the agent.
     * @param radius The social interaction radius of the agent.
     */
    public AntiSocialAgent(double x0, double y0, int radius) {
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
     * Updates the state of the antiSocial agent based on the provided landscape.
     * AntiSocial agents randomly move if they more than one neighbors within their social radius.
     * Otherwise, they maintain their current position.
     *
     * @param scape The landscape on which the agent resides.
     */
    @Override
    public void updateState(Landscape scape) {
        Random rand = new Random();
        LinkedList<Agent> neighborList = scape.getNeighbors(getX(), getY(), getRadius());
        neighborList.remove(this);
        if (neighborList.size()>1) {
            int[] oldSector = scape.getSector(this);
            double newX = -1.;
            double newY = -1.;
            Double[] newPos = new Double[2];
            do {
                double lowerBound = -10;
                double upperBound = 10;
                if (getX() - 10 < 0) {
                    lowerBound = -getX();
                }
                if (getX() + 10 > scape.getWidth()) {
                    upperBound = scape.getWidth() - getX();
                }
                newX = getX() + rand.nextDouble(lowerBound, upperBound);
            } while (newX < 0 || newX > scape.getWidth());
            do {
                double lowerBound = -10;
                double upperBound = 10;
                if (getY() - 10 < 0) {
                    lowerBound = -getY();
                }
                if (getY() + 10 > scape.getHeight()) {
                    upperBound = scape.getHeight() - getY();
                }
                newY = getY() + rand.nextDouble(lowerBound, upperBound);
            } while (newY < 0 || newY > scape.getHeight());
            newPos[0] = newX;
            newPos[1] = newY;
            setPos(newPos);
            int[] newSector = scape.getSector(this);
            scape.sectorMap.get(Arrays.toString(oldSector)).remove(this);
            scape.sectorMap.get(Arrays.toString(newSector)).add(this);
        }
        else {
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
            g.setColor(new Color(255, 0, 0));
        }
        else {
            g.setColor(new Color(255, 125, 125));
        }
        g.fillOval((int) getX()* scale, (int) getY()* scale, 5 * scale, 5 * scale);
    }
}
