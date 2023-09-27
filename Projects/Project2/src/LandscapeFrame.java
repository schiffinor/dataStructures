/*
I simply could not help myself.
 */


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author      Roman Schiffino <rjschi24@colby.edu>
 * @version     1.1
 * @since       1.1
 */

public class LandscapeFrame {

    final JFrame win;
    protected final Landscape gameInit;
    public int gridScale; // width (and height) of each square in the grid
    public JScrollPane holder;
    public DisplayPanel landscapePanel;
    public LandscapeFrame(Landscape landscapeObj, int scale) {
        // set up the window
        this.win = new JFrame("Specific Game of Life");
        this.win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.gameInit = landscapeObj;
        this.gridScale = scale;

        // create a panel in which to display the Landscape
        // put a buffer of two rows around the display grid
        this.landscapePanel = new DisplayPanel(gameInit, (this.gameInit.getCols() + 4) * this.gridScale,
                (this.gameInit.getRows() + 4) * this.gridScale, gridScale);

        // add the panel to the window, layout, and display
        createMenuBar();
        this.holder = new JScrollPane(landscapePanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.win.add(holder, BorderLayout.CENTER);
        this.win.pack();
        this.win.setVisible(true);
    }

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

        // Create a "Play" button item
        JButton playButton = new JButton("Play");
        playButton.addActionListener(e -> {
            // Start the simulation if it is paused
            if (Landscape.paused) {
                // Create a thread to run the simulation in the background
                Runnable runnable = () -> gameInit.play(this.win);

                // Create and start the thread
                Thread.Builder builder = Thread.ofVirtual().name("playThread");
                Thread thread1 = builder.start(runnable);
            }
        });

        // Create a "Pause" button item
        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> {
            if (!Landscape.paused) {
                gameInit.pause();
            }
        });

        // Create a "Previous State" button item
        JButton backButton = new JButton("<<");
        backButton.addActionListener(e -> {
            gameInit.revert();
            repaint();
        });

        // Create a "Proceeding State" button item
        JButton nextButton = new JButton(">>");
        nextButton.addActionListener(e -> {
            gameInit.advance();
            repaint();
        });

        // Create a "ZoomIn" Button item
        JButton zoomInButton = new JButton("+");
        zoomInButton.addActionListener(e -> {
            if (this.gridScale<30) {
                this.gridScale += 1;
                repaint();
            }
        });

        // Create a "ZoomIn" Button item
        JButton zoomOutButton = new JButton("-");
        zoomOutButton.addActionListener(e -> {
                if (this.gridScale>2) {
                    this.gridScale -= 1;
                    repaint();
                }
        });

        // Create a "enable Predator Cells" Button item
        JCheckBoxMenuItem enablePredatorCells = new JCheckBoxMenuItem("Enable Predator Cells");
        enablePredatorCells.addActionListener(e -> {
            if (this.gridScale>2) {
                this.gridScale -= 1;
                repaint();
            }
        });

        // Create a "enable Predator Cells" Button item
        JCheckBoxMenuItem enableProtectorCells = new JCheckBoxMenuItem("Enable Protector Cells");
        enableProtectorCells.addActionListener(e -> {
            if (this.gridScale>2) {
                this.gridScale -= 1;
                repaint();
            }
        });

        // Create a "enable Predator Cells" Button item
        JMenuItem reset = new JMenuItem("Reset Game");
        reset.addActionListener(e -> {
            JOptionPane resetConditions = new JOptionPane();
            JMenu resetData = new JMenu();

            String o;
        });

        // Add the "Play," "Pause," "<<," and ">>" menu items to the Simulation menu


        // Add the File and Simulation menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(backButton);
        menuBar.add(pauseButton);
        menuBar.add(playButton);
        menuBar.add(nextButton);
        menuBar.add(zoomOutButton);
        menuBar.add(zoomInButton);
        menuBar.add(simMenu);

        // Set the menu bar for the JFrame
        win.setJMenuBar(menuBar);
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
        String ext = filename.substring(filename.lastIndexOf('.') + 1, filename.length());

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


    public void repaint() {
        this.win.repaint();
        this.landscapePanel.updateHeight((this.gameInit.getCols() + 4) * this.gridScale,
                (this.gameInit.getRows() + 4) * this.gridScale, this.gridScale);
        this.holder.revalidate();
        this.holder.getViewport().revalidate();
    }

    public static void main(String[] args) {
        Landscape game = new Landscape(100, 200, 50);

        LandscapeFrame display = new LandscapeFrame(game, 6);

    }
}
