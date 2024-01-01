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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

/**
 * Displays a Landscape graphically using Swing. The Landscape
 * contains a grid which can be displayed at any scale factor.
 * 
 * @author bseastwo
 */
public class GraphDisplay {

    static final class Coord {
        double x, y;

        Coord(double a, double b) {
            x = a;
            y = b;
        }

        double norm() {
            return Math.sqrt(x * x + y * y);
        }

        Coord diff(Coord c) {
            return new Coord(x - c.x, y - c.y);
        }

        Coord sum(Coord c) {
            return new Coord(x + c.x, y + c.y);
        }

        double dist(Coord c) {
            return diff(c).norm();
        }

        void addBy(Coord c) {
            x += c.x;
            y += c.y;
        }

        Coord scale(double d) {
            return new Coord(x * d, y * d);
        }

        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    JFrame win;
    protected Graph graph;
    private final LandscapePanel canvas;
    private final Driver gameInit;
    private int gridScale; // width (and height) of each square in the grid
    private final JScrollPane holder;

    AbstractPlayerAlgorithm pursuer, evader;
    HashMap<Vertex, Coord> coords;

    /**
     * Initializes a display window for a Landscape.
     * 
     * @param g the Graph to display
     * @param scale controls the relative size of the display
     * @throws InterruptedException
     */
    public GraphDisplay(Graph g, int scale, Driver game) throws InterruptedException {
        this(g, null, null, scale, game);
    }


    public GraphDisplay(Graph g, AbstractPlayerAlgorithm pursuer, AbstractPlayerAlgorithm evader, int scale, Driver game)
            throws InterruptedException {

        // setup the window
        this.win = new JFrame("Grid-Search");
        this.win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.pursuer = pursuer;
        this.evader = evader;

        this.graph = g;
        this.gridScale = scale;
        this.gameInit = game;

        // create a panel in which to display the Landscape
        // put a buffer of two rows around the display grid

        this.canvas = new LandscapePanel(this.gridScale * graph.size(),
                this.gridScale * graph.size());

        // add the panel to the window, layout, and display
        this.holder = new JScrollPane(canvas,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.win.add(holder, BorderLayout.CENTER);
        this.win.pack();
        createMenuBar();
        createCoordinateSystem();
        this.win.setVisible(true);
    }

    public void setGraph(Graph graph) throws InterruptedException {
        this.graph = graph;
        createCoordinateSystem();
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

    public void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Create a File menu
        JMenu fileMenu = new JMenu("File");

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

        //Create a "Play" button item
        JButton playButton = new JButton("Play");
        playButton.addActionListener(e -> {
            //Start the simulation if it is paused
            if (gameInit.getPaused()) {
                //Create a thread to run the simulation in the background
                gameInit.setPause(false);
                gameInit.play(this);
                //Create and start the thread
            }
        });

        //Create a "Pause" button item
        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> {
            if (!gameInit.getPaused()) {
                SwingUtilities.invokeLater(gameInit::pause);
            }
        });

        //Create a "Proceeding State" button item
        JButton nextButton = new JButton(">>");
        nextButton.addActionListener(e -> {
            gameInit.advance();
            repaint();
        });

        //Create a "ZoomIn" Button item
        JButton zoomInButton = new JButton("+");
        zoomInButton.addActionListener(e -> {
            if (this.gridScale<79) {
                this.gridScale += 1;
                System.out.println(gridScale);
                repaint();
            }
        });

        //Create a "ZoomOut" Button item
        JButton zoomOutButton = new JButton("-");
        zoomOutButton.addActionListener(e -> {
            if (this.gridScale>40) {
                this.gridScale -= 1;
                System.out.println(gridScale);
                repaint();
            }
        });



        //Add the File and Simulation menus to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(pauseButton);
        menuBar.add(playButton);
        menuBar.add(nextButton);
        menuBar.add(zoomOutButton);
        menuBar.add(zoomInButton);

        //Set the menu bar for the JFrame
        win.setJMenuBar(menuBar);
    }

