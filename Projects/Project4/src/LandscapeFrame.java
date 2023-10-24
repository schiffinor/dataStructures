/*
I simply could not help myself.
 */


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

/**
 * The LandscapeFrame class represents a graphical user interface, GUI,
 * window for visualizing and interacting with the sudoku game.
 * It provides pretty minimal interaction as I really overshot this project
 * and ran out of time. It is slightly reworked though, the solver couldn't be
* adapted for the buttons because of lambda uses of thread causing problems with
 * draw functions, it could be fixed via some thread management, but that would
 * take too long,so no implementation.
 *
 * @author Roman Schiffino <rjschi24@colby.edu>
 * @version 1.1
 * @since 1.1
 */
public class LandscapeFrame {

    public final JScrollPane holder;
    public final DisplayPanel landscapePanel;
    final JFrame win;
    public int scale; //width (and height) of each square in the grid
    protected Board gameInit;

    public LandscapeFrame(Board landscapeObj, int scale) {
        super();
        //set up the window
        this.win = new JFrame("Sudoku Simulation");
        this.win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.gameInit = landscapeObj;
        getGameInit().setFrame(this);
        this.scale = scale;

        //create a panel in which to display the Landscape
        //put a buffer of two rows around the display grid
        this.landscapePanel = new DisplayPanel(gameInit, (this.gameInit.getCols() + 2) * this.scale,
                (this.gameInit.getRows() + 3) * this.scale, scale);

        //add the panel to the window, layout, and display
        createMenuBar();
        this.holder = new JScrollPane(landscapePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.win.add(holder, BorderLayout.CENTER);
        this.win.pack();
        this.win.setVisible(true);
    }

    //test function that creates a new LandscapeDisplay and populates it with 200 agents.
    public static void main(String[] args) {
        Board scape = new Board(40);
        Random gen = new Random();

        LandscapeFrame display = new LandscapeFrame(scape, 30);


    }

    public Board getGameInit() {
        return gameInit;
    }

    public void setGameInit(Board gameInit) {
        this.gameInit = gameInit;
        this.landscapePanel.setGame(gameInit);
    }

    /**
     * Creates and configures a menu bar for the graphical user interface.
     * This method sets up a menu bar with File and Simulation menus and populates
     * them with various menu items such as "Save Image," "Play," "Pause," "<<," and ">>."
     * Action listeners are attached to the menu items to handle user interactions.
     * The menu bar is then added to the JFrame for display.
     */
    public void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Create a File menu
        JMenu fileMenu = new JMenu("File");
        //Create a Simulation menu
        JMenu simMenu = new JMenu("Simulation");

        //Create a "Save Image" menu item
        JMenuItem saveMenuItem = new JMenuItem("Save Image");
        saveMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int choice = fileChooser.showSaveDialog(null);
            if (choice == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getAbsolutePath();
                saveImage(filename);
            }
        });

        //Add the "Save Image" menu item to the File menu
        fileMenu.add(saveMenuItem);

        //Create a "Proceeding State" button item
        JButton nextButton = new JButton(">>");
        nextButton.addActionListener(e -> {
            gameInit.advance();
            repaint();
        });

        //Create a "ZoomIn" Button item
        JButton zoomInButton = new JButton("+");
        zoomInButton.addActionListener(e -> {
            if (this.scale < 55) {
                this.scale += 1;
                repaint();
            }
        });

        //Create a "ZoomOut" Button item
        JButton zoomOutButton = new JButton("-");
        zoomOutButton.addActionListener(e -> {
            if (this.scale > 20) {
                this.scale -= 1;
                repaint();
            }
        });

        //Create a "Settings" menu item
        JMenuItem settings = new JMenuItem("Settings");
        settings.addActionListener(e -> {
            JDialog settingsDialog = new JDialog(this.win);
            settingsDialog.setTitle("Settings Menu");

            PopUpClass settingsPopup = new PopUpClass(settingsDialog, gameInit, this);
            settingsDialog.setContentPane(settingsPopup);

            settingsDialog.setVisible(true);
            settingsDialog.pack();
        });

        //Add the "Play," "Pause," "<<," and ">>" menu items to the Simulation menu
        simMenu.add(settings);

        //Add the File and Simulation menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(nextButton);
        menuBar.add(zoomOutButton);
        menuBar.add(zoomInButton);
        menuBar.add(simMenu);

        //Set the menu bar for the JFrame
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
        //get the file extension from the filename
        String ext = filename.substring(filename.lastIndexOf('.') + 1);

        //create an image buffer to save this component
        Component tosave = this.win.getRootPane();
        BufferedImage image = new BufferedImage(tosave.getWidth(), tosave.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        //paint the component to the image buffer
        Graphics g = image.createGraphics();
        tosave.paint(g);
        g.dispose();

        //save the image
        Path folderPath = Paths.get("data");
        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ImageIO.write(image, ext, new File(filename));
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Override of the basic repaint() method from Swing.
     * <p>
     * This effectively just adds a bunch of additional utilities as well as aiding in implementing automatic zoom
     * functions, and a scroll pane.
     */
    public void repaint() {
        this.win.repaint();
        int calcWidth = (this.gameInit.getRows() + 2) * this.scale;
        int calcHeight = (this.gameInit.getCols() + 3) * this.scale;
        this.landscapePanel.updateDimensions(calcWidth, calcHeight, this.scale);
        this.holder.revalidate();
        this.holder.getViewport().revalidate();
        if (calcWidth < 1920 || calcHeight < 1080) {
            this.win.pack();
        }

    }

    public int getscale() {
        return this.scale;
    }

    public void setscale(int scale) {
        this.scale = scale;
    }
}
