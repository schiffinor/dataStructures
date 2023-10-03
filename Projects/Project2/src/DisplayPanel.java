import javax.swing.*;
import java.awt.*;

public class DisplayPanel extends JPanel {

    public final Landscape game;
    public int width;
    public int height;
    public int gridScale;
    /**
     * Creates the panel.
     *
     * @param width  the width of the panel in pixels
     * @param height the height of the panel in pixels
     */
    public DisplayPanel(Landscape game, int width, int height, int gridScale) {
        super();
        this.game = game;
        this.width = width;
        this.height = height;
        this.gridScale = gridScale;
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.lightGray);
    }


    public void updateHeight(int width, int height, int gridScale) {
        this.width = width;
        this.height = height;
        this.gridScale = gridScale;
        this.setPreferredSize(new Dimension(width, height));
    }

    /**
     * Method overridden from JPanel that is responsible for
     * drawing components on the screen. The supplied Graphics
     * object is used to draw.
     *
     * @param g the Graphics object used for drawing
     */
    public void paintComponent(Graphics g) {
        // take care of housekeeping by calling parent paintComponent
        super.paintComponent(g);

        // call the Landscape draw method here
        game.draw(g, gridScale);
    } // end paintComponent

} // end LandscapePanel