    public void createCoordinateSystem() throws InterruptedException {

        // draw the graph
        // see http://yifanhu.net/PUB/graph_draw_small.pdf for more details
        Random rand = new Random();
        HashMap<Vertex, HashMap<Vertex, Double>> distances = new HashMap<>();
        for (Vertex v : graph.getVertices())
            distances.put(v, graph.distanceFrom(v));
        coords = new HashMap<>();
        LinkedList<HashSet<Vertex>> connectedComps = graph.connectedComponents();
        if (connectedComps.size() < 2) {
            for (Vertex v : graph.getVertices())
                coords.put(v, new Coord(rand.nextInt(canvas.getWidth() / 2) - Math.floorDiv(canvas.getWidth(),2),
                        rand.nextInt(canvas.getHeight() / 2) - Math.floorDiv(canvas.getHeight(),2)));

            double step = 1000;
            for (int i = 0; i < 100; i++) {
                HashMap<Vertex, Coord> newCoords = new HashMap<>();
                for (Vertex v : graph.getVertices()) {
                    Coord f = new Coord(0, 0);
                    boolean pickRandom = false;
                    for (Vertex u : graph.getVertices()) {
                        if (u == v)
                            continue;
                        Coord xv = coords.get(v);
                        Coord xu = coords.get(u);
                        if ((Math.abs(xv.x - xu.x) > .1 / i) && (Math.abs(xv.y - xu.y) > .1 / i))
                            f.addBy(xu.diff(xv).scale((xu.diff(xv).norm()
                                    - (distances.get(u).get(v) == Double.POSITIVE_INFINITY ? 1000
                                    : distances.get(u).get(v) * 100))
                                    / (xu.diff(xv).norm())));
                        else
                            pickRandom = true;
                    }
                    if (!pickRandom)
                        newCoords.put(v,
                                f.x == 0 && f.y == 0 ? coords.get(v) : coords.get(v).sum(f.scale(step / f.norm())));
                    else
                        newCoords.put(v, new Coord(rand.nextInt(canvas.getWidth() / 2) - Math.floorDiv(canvas.getWidth(),2),
                                rand.nextInt(canvas.getHeight() / 2) - Math.floorDiv(canvas.getHeight(),2)));

                }
                step *= .9;
                Coord average = new Coord(0, 0);
                for (Vertex v : graph.getVertices())
                    average.addBy(coords.get(v));
                average = average.scale(1.0 / graph.size());
                for (Coord c : newCoords.values()) {
                    c.x -= average.x;
                    c.x = Math.min(Math.max(c.x, -Math.floorDiv(canvas.getWidth(),2)), Math.floorDiv(canvas.getWidth(),2));
                    c.y -= average.y;
                    c.y = Math.min(Math.max(c.y, -Math.floorDiv(canvas.getHeight(),2)), Math.floorDiv(canvas.getHeight(),2));
                }
                coords = newCoords;
                // Uncomment below to see how the coordinates are formed!
                // repaint();
                // Thread.sleep(50);
            }
            Coord average = new Coord(0, 0);
            for (Vertex v : graph.getVertices())
                average.addBy(coords.get(v));
            average = average.scale(1.0 / graph.size());
            double maxNorm = 0;
            for (Vertex v : graph.getVertices()) {
                Coord newCoord = (new Coord(coords.get(v).x - average.x, coords.get(v).y - average.y));
                coords.put(v, newCoord);
                maxNorm = Math.max(maxNorm, newCoord.norm());
            }
            for (Vertex v : graph.getVertices())
                coords.put(v, coords.get(v)
                        .scale((Math.min(canvas.getWidth() / 2, canvas.getHeight() / 2) - Math.floorDiv(gridScale,2)) / maxNorm));

            int singletonCount = 0;
            for (Vertex v : graph.getVertices())
                if (!v.adjacentVertices().iterator().hasNext())
                    coords.put(v, new Coord(-Math.floorDiv(canvas.getWidth(),2) + gridScale * ++singletonCount,
                            -Math.floorDiv(canvas.getHeight(),2) + gridScale));

        } else {
            int row = 0;
            int column= 0;
            int subGraphCount = connectedComps.size();
            int newRow = (subGraphCount > 2) ? ((int) Math.round(Math.sqrt(subGraphCount))) : 2;
            double baseX = ((double) canvas.getWidth() / (2 * newRow));
            double baseY = ((double) canvas.getHeight() / (2 * newRow));
            for (Vertex v : graph.getVertices()) {
                System.out.println("disto: " + graph.distanceFrom(v));
            }
            for (HashSet<Vertex> subSet : connectedComps) {
                if (column % newRow == 0) {
                    column = 0;
                    row++;
                }
                Vertex centroid  = graph.centroid(subSet);
                for (Vertex v : subSet)
                    coords.put(v, new Coord(rand.nextInt(canvas.getWidth() / (2 *newRow)) - Math.floorDiv(canvas.getWidth(),(2 *newRow)),
                            rand.nextInt(canvas.getHeight() / (2 *newRow)) - Math.floorDiv(canvas.getHeight(),(2 *newRow))));
                double step = 1000;
                for (int i = 0; i < 100; i++) {
                    HashMap<Vertex, Coord> newCoords = new HashMap<>();
                    for (Vertex v : subSet) {
                        Coord f = new Coord(0, 0);
                        boolean pickRandom = false;
                        for (Vertex u : subSet) {
                            if (u == v)
                                continue;
                            Coord xv = coords.get(v);
                            Coord xu = coords.get(u);
                            if ((Math.abs(xv.x - xu.x) > .1 / i) && (Math.abs(xv.y - xu.y) > .1 / i))
                                f.addBy(xu.diff(xv).scale((xu.diff(xv).norm()
                                        - (distances.get(u).get(v) == Double.POSITIVE_INFINITY ? 1000
                                        : distances.get(u).get(v) * 100))
                                        / (xu.diff(xv).norm())));
                            else
                                pickRandom = true;
                        }
                        if (!pickRandom)
                            newCoords.put(v,
                                    f.x == 0 && f.y == 0 ? coords.get(v) : coords.get(v).sum(f.scale(step / f.norm())));
                        else
                            newCoords.put(v, new Coord(rand.nextInt(canvas.getWidth() / (2 *newRow)) - Math.floorDiv(canvas.getWidth(),(2 *newRow)),
                                    rand.nextInt(canvas.getHeight() / (2 *newRow)) - Math.floorDiv(canvas.getHeight(),(2 *newRow))));

                    }
                    step *= .9;
                    Coord average = new Coord(0, 0);
                    for (Vertex v : subSet)
                        average.addBy(coords.get(v));
                    average = average.scale(1.0 / graph.size());
                    for (Coord c : newCoords.values()) {
                        c.x -= average.x;
                        c.x = Math.min(Math.max(c.x, -Math.floorDiv(canvas.getWidth(),(2 *newRow))), Math.floorDiv(canvas.getWidth(),(2 *newRow)));
                        c.y -= average.y;
                        c.y = Math.min(Math.max(c.y, -Math.floorDiv(canvas.getHeight(),(2 *newRow))), Math.floorDiv(canvas.getHeight(),(2 *newRow)));
                    }
                    coords.putAll(newCoords);
                    // Uncomment below to see how the coordinates are formed!
                    // repaint();
                    // Thread.sleep(50);
                }
                Coord average = new Coord(0, 0);
                for (Vertex v : subSet)
                    average.addBy(coords.get(v));
                average = average.scale(1.0 / graph.size());
                double maxNorm = 0;
                for (Vertex v : subSet) {
                    Coord newCoord = (new Coord(coords.get(v).x - average.x, coords.get(v).y - average.y));
                    coords.put(v, newCoord);
                    maxNorm = Math.max(maxNorm, newCoord.norm());
                }

                int singletonCount = 0;
                for (Vertex v : graph.getVertices())
                    if (!v.adjacentVertices().iterator().hasNext())
                        coords.put(v, new Coord(-Math.floorDiv(canvas.getWidth(),2) + gridScale * ++singletonCount,
                                -Math.floorDiv(canvas.getHeight(),2) + gridScale));

                Coord gridCoord = new Coord(-baseX + (2 * column * baseX), -baseY + (2 * row * baseY));
                System.out.println("gridCoord: " + gridCoord);
                Coord centroidOffset = coords.get(centroid);
                System.out.println("centroidOffset: " + centroidOffset);
                for (Vertex v : subSet) {
                    Coord newCoord = (new Coord(coords.get(v).x - centroidOffset.x + gridCoord.x,
                            coords.get(v).y - centroidOffset.y + gridCoord.y));
                    coords.put(v, newCoord);
                }
                column++;
            }

        }
        System.out.println("zoop: " + coords);
    }

