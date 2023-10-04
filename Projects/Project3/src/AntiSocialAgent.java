import java.awt.*;

public class AntiSocialAgent extends Agent{
    int radius;
    /**
     * @param x0
     * @param y0
     */
    public AntiSocialAgent(double x0, double y0, int radius) {
        super(x0, y0);
        this.radius = radius;
    }

    /**
     *
     * @return
     */
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

    }

    @Override
    public void draw(Graphics g, int scale) {
        if (!moved) {
            g.setColor(new Color(255, 0, 0));
        }
        else {
            g.setColor(new Color(255, 125, 125));
        }
        g.fillOval((int) getX(), (int) getY(), 5 * scale, 5 * scale);
    }
}
