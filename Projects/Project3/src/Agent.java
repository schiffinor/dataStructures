import java.awt.*;
import java.util.Arrays;
import java.util.List;

public abstract class Agent {
    double xCor;
    double yCor;
    Double[] position;
    boolean moved;
    int scale;

    /**
     *
     * @param x0
     * @param y0
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
     *
     */
    public double getX() {
        return this.xCor;
    }

    /**
     *
     */
    public double getY() {
        return this.yCor;
    }

    /**
     *
     */
    public Double[] getPos() {
        return this.position;
    }

    /**
     *
     * @param newX
     * @return previous x-coordinate
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
     *
     * @param newY
     * @return previous y-coordinate
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
     *
     * @param newPos
     * @return previous position
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
        return prev;
    }

    public int getScale() {
        return this.scale;
    }


    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return Arrays.toString(this.position);
    }
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        SocialAgent AgentTest = new SocialAgent(0,0,1);
        AgentTest.setX(5);
        System.out.println(AgentTest);

    }

    /**
     *
     */
    public abstract void updateState(Landscape scape);
    public void draw(Graphics g) {
        draw(g,this.scale);
    }

    public abstract void draw(Graphics g, int gridScale);
}