    /**
     * This inner class provides the panel on which Landscape elements
     * are drawn.
     */
    private class LandscapePanel extends JPanel {
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

        public void updateDimensions(int width, int height) {
            this.setPreferredSize(new Dimension(width, height));
        }

        /**
         * Method overridden from JComponent that is responsible for
         * drawing components on the screen. The supplied Graphics
         * object is used to draw.
         * 
         * @param g the Graphics object used for drawing
         */
        public void paintComponent(Graphics g) {
            // take care of housekeeping by calling parent paintComponent
            super.paintComponent(g);
            if (pursuer != null && pursuer.getCurrentVertex() == evader.getCurrentVertex())
                setBackground(new Color(0, 255, 0));
            g.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
            for (Edge e : graph.getEdges()) {
                g.setColor(Color.BLACK);
                Coord vert0 = coords.get(e.vertices()[0]).scale(((double) gridScale /40));
                Coord vert1 = coords.get(e.vertices()[1]).scale(((double) gridScale /40));
                double x1 = vert0.x + (double) gridScale / 4;
                double x2 = vert1.x + (double) gridScale / 4;
                double y1 = vert0.y + (double) gridScale / 4;
                double y2 = vert1.y + (double) gridScale / 4;
                int x1Scaled = (int) Math.round(x1);
                int x2Scaled = (int) Math.round(x2);
                int y1Scaled = (int) Math.round(y1);
                int y2Scaled = (int) Math.round(y2);
                g.drawLine(x1Scaled, y1Scaled, x2Scaled, y2Scaled);
                if (e.getDirection() != EdgeType.UNDIRECTED) {
                    double dx = x2-x1;
                    double dy = y2-y1;
                    double distance = vert1.dist(vert0);
                    Coord normalizedDistance = new Coord(dx, dy).scale(1/distance);
                    Coord offset = normalizedDistance.scale(10 * ((double) gridScale /40));
                    double angle = Math.atan2(dy, dx);
                    Coord point0 = new Coord(x2 - offset.x, y2 - offset.y);
                    Coord point1 = new Coord(point0.x - offset.x + (offset.norm() * Math.cos(angle + (Math.PI / 2))),
                            point0.y - offset.y + (offset.norm() * Math.sin(angle + (Math.PI / 2))));
                    Coord point2 = new Coord(point0.x - offset.x + (offset.norm() * Math.cos(angle - (Math.PI / 2))),
                            point0.y - offset.y + (offset.norm() * Math.sin(angle - (Math.PI / 2))));
                    int[] xPoints = new int[] {(int) Math.round(point0.x),
                            (int) Math.round(point1.x),
                            (int) Math.round(point2.x)};
                    int[] yPoints = new int[] {(int) Math.round(point0.y),
                            (int) Math.round(point1.y),
                            (int) Math.round(point2.y)};
                    g.fillPolygon(xPoints, yPoints, 3);
                }
            }
            for (Vertex v : graph.getVertices()) {
                if (pursuer != null && v == pursuer.getCurrentVertex() && v == evader.getCurrentVertex())
                    g.setColor(new Color(148, 0, 211));
                else if (pursuer != null && v == pursuer.getCurrentVertex())
                    g.setColor(Color.BLUE);
                else if (evader != null && v == evader.getCurrentVertex())
                    g.setColor(Color.RED);
                else
                    g.setColor(Color.BLACK);
                Coord scaled = coords.get(v).scale(((double) gridScale /40));
                g.fillOval((int) (scaled.x) , ((int) scaled.y ), gridScale / 2  * (gridScale/40), gridScale / 2 * (gridScale/40));
            }
        } // end paintComponent

    } // end LandscapePanel

