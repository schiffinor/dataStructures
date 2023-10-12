import java.awt.*;
import java.util.Arrays;
import java.util.Random;

/**
 *
 */
public class SocialAgent extends Agent{
    int radius;
    /**
     * @param x0
     * @param y0
     */
    public SocialAgent(double x0, double y0, int radius) {
        super(x0, y0);
        this.radius = radius;
    }

    /**
     *
     * @return
     */
    @Override
    public int getRadius() {
        return this.radius;
    }
    /**
     *
     * @return
     */
    public int setRadius(int radius) {
        int prev = this.radius;
        this.radius = radius;
        return prev;
    }

    @Override
    public void updateState(Landscape scape) {
        Random rand = new Random();
        LinkedList<Agent> neighborList = scape.getNeighbors(getX(), getY(), getRadius());
        neighborList.remove(this);
        if (neighborList.size()<4) {
            int[] oldSector = scape.getSector(this);
            double newX = -1.;
            double newY = -1.;
            Double[] newPos = new Double[2];
            do {
                double lowerBound = -10;
                double upperBound = 10;
                if (getX()-10<0) {
                    lowerBound = -getX();
                }
                if (getX()+10>scape.getWidth()) {
                    upperBound = scape.getWidth()-getX();
                }
                newX = getX()+rand.nextDouble(lowerBound,upperBound);
            } while (newX<0||newX>scape.getWidth());
            do {
                double lowerBound = -10;
                double upperBound = 10;
                if (getY()-10<0) {
                    lowerBound = -getY();
                }
                if (getY()+10>scape.getHeight()) {
                    upperBound = scape.getHeight()-getY();
                }
                newY = getY()+rand.nextDouble(lowerBound,upperBound);
            } while (newY<0||newY>scape.getHeight());
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
