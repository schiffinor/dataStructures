
import java.awt.*;
import java.util.Arrays;

/**
 * The `Agent` class represents an abstract agent in a simulation.
 * Agents have coordinates, a position array, a scale, and the ability to move.
 * <p>
 * This class serves as the base class for specific agent types, and it provides basic methods for setting and
 * retrieving agent coordinates, as well as for updating the agent's state and drawing the agent on the landscape.
 *
 * @version 1.0
 */
public abstract class Agent {
    protected double xCor;            //x-coordinate of the agent
    protected double yCor;            //y-coordinate of the agent
    protected Double[] position;      //array storing the agent's position [xCor, yCor]
    protected boolean moved;          //indicates whether the agent has moved
    protected int scale;              //scale of the agent's representation

    /**
     * Creates an abstract agent with the specified initial coordinates.
     *
     * @param x0 The initial x-coordinate of the agent.
     * @param y0 The initial y-coordinate of the agent.
     */
    public Agent(double x0, double y0) {
        this.position = new Double[2];
        this.xCor = x0;
        this.yCor = y0;
        this.position[0] = this.xCor;
        this.position[1] = this.yCor;
        this.scale = 1;
    }

    /**
     * Gets the x-coordinate of the agent.
     *
     * @return The x-coordinate of the agent.
     */
    public double getX() {
        return this.xCor;
    }

    /**
     * Gets the y-coordinate of the agent.
     *
     * @return The y-coordinate of the agent.
     */
    public double getY() {
        return this.yCor;
    }

    /**
     * Gets the position of the agent as a Double array containing [x, y].
     *
     * @return The position of the agent as [x, y].
     */
    public Double[] getPos() {
        return this.position;
    }

    /**
     * Sets the x-coordinate of the agent and indicates whether the agent has moved.
     *
     * @param newX The new x-coordinate to set.
     * @return The previous x-coordinate of the agent.
     */
    public double setX(double newX) {
        double prev = this.xCor;
        if (prev != newX) {
            this.xCor = newX;
            this.position[0] = this.xCor;
            this.moved = true;
        }
        else {
            this.moved = false;
        }
        return prev;
    }

    /**
     * Sets the y-coordinate of the agent and indicates whether the agent has moved.
     *
     * @param newY The new y-coordinate to set.
     * @return The previous y-coordinate of the agent.
     */
    public double setY(double newY) {
        double prev = this.yCor;
        if (prev != newY) {
            this.yCor = newY;
            this.position[1] = this.yCor;
            this.moved = true;
        }
        else {
            this.moved = false;
        }
        return prev;
    }

    /**
     * Sets the position of the agent as a Double array [x, y] and indicates whether the agent has moved.
     *
     * @param newPos The new position to set as [x, y].
     * @return The previous position of the agent as [x, y].
     */
    public Double[] setPos(Double[] newPos) {
        Double[] prev = this.position;
        if (!Arrays.deepEquals(prev, newPos)) {
            this.xCor = newPos[0];
            this.yCor = newPos[1];
            this.position = newPos;
            this.moved = true;
        }
        else {
            this.moved = false;
        }
        return prev;    }

    /**
     * Gets the scale of the agent.
     *
     * @return The scale of the agent.
     */
    public int getScale() {
        return this.scale;
    }

    /**
     * Sets the scale of the agent.
     *
     * @param scale The new scale to set.
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * Returns a string representation of the agent's position.
     *
     * @return A string representing the agent's position as [x, y].
     */
    @Override
    public String toString() {
        return Arrays.toString(this.position);
    }

    /**
     * Draws the agent using the specified Graphics object.
     *
     * @param g The Graphics object to use for drawing.
     */
    public void draw(Graphics g) {
        draw(g,this.scale);
    }

    /**
     * Abstract method for updating the agent's state based on the landscape.
     *
     * @param scape The landscape on which the agent resides.
     */
    public abstract void updateState(Landscape scape);


    /**
     * Abstract method for drawing the agent with a specific grid scale using the specified Graphics object.
     *
     * @param g         The Graphics object to use for drawing.
     * @param gridScale The scale to use for drawing the agent on a grid.
     */
    public abstract void draw(Graphics g, int gridScale);

    /**
     * Gets the radius of the agent.
     *
     * @return The radius of the agent.
     */
    public abstract int getRadius();
}