    public void repaint() {
        this.win.repaint();
        //int calcWidth = graph.getWidth() * (gridScale);
        //int calcHeight = graph.getHeight() * (gridScale);
        //System.out.println("width=" + calcWidth + ", height=" + calcHeight);
        //this.canvas.updateDimensions(calcWidth, calcHeight);
        //this.holder.revalidate();
        //this.holder.getViewport().revalidate();
        //if (calcWidth<1920||calcHeight<1080) {
        //    this.win.pack();
        //}
    }

    public static void main(String[] args) throws InterruptedException {
        Graph g = new Graph();

        // This draws C5
        Vertex[] vertices = new Vertex[7];
        for (int i = 0; i < 7; i++) {
            vertices[i] = g.addVertex();
        }
        for (int i = 0; i < 7; i++) {
            g.addEdge(vertices[i], vertices[(i + 1) % 7], 1);
        }

        g.addEdge(vertices[0], vertices[2], 1);
        g.addEdge(vertices[0], vertices[3], 1);
        g.addEdge(vertices[3], vertices[6], 1);
        g.addEdge(vertices[4], vertices[6], 1);

        // new GraphDisplay(g, 100);

        // ArrayList<Vertex> ordered = new ArrayList<>();
        // for(int i = 0; i < 10; i++){
        // ordered.add(g.addVertex());
        // } for (int i = 0; i < 5; i++){
        // g.addEdge(ordered.get(i), ordered.get((i + 1) % 5), 3);
        // g.addEdge(ordered.get(i), ordered.get((i + 5)), 2);
        // g.addEdge(ordered.get(i + 5), ordered.get(((i + 2) % 5) + 5), 1);
        // }
        new Driver(10,.03);

    }
}