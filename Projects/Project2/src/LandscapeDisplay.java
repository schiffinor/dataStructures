/*
  Originally written by Bruce A. Maxwell a long time ago.
  Updated by Brian Eastwood and Stephanie Taylor more recently
  Updated by Bruce again in Fall 2018
  Updated by Roman Schiffino on the on September 26th.

  Creates a window using the JFrame class.

  Creates a drawable area in the window using the JPanel class.

  The JPanel calls the Landscape's draw method to fill in content, so the
  Landscape class needs a draw method.
  
  Students should not *need* to edit anything outside of the main method,
  but are free to do so if they wish.

  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

  Personally I just touched up a couple of things here and there and added some things.
  Used thread builder as such Java JDK21 is necessary. Or --enable-preview necessary.
  Thread is used for a better implementation of GUI and toggleable game states.
*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Displays a Landscape graphically using Swing. The Landscape
 * contains a grid which can be displayed at any scale factor.
 * 
 * @author bseastwo
 */
public class LandscapeDisplay extends AbstractLandscapePresenter{

    //I may have made many a tweak.
    final JFrame win;
    protected final Landscape scape;
    private final int gridScale; // width (and height) of each square in the grid
    public Thread thread1;

    /**
     * Initializes a display window for a Landscape.
     * 
     * @param scape the Landscape to display
     * @param scale controls the relative size of the display
     */
    public LandscapeDisplay(Landscape scape, int scale) {
        // set up the window
        this.win = new JFrame("Game of Life");
        this.win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.scape = scape;
        this.gridScale = scale;

        // create a panel in which to display the Landscape
        // put a buffer of two rows around the display grid
        LandscapePanel canvas = new LandscapePanel((this.scape.getCols() + 4) * this.gridScale,
                (this.scape.getRows() + 4) * this.gridScale);

        // add the panel to the window, layout, and display
        createMenuBar();
        this.win.add(canvas, BorderLayout.CENTER);
        this.win.pack();
        this.win.setVisible(true);
    }

    /**
     * Saves an image of the display contents to a file. The supplied
     * filename should have an extension supported by javax.imageio, e.g.
     * "png" or "jpg".
     *
     * @param filename the name of the file to save
     */
    public void saveImage(String filename) {
        // get the file extension from the filename
        String ext = filename.substring(filename.lastIndexOf('.') + 1);

        // create an image buffer to save this component
        Component tosave = this.win.getRootPane();
        BufferedImage image = new BufferedImage(tosave.getWidth(), tosave.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        // paint the component to the image buffer
        Graphics g = image.createGraphics();
        tosave.paint(g);
        g.dispose();

        // save the image
        try {
            ImageIO.write(image, ext, new File(filename));
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * This inner class provides the panel on which Landscape elements
     * are drawn.
     */
    protected class LandscapePanel extends JPanel {
        /**
         * Creates the panel.
         * 
         * @param width  the width of the panel in pixels
         * @param height the height of the panel in pixels
         */
        public LandscapePanel(int width, int height) {
            super();
            this.setPreferredSize(new Dimension(width, height));
            this.setBackground(Color.lightGray);
        }

        /**
         * Method overridden from JHolder that is responsible for
         * drawing components on the screen. The supplied Graphics
         * object is used to draw.
         * 
         * @param g the Graphics object used for drawing
         */
        public void paintComponent(Graphics g) {
            // take care of housekeeping by calling parent paintComponent
            super.paintComponent(g);

            // call the Landscape draw method here
            scape.draw(g, gridScale);
        } // end paintComponent

    } // end LandscapePanel

    @Override
    public void repaint() {
        this.win.repaint();
    }

    public static void main(String[] args) {
        Landscape scape = new Landscape(100, 100, 50);

        LandscapeDisplay display = new LandscapeDisplay(scape, 6);

    }

    /*
    Decided to separate what I made from scratch to make it clearer.
     */

    /**
     * Creates and configures a menu bar for the graphical user interface.
     * This method sets up a menu bar with File and Simulation menus and populates
     * them with various menu items such as "Save Image," "Play," "Pause," "<<," and ">>."
     * Action listeners are attached to the menu items to handle user interactions.
     * The menu bar is then added to the JFrame for display.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Create a File menu
        JMenu fileMenu = new JMenu("File");
        // Create a Simulation menu
        JMenu simMenu = new JMenu("Simulation");

        // Create a "Save Image" menu item
        JMenuItem saveMenuItem = new JMenuItem("Save Image");
        saveMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int choice = fileChooser.showSaveDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                saveImage(filename);
            }
        });

        // Add the "Save Image" menu item to the File menu
        fileMenu.add(saveMenuItem);

        // Create a "Play" menu item
        JMenuItem playMenuItem = new JMenuItem("Play");
        playMenuItem.addActionListener(e -> {
            // Start the simulation if it is paused
            if (scape.getPaused()) {
                // Create a thread to run the simulation in the background
                Runnable runnable = () -> scape.play(this);

                // Create and start the thread
                Thread.Builder builder = Thread.ofVirtual().name("playThread");
                thread1 = builder.start(runnable);
            }
        });

        // Create a "Pause" menu item
        JMenuItem pauseMenuItem = new JMenuItem("Pause");
        pauseMenuItem.addActionListener(e -> {
            if (!Landscape.paused) {
                scape.pause();
            }
        });

        // Create a "Previous State" menu item
        JMenuItem backMenuItem = new JMenuItem("<<");
        backMenuItem.addActionListener(e -> {
            scape.revert();
            win.repaint();
        });

        // Create a "Proceeding State" menu item
        JMenuItem nextMenuItem = new JMenuItem(">>");
        nextMenuItem.addActionListener(e -> {
            scape.advance();
            win.repaint();
        });

        // Add the "Play," "Pause," "<<," and ">>" menu items to the Simulation menu
        simMenu.add(playMenuItem);
        simMenu.add(pauseMenuItem);
        simMenu.add(backMenuItem);
        simMenu.add(nextMenuItem);

        // Add the File and Simulation menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(simMenu);

        // Set the menu bar for the JFrame
        win.setJMenuBar(menuBar);
    }
}