import javax.swing.*;
import java.awt.*;

public class DisplayPanel extends JPanel {

    public final Board game;
    public int width;
    public int height;
    public int scale;
    /**
     * Creates the panel.
     *
     * @param width  the width of the panel in pixels
     * @param height the height of the panel in pixels
     */
    public DisplayPanel(Board game, int width, int height, int scale) {
        super();
        this.game = game;
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.white);
        this.setFont(new Font("Serif", Font.PLAIN, scale));
    }


    public void updateDimensions(int width, int height, int scale) {
        this.width = width;
        this.height = height;
        this.scale = scale;
        this.setPreferredSize(new Dimension(width, height));
        this.setFont(new Font("Serif", Font.PLAIN, scale));
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
        game.draw(g, scale);
    } // end paintComponent

} // end LandscapePanel