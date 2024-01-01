import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.*;

/**
 * The `Graph` class represents a versatile graph data structure, providing functionality for creating,
 * manipulating, and analyzing graphs with support for directed, undirected, or mixed graph types.
 *
 * <p>This class includes methods for graph construction, file-based graph creation, and operations for
 * working with vertices and edges, such as adding, removing, and retrieving them. It also supports graph
 * analysis, including finding connected components, calculating Betti numbers, and determining the
 * centroid of a connected component.</p>
 *
 * <p>Graphs can be created with random edges based on parameters such as the number of vertices, probability
 * of edge creation, and graph type. Alternatively, a graph can be initialized from a file, either using
 * a standard format or a custom format with a specified quantifier. The class leverages data structures
 * such as HashMaps and LinkedLists to efficiently manage vertices and edges.</p>
 *
 * <p>The `Graph` class is equipped with methods for distance calculation between vertices, computing the
 * incidence matrix (excluding mixed graphs), and determining fitting radii for connected components.
 * Fitting radii is a method designed for use in circle packing algorithms to efficiently represent
 * disconnected graphs; however, I unfortunately didn't fully implement that feature as it would take too long.
 * Additionally, the class provides methods for obtaining graph dimensions, width, and height, based on
 * fitting radii and connected component count.</p>
 *
 * <p>This class is an essential tool for graph-related tasks, ranging from basic graph creation to advanced
 * graph analysis. It provides a robust foundation for understanding and manipulating graph structures in
 * diverse scenarios.</p>
 *
 * @see Vertex
 * @see Edge
 * @see GraphType
 * @see EdgeType
 * @see ToroidalDoublyLinkedList
 * @see CircularLinkedList
 * @see HashSet
 * @see PriorityQueue
 * @see Stack
 * @see Queue
 *
 * <p>
 *
 * @author Roman Schiffino &lt;rjschi24@colby.edu&gt;
 * @version 1.0
 * @since 1.0
 */
public class Graph {

    private final Random r;
    private final HashMap<String, Edge> edges;
    private final HashMap<String, Vertex> vertices;
    private GraphType type;
    private double typeProbability;
    private int vertexCount;
    private int edgeCount;
    private int createdVertices;
    private int createdEdges;
    private Vertex centroid;
    private HashMap<Graph, Double> fittingRadii;

    /**
     * Constructs a new graph with no vertices.
     */
    public Graph() {
        this(0);
    }

    /**
     * Constructs a new graph with the specified number of vertices and default edge probability.
     *
     * @param n The number of vertices in the graph.
     */
    public Graph(int n) {
        this(n, 0.0);
    }

    /**
     * Constructs a new graph with the specified number of vertices and edge creation probability.
     *
     * @param n           The number of vertices in the graph.
     * @param probability The probability of creating an edge between any two vertices.
     */
    public Graph(int n, double probability) {
        this(n, probability, GraphType.UNDIRECTED);
    }

    /**
     * Constructs a new graph with the specified number of vertices, edge creation probability, and graph type.
     * <p>
     * Default type probability of 0.5.
     *
     * @param n           The number of vertices in the graph.
     * @param probability The probability of creating an edge between any two vertices.
     * @param type        The type of the graph, either undirected, directed, or mixed.
     */
    public Graph(int n, double probability, GraphType type) {
        this(n, probability, type, 0.5);
    }

    /**
     * Constructs a new graph with the specified number of vertices, edge creation probability, graph type,
     * type probability for mixed graphs, and a specified probability for directed edges in mixed graphs.
     *
     * @param n                The number of vertices in the graph.
     * @param probability      The probability of creating an edge between any two vertices.
     * @param graphType        The type of the graph, either undirected, directed, or mixed.
     * @param typeProbability  The probability of choosing an undirected edge in a mixed graph.
     */
    public Graph(int n, double probability, GraphType graphType, double typeProbability) {
        // Initiate all fields.
        this.r = new Random();
        this.edges = new HashMap<>();
        this.vertices = new HashMap<>();
        this.edgeCount = 0;
        this.vertexCount = 0;
        this.createdEdges = 0;
        this.createdVertices = 0;
        this.type = graphType;
        this.typeProbability = typeProbability;
        this.centroid = null;

        // Add vertices.
        for (int i = 0; i < n; i++) {
            addVertex();
        }

        // Add all Edges.
        // Copy all vertices to ArrayList.
        ArrayList<String> temp = new ArrayList<>(vertices.keySet());
        for (String vertexKey : vertices.keySet()) {
            // Select vertex to add edges too.
            Vertex vertex = vertices.get(vertexKey);

            // Remove the last selected vertex to not add edges twice.
            temp.remove(0);

            // Stop if all vertices added to.
            if (temp.isEmpty()) break;

            // Create edge per vertex in temp.
            for (String otherKey : temp) {
                Vertex other = vertices.get(otherKey);

                // Edge type dependent on probability.
                if (r.nextDouble(0, 1) <= probability) {
                    EdgeType eType = EdgeType.UNDIRECTED;
                    if (type == GraphType.DIRECTED || type == GraphType.MIXED) {
                        eType = (r.nextDouble(0, 1) < 0.5) ? EdgeType.DIRECTED_NORMAL : EdgeType.DIRECTED_INVERTED;
                    }
                    if (type == GraphType.MIXED && r.nextDouble(0, 1) <= typeProbability) {
                        eType = EdgeType.UNDIRECTED;
                    }
                    String edgeName = "Edge" + createdEdges;
                    addEdge(vertex, other, 1, eType, edgeName);
                }
            }

        }
    }

    /**
     * Creates a new graph based on the information provided in a file.
     * The file format should specify the number of vertices and edges, as well as the edges themselves.
     *
     * @param filename the name of the file containing the graph information.
     */
    public Graph(String filename) {
        this.r = new Random();
        this.edges = new HashMap<>();
        this.vertices = new HashMap<>();
        this.edgeCount = 0;
        this.vertexCount = 0;
        this.type = GraphType.UNDIRECTED;
        try {
            //Setup for reading the file
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);

            //Get the number of vertices from the file and initialize that number of verticies
            int numVertices = Integer.parseInt(br.readLine().split(": ")[1]);
            for (int i = 0; i < numVertices; i++) {
                addVertex();
            }

            //Read in the edges specified by the file and create them
            String header = br.readLine(); //We don't use the header, but have to read it to skip to the next line
            //Read in all the lines corresponding to edges
            String line = br.readLine();
            while (line != null) {
                //Parse out the index of the start and end vertices of the edge
                String[] arr = line.split(",");
                String start = arr[0];
                String end = arr[1];

                //Make the edge that starts at start and ends at end with weight 1
                String edgeName = "Edge" + createdEdges;
                addEdge(vertices.get(start), vertices.get(end), 1, EdgeType.UNDIRECTED, edgeName);

                //Read the next line
                line = br.readLine();
            }
            // call the close method of the BufferedReader:
            br.close();
            System.out.println(this.edges);
        } catch (FileNotFoundException ex) {
            System.out.println("Graph constructor:: unable to open file " + filename + ": file not found");
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            System.out.println("Graph constructor:: error reading file " + filename);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates a new graph based on the information provided in a file with a custom quantifier.
     * The file format should specify the graph type, vertices, and edges, along with their properties.
     *
     * @param filename the name of the file containing the graph information.
     * @param custom   a custom quantifier (must be "custom").
     * @throws IllegalArgumentException if the custom quantifier is not "custom".
     */
    public Graph(String filename, String custom) {
        if (!custom.equals("custom")) throw new IllegalArgumentException("custom quantifier must be \"custom\"");
        this.r = new Random();
        this.edges = new HashMap<>();
        this.vertices = new HashMap<>();
        this.edgeCount = 0;
        this.vertexCount = 0;
        try {
            //Setup for reading the file
            FileReader fr = new FileReader(filename);
            BufferedReader br = new BufferedReader(fr);


            this.type = switch (br.readLine().split(": ")[1]) {
                case "MIXED" -> GraphType.MIXED;
                case "UNDIRECTED" -> GraphType.UNDIRECTED;
                case "DIRECTED" -> GraphType.DIRECTED;
                default -> null;
            };
            if (this.type == null) throw new IllegalArgumentException("Unknown graph type");
            br.readLine();

            String line = br.readLine();

            while (line != null) {
                if (line.startsWith("Edges:")) break;
                String name = line.split("\\s+")[0];
                addVertex(name);
                line = br.readLine();
            }


            //Read in the edges specified by the file and create them
            br.readLine(); //We don't use the header, but have to read it to skip to the next line
            //Read in all the lines corresponding to edges
            line = br.readLine();

            while (line != null) {
                //Parse out the index of the start and end vertices of the edge
                String[] arr = line.split(",");
                String start = arr[0];
                String end = arr[1];
                double distance = Double.parseDouble(arr[2]);
                EdgeType edgeType = EdgeType.UNDIRECTED;
                if (arr.length == 4) {
                    edgeType = switch (arr[3].split("")[0]) {
                        case "u", "U" -> EdgeType.UNDIRECTED;
                        case "d", "D" -> EdgeType.DIRECTED_NORMAL;
                        case "i", "I" -> EdgeType.DIRECTED_INVERTED;
                        default -> throw new IllegalArgumentException("Unknown edge type");
                    };
                }

                //Make the edge that starts at start and ends at end with weight 1
                String edgeName = "Edge" + createdEdges;
                addEdge(vertices.get(start), vertices.get(end), distance, edgeType, edgeName);

                //Read the next line
                line = br.readLine();
            }
            // call the close method of the BufferedReader:
            br.close();
            System.out.println(this.edges);
        } catch (FileNotFoundException ex) {
            System.out.println("Graph constructor:: unable to open file " + filename + ": file not found");
        } catch (IOException ex) {
            System.out.println("Graph constructor:: error reading file " + filename);
        }
    }

    public static void main(String[] args) {
        // Test default constructor
        Graph graph1 = new Graph();
        System.out.println("Graph 1 - Type: " + graph1.getType() + ", Size: " + graph1.size());

        // Test constructor with specified number of vertices
        Graph graph2 = new Graph(5);
        System.out.println("Graph 2 - Type: " + graph2.getType() + ", Size: " + graph2.size());

        // Test constructor with specified number of vertices and probability
        Graph graph3 = new Graph(4, 0.3);
        System.out.println("Graph 3 - Type: " + graph3.getType() + ", Size: " + graph3.size());

        // Test constructor with specified number of vertices, probability, and graph type
        Graph graph4 = new Graph(3, 0.4, GraphType.DIRECTED);
        System.out.println("Graph 4 - Type: " + graph4.getType() + ", Size: " + graph4.size());

        // Test constructor with a file name
        Graph graph5 = new Graph("data/in/graph1.txt");
        System.out.println("Graph 5 - Type: " + graph5.getType() + ", Size: " + graph5.size());

        // Test constructor with a file name and custom quantifier
        Graph graph6 = new Graph("data/in/customGraph.txt", "custom");
        System.out.println("Graph 6 - Type: " + graph6.getType() + ", Size: " + graph6.size());

        // Test size, getVertices, and getEdges methods
        System.out.println("Graph 6 - Vertices: ");
        for (Vertex vertex : graph6.getVertices()) {
            System.out.println(vertex.getName());
        }

        System.out.println("Graph 6 - Edges: ");
        for (Edge edge : graph6.getEdges()) {
            System.out.println(edge);
        }

        // Test addVertex and addEdge methods
        Vertex newVertex = graph6.addVertex("NewVertex");
        Vertex anotherVertex = graph6.addVertex();
        Edge newEdge = graph6.addEdge(newVertex, anotherVertex, 2.0, EdgeType.DIRECTED_NORMAL);
        System.out.println("Graph 6 - Updated Size: " + graph6.size());
        // Test size, getVertices, and getEdges methods
        System.out.println("Graph 6 - Vertices: ");
        for (Vertex vertex : graph6.getVertices()) {
            System.out.println(vertex.getName());
        }

        System.out.println("Graph 6 - Edges: ");
        for (Edge edge : graph6.getEdges()) {
            System.out.println(edge);
        }

        // Test remove methods
        graph6.remove(newVertex);
        graph6.remove(graph6.getEdgeMap().get("Edge1"));
        // Test size, getVertices, and getEdges methods
        System.out.println("Graph 6 - Vertices: ");
        for (Vertex vertex : graph6.getVertices()) {
            System.out.println(vertex.getName());
        }

        System.out.println("Graph 6 - Edges: ");
        for (Edge edge : graph6.getEdges()) {
            System.out.println(edge);
        }
        System.out.println("Graph 6 - Updated Size after removal: " + graph6.size());

        // Test getTypeProbability, setTypeProbability methods
        System.out.println("Graph 6 - Initial Type Probability: " + graph6.getTypeProbability());
        graph6.setTypeProbability(0.7);
        System.out.println("Graph 6 - Updated Type Probability: " + graph6.getTypeProbability());

        // Test getVertexMap and getEdgeMap methods
        System.out.println("Graph 6 - Vertex Map: " + graph6.getVertexMap());
        System.out.println("Graph 6 - Edge Map: " + graph6.getEdgeMap());

        // Test getEdge method with vertices and distance
        Vertex vertexA = graph6.getVertexMap().get("0");
        Vertex vertexB = graph6.getVertexMap().get("1");
        Edge edgeAB = graph6.getEdge(vertexA, vertexB, 1.0);
        System.out.println("Graph 6 - Edge from A to B: " + edgeAB);

        // Test getEdge method with edge name
        Edge edgeByName = graph6.getEdge("Edge2");
        System.out.println("Graph 6 - Edge by Name: " + edgeByName);

        // Test remove(Vertex) method
        Vertex vertexToRemove = graph6.getVertexMap().get("3");
        graph6.remove(vertexToRemove);
        System.out.println("Graph 6 - Size after removing vertex: " + graph6.size());

        // Test remove(Edge) method
        Edge edgeToRemove = graph6.getEdgeMap().get("Edge2");
        graph6.remove(edgeToRemove);
        System.out.println("Graph 6 - Size after removing edge: " + graph6.size());

        System.out.println("Graph 6 - Vertices: ");
        for (Vertex vertex : graph6.getVertices()) {
            System.out.println(vertex.getName());
        }

        System.out.println("Graph 6 - Edges: ");
        for (Edge edge : graph6.getEdges()) {
            System.out.println(edge);
        }
        System.out.println("\n edge testing\n ");
        Edge newEdge2 = graph6.addEdge(graph6.getVertex("1"), graph6.getVertex("4"), 2.0, EdgeType.DIRECTED_NORMAL);
        System.out.println("Graph 6 - Updated Size: " + graph6.size());
        // Test size, getVertices, and getEdges methods
        System.out.println("Graph 6 - Vertices: ");
        for (Vertex vertex : graph6.getVertices()) {
            System.out.println(vertex.getName());
        }

        System.out.println("Graph 6 - Edges: ");
        for (Edge edge : graph6.getEdges()) {
            System.out.println(edge);
        }

        Edge newEdge4 = graph6.addEdge(graph6.getVertex("4"), graph6.getVertex("1"), 3.0, EdgeType.DIRECTED_INVERTED);
        System.out.println("Graph 6 - Updated Size: " + graph6.size());
        // Test size, getVertices, and getEdges methods
        System.out.println("Graph 6 - Vertices: ");
        for (Vertex vertex : graph6.getVertices()) {
            System.out.println(vertex.getName());
        }

        System.out.println("Graph 6 - Edges: ");
        for (Edge edge : graph6.getEdges()) {
            System.out.println(edge);
        }

        graph1 = null;
        graph2 = null;
        graph3 = null;
        graph4 = null;
        graph5 = null;
        graph6 = null;
        System.gc();

        Graph graph7 = new Graph("data/in/cG2.txt", "custom");
        System.out.println("Graph 7 - Type: " + graph7.getType() + ", Size: " + graph7.size());
        System.out.println(graph7.distanceFrom(graph7.getVertex("A")));
        System.out.println("Graph 7 - Paths: ");
        for (Vertex vertex : graph7.getVertices()) {
            System.out.println(vertex + ": " + vertex.getPreviousVertex().toString());
        }
        System.out.println("Graph 7 - Incidence: ");
        ToroidalDoublyLinkedList<Integer> iM = graph7.incidenceMatrix();
        System.out.println(iM);
        System.out.println("Graph 7 - Betti: ");
        System.out.println("Betti_0: " + graph7.betti_0());
        System.out.println("Betti_1: " + graph7.betti_1());
        LinkedList<HashSet<Vertex>> concom = graph7.connectedComponents();
        System.out.println("Connected Components: " + concom);
        for (HashSet<Vertex> cc : concom) {
            System.out.println(cc + " Centroid: " + graph7.centroid(cc));
        }
        graph7 = null;
        concom = null;
        iM = null;
        System.gc();

        Graph graph8 = new Graph("data/in/cG3.txt", "custom");
        System.out.println("Graph 8 - Type: " + graph8.getType() + ", Size: " + graph8.size());
        System.out.println(graph8.distanceFrom(graph8.getVertex("A")));
        System.out.println("Graph 8 - Paths: ");
        for (Vertex vertex : graph8.getVertices()) {
            System.out.println(vertex + ": " + vertex.getPreviousVertex().toString());
        }
        System.out.println("Graph 8 - Incidence: ");
        ToroidalDoublyLinkedList<Integer> iM1 = graph8.incidenceMatrix();
        System.out.println(iM1);
        System.out.println("Graph 8 - Betti: ");
        System.out.println("Betti_0: " + graph8.betti_0());
        System.out.println("Betti_1: " + graph8.betti_1());
        LinkedList<HashSet<Vertex>> concom1 = graph8.connectedComponents();
        System.out.println("Connected Components: " + concom1);
        for (HashSet<Vertex> cc : concom1) {
            System.out.println(cc + " Centroid: " + graph8.centroid(cc));
        }
        graph8 = null;
        concom1 = null;
        iM1 = null;
        System.gc();

        Graph graph9 = new Graph("data/in/cG4.txt", "custom");
        System.out.println("Graph 9 - Type: " + graph9.getType() + ", Size: " + graph9.size());
        System.out.println(graph9.distanceFrom(graph9.getVertex("A")));
        System.out.println("Graph 9 - Paths: ");
        for (Vertex vertex : graph9.getVertices()) {
            System.out.println(vertex + ": " + vertex.getPreviousVertex().toString());
        }
        System.out.println("Graph 9 - Incidence: ");
        ToroidalDoublyLinkedList<Integer> iM2 = graph9.incidenceMatrix();
        System.out.println(iM2);
        System.out.println("Graph 9 - Betti: ");
        System.out.println("Betti_0: " + graph9.betti_0());
        System.out.println("Betti_1: " + graph9.betti_1());
        LinkedList<HashSet<Vertex>> concom2 = graph9.connectedComponents();
        System.out.println("Connected Components: " + concom2);
        for (HashSet<Vertex> cc : concom2) {
            System.out.println(cc + " Centroid: " + graph9.centroid(cc));
        }
    }

    /**
     * Retrieves the random number generator used for edge creation probabilities.
     *
     * @return The random number generator instance.
     */
    public Random getR() {
        return r;
    }

    /**
     * Retrieves the type of the graph, whether undirected, directed, or mixed.
     *
     * @return The type of the graph.
     */
    public GraphType getType() {
        return type;
    }

    /**
     * Retrieves the probability of choosing an undirected edge in a mixed graph.
     *
     * @return The probability of choosing an undirected edge.
     */
    public double getTypeProbability() {
        return typeProbability;
    }

    /**
     * Sets the probability of choosing an undirected edge in a mixed graph.
     *
     * @param typeProbability The new probability of choosing an undirected edge.
     */
    public void setTypeProbability(double typeProbability) {
        this.typeProbability = typeProbability;
    }

    /**
     * Retrieves the map of edges in the graph.
     *
     * @return The map containing edges in the graph.
     */
    public HashMap<String, Edge> getEdgeMap() {
        return edges;
    }

    /**
     * Retrieves the map of vertices in the graph.
     *
     * @return The map containing vertices in the graph.
     */
    public HashMap<String, Vertex> getVertexMap() {
        return vertices;
    }

    /**
     * Generates the incidence matrix for the graph, representing the relationships between vertices and edges.
     * Note: This method is not supported for Mixed Graphs.
     *
     * @return ToroidalDoublyLinkedList instance representing the incidence matrix.
     * @throws IllegalArgumentException if the graph type is MIXED.
     */
    public ToroidalDoublyLinkedList<Integer> incidenceMatrix() {
        if (this.type == GraphType.MIXED)
            throw new IllegalArgumentException("incidenceMatrix() is not supported for Mixed Graphs.");

        // Initialize the matrix with the number of vertices and edges
        ToroidalDoublyLinkedList<Integer> matrix = new ToroidalDoublyLinkedList<>(vertexCount, edgeCount);

        // Sort edge and vertex lists for consistent matrix ordering
        ArrayList<String> edgeList = new ArrayList<>(edges.keySet());
        edgeList.sort(Comparator.comparingInt(a -> Integer.parseInt(a.split("(?<=\\D)(?=\\d)")[1])));
        ArrayList<String> vertexList = new ArrayList<>(vertices.keySet());

        // Populate row titles with vertex names
        for (int i = 0; i < vertexCount; i++) {
            matrix.getRowTitles().put(i, vertexList.get(i));
        }

        // Populate column titles with edge names
        for (int i = 0; i < edgeCount; i++) {
            String edgeName = edgeList.get(i);
            matrix.getColumnTitles().put(i, edgeName);

            // Create a circular linked list to represent each column in the matrix
            CircularLinkedList<Integer> tempList = new CircularLinkedList<>();
            Edge tempEdge = edges.get(edgeName);

            // Populate the column with 1, 0, or -1 based on the edge direction and connected vertices
            for (int j = 0; j < vertexCount; j++) {
                String vertexName = vertexList.get(j);
                Vertex tempVertex = vertices.get(vertexName);

                if (tempEdge.getDirection() == EdgeType.UNDIRECTED) {
                    tempList.add((tempVertex.equals(tempEdge.getHeadVertex()) || tempVertex.equals(tempEdge.getTailVertex()) ? 1 : 0));
                } else {
                    if (tempVertex.equals(tempEdge.getHeadVertex()))
                        tempList.add(1);
                    else if (tempVertex.equals(tempEdge.getTailVertex()))
                        tempList.add(-1);
                    else
                        tempList.add(0);
                }
            }

            // Set the column in the matrix
            matrix.setColumn(i, tempList);
        }
        return matrix;
    }

    /**
     * Returns the number of vertices in the graph.
     *
     * @return The size of the graph in terms of vertices.
     */
    public int size() {
        return vertexCount;
    }

    /**
     * Returns an iterable collection of all vertices in the graph.
     *
     * @return Iterable collection of vertices.
     */
    public Iterable<Vertex> getVertices() {
        return vertices.values();
    }

    /**
     * Returns an iterable collection of all edges in the graph.
     *
     * @return Iterable collection of edges.
     */
    public Iterable<Edge> getEdges() {
        return edges.values();
    }

    /**
     * Adds a vertex to the graph with an automatically generated name based on the current vertex count.
     *
     * @return The newly added vertex.
     */
    public Vertex addVertex() {
        return addVertex(String.valueOf(vertexCount));
    }

    /**
     * Adds a vertex to the graph with the specified name.
     *
     * @param name The name of the vertex to be added.
     * @return The newly added vertex.
     * @throws IllegalArgumentException if the provided name is null or already taken.
     */
    public Vertex addVertex(String name) {
        if (name == null) throw new IllegalArgumentException("Name cannot be null.");
        if (vertices.get(name) != null) throw new IllegalArgumentException("Name already taken.");

        // Create a new vertex with the specified name
        Vertex tempVertex = new Vertex(name);

        // Add the vertex to the vertices map
        vertices.put(name, tempVertex);

        // Increment vertex counts
        vertexCount++;
        createdVertices++;

        return tempVertex;
    }

    /**
     * Adds an undirected edge between two vertices with a given distance.
     * The edge type is determined based on the graph type and probability settings.
     *
     * @param u        The first vertex.
     * @param v        The second vertex.
     * @param distance The distance associated with the edge.
     */
    public void addEdge(Vertex u, Vertex v, double distance) {
        // Default edge type is undirected
        EdgeType edgeType = EdgeType.UNDIRECTED;

        // Adjust edge type based on graph type and probability
        if (type == GraphType.DIRECTED || type == GraphType.MIXED) {
            edgeType = (r.nextDouble(0, 1) < 0.5) ? EdgeType.DIRECTED_NORMAL : EdgeType.DIRECTED_INVERTED;
        }
        if (type == GraphType.MIXED && r.nextDouble(0, 1) <= typeProbability) {
            edgeType = EdgeType.UNDIRECTED;
        }

        // Delegate to the specific addEdge method
        addEdge(u, v, distance, edgeType);
    }

    /**
     * Adds an edge between two vertices with the given distance and direction.
     * The edge is assigned a default name.
     *
     * @param u         The first vertex.
     * @param v         The second vertex.
     * @param distance  The distance associated with the edge.
     * @param direction The direction of the edge (UNDIRECTED, DIRECTED_NORMAL, or DIRECTED_INVERTED).
     * @return The newly added edge.
     */
    public Edge addEdge(Vertex u, Vertex v, double distance, EdgeType direction) {
        return addEdge(u, v, distance, direction, "Edge" + createdEdges);
    }

    /**
     * Adds an edge between two vertices with the given distance, direction, and name.
     *
     * @param u         The first vertex.
     * @param v         The second vertex.
     * @param distance  The distance associated with the edge.
     * @param direction The direction of the edge (UNDIRECTED, DIRECTED_NORMAL, or DIRECTED_INVERTED).
     * @param name      The name of the edge.
     * @return The newly added edge.
     */
    public Edge addEdge(Vertex u, Vertex v, double distance, EdgeType direction, String name) {
        return addEdge(u, v, distance, direction, name, null);
    }

    /**
     * Adds an edge between two vertices with the given distance, direction, name, and identifier.
     * If an edge with the same vertices, distance, and direction already exists, it is replaced with the new edge.
     *
     * @param u          The first vertex.
     * @param v          The second vertex.
     * @param distance   The distance associated with the edge.
     * @param direction  The direction of the edge (UNDIRECTED, DIRECTED_NORMAL, or DIRECTED_INVERTED).
     * @param name       The name of the edge.
     * @param identifier The identifier of the edge.
     * @return The newly added edge.
     * @throws IllegalArgumentException if the direction is null or incompatible with the graph type.
     */
    public Edge addEdge(Vertex u, Vertex v, double distance, EdgeType direction, String name, String identifier)
            throws IllegalArgumentException {
        // Validate direction
        if (direction == null) throw new IllegalArgumentException("Direction must not be null");
        if ((type == GraphType.UNDIRECTED && direction != EdgeType.UNDIRECTED) ||
                (type == GraphType.DIRECTED && direction == EdgeType.UNDIRECTED))
            throw new IllegalArgumentException("Direction must be compatible with GraphType.");

        // Create a new edge
        Edge tempEdge = new Edge(u, v, distance, direction, name, identifier);

        // Fetch existing edge with the same properties
        Edge fetchEdge = getEdge(u, v, distance);

        // Remove existing edge if match is found and double not allowed in the graph type
        if (type != GraphType.MIXED && fetchEdge != null) {
            u.removeEdge(fetchEdge);
            v.removeEdge(fetchEdge);
            edges.remove(fetchEdge.getName(), fetchEdge);
        }

        // Handle replacement logic for mixed graphs
        if (fetchEdge != null) {
            if (fetchEdge.getDirection() == EdgeType.UNDIRECTED) return fetchEdge;
            if (fetchEdge.vertices()[0].equals(tempEdge.vertices()[1]) && fetchEdge.vertices()[1].equals(tempEdge.vertices()[0])) {
                tempEdge.setDirection(EdgeType.UNDIRECTED);
                remove(fetchEdge);
            }
        }

        // Add the edge to the vertices and edges collections
        u.addEdge(tempEdge);
        v.addEdge(tempEdge);

        //Add the edge to the collection of edges in the graph
        //Edges with the same name are always replaced.
        this.edges.put(name, tempEdge);

        // Increment edge counts
        edgeCount++;
        createdEdges++;

        return tempEdge;
    }

    /**
     * Gets the edge between two vertices.
     *
     * @param u The first vertex.
     * @param v The second vertex.
     * @return The edge between the specified vertices, or null if no such edge exists.
     */
    public Edge getEdge(Vertex u, Vertex v) {
        return u.getEdgeTo(v);
    }

    /**
     * Gets the edge between two vertices with the specified name.
     * This method is designed as an overloaded version of {@link #getEdge(String)}.
     *
     * @param u    The first vertex.
     * @param v    The second vertex.
     * @param name The name of the edge.
     * @return The edge with the specified name, or null if no such edge exists.
     */
    public Edge getEdge(Vertex u, Vertex v, String name) {
        return getEdge(name);
    }

    /**
     * Gets the edge between two vertices with the specified distance.
     *
     * @param u        The first vertex.
     * @param v        The second vertex.
     * @param distance The distance associated with the edge.
     * @return The edge between the specified vertices and distance, or null if no such edge exists.
     */
    public Edge getEdge(Vertex u, Vertex v, double distance) {
        if (u.getEdgeMap().get(v) == null) return null;
        return u.getEdgeMap().get(v).get(distance);
    }

    /**
     * Gets the edge with the specified name.
     *
     * @param name The name of the edge.
     * @return The edge with the specified name, or null if no such edge exists.
     */
    public Edge getEdge(String name) {
        return edges.get(name);
    }

    /**
     * Gets the vertex with the specified name.
     *
     * @param name The name of the vertex.
     * @return The vertex with the specified name, or null if no such vertex exists.
     */
    public Vertex getVertex(String name) {
        return vertices.get(name);
    }

    /**
     * Gets a random vertex from the graph.
     *
     * @return A randomly selected vertex from the graph.
     */
    public Vertex getVertex() {
        return (Vertex) vertices.values().toArray()[r.nextInt(0, vertices.size())];
    }

    /**
     * Removes a vertex and its incident edges from the graph.
     * If the vertex is not present in the graph, this method has no effect.
     *
     * @param vertex The vertex to be removed.
     */
    public void remove(Vertex vertex) {
        if (!vertices.containsKey(vertex.getName())) return;
        HashSet<Edge> incidentEdges = new HashSet<>(vertex.getIncidentEdges());
        vertex.disconnect();
        for (Edge edge : incidentEdges) {
            remove(edge);
        }
        vertexCount--;
        vertices.remove(vertex.getName(), vertex);
    }

    /**
     * Removes an edge from the graph.
     * If the edge is not present in the graph, this method has no effect.
     *
     * @param edge The edge to be removed.
     */
    public void remove(Edge edge) {
        if (edge == null) return;
        if (!edges.containsKey(edge.getName())) return;
        for (Vertex vertex : edge.vertices()) vertex.removeEdge(edge);
        edgeCount--;
        edges.remove(edge.getName(), edge);
    }

    /**
     * Gets the total number of vertices created in the graph.
     *
     * @return The total number of vertices created.
     */
    public int getCreatedVertices() {
        return createdVertices;
    }

    /**
     * Gets the total number of edges created in the graph.
     *
     * @return The total number of edges created.
     */
    public int getCreatedEdges() {
        return createdEdges;
    }

    /**
     * Calculates the distance from the source vertex to all other vertices in the graph using Dijkstra's algorithm.
     * The distances are stored in a HashMap where the key is the destination vertex, and the value is the distance.
     *
     * @param source The source vertex from which distances are calculated.
     * @return A HashMap containing the distances from the source vertex to all other vertices.
     */
    public HashMap<Vertex, Double> distanceFrom(Vertex source) {
        // Initialize the return map with the source vertex and a distance of 0.0
        HashMap<Vertex, Double> returnMap = new HashMap<>();
        returnMap.put(source, 0.0);

        // Set up data structures for Dijkstra's algorithm
        HashSet<Vertex> visited = new HashSet<>();
        PriorityQueue<Vertex> pQueue = new PriorityQueue<>();

        // Initialize source vertex properties
        source.setPreviousVertex(source);
        pQueue.offer(source);
        source.setRefDistance(0.0);

        // Populate priority queue and set reference distances for all vertices
        for (Vertex vertex : getVertices()) {
            if (vertex != source) {
                vertex.setRefDistance(Double.POSITIVE_INFINITY);
                pQueue.offer(vertex);
            }
        }

        // Dijkstra's algorithm
        while (!pQueue.isEmpty()) {
            Vertex currVertex = pQueue.poll();
            visited.add(currVertex);

            // Relaxation step: Update reference distances for neighboring vertices
            for (Edge edge : currVertex.getOutwardEdges()) {
                Vertex vertex = edge.other(currVertex);
                Double compoundDistance = currVertex.refDistance() + edge.getDistance();

                // If a shorter path is found, update vertex properties and priority queue
                if (compoundDistance < vertex.refDistance()) {
                    vertex.setRefDistance(compoundDistance);
                    vertex.setPreviousVertex(currVertex);
                    pQueue.remove(vertex);
                    pQueue.add(vertex);
                }
            }
        }

        // Populate the return map with the calculated distances
        for (Vertex vertex : visited) {
            returnMap.put(vertex, vertex.refDistance());
        }

        // Return the final distance map
        return returnMap;
    }

    /**
     * Calculates the 0th Betti number, which represents the number of connected components in the graph.
     *
     * @return The number of connected components in the graph.
     */
    public int betti_0() {
        return connectedComponents().size();
    }

    /**
     * Finds the connected components in the graph and returns them as a list of sets of vertices.
     * Uses a modified depth-first search algorithm for efficient traversal.
     *
     * @return A list of connected components, where each component is represented as a HashSet of vertices.
     */
    public LinkedList<HashSet<Vertex>> connectedComponents() {
        LinkedList<HashSet<Vertex>> connectedComponents = new LinkedList<>();
        Queue<Vertex> unvisited = new LinkedList<>(vertices.values());
        HashSet<Vertex> visited = new HashSet<>();
        Stack<Vertex> stack = new Stack<>();

        while (!unvisited.isEmpty()) {
            HashSet<Vertex> tempComponent = new HashSet<>();
            Vertex current = unvisited.poll();
            tempComponent.add(current);
            visited.add(current);
            Set<Vertex> neighbors = new HashSet<>(current.adjacentVertices());

            // Add unvisited neighbors to the stack for processing
            for (Vertex v : neighbors) {
                if (!visited.contains(v))
                    stack.push(v);
            }

            // Traverse connected component using DFS
            while (!stack.isEmpty()) {
                current = stack.pop();
                unvisited.remove(current);
                tempComponent.add(current);
                visited.add(current);
                neighbors = new HashSet<>(current.adjacentVertices());

                // Add unvisited neighbors to the stack for processing
                for (Vertex v : neighbors) {
                    if (!visited.contains(v))
                        stack.push(v);
                }
            }
            connectedComponents.add(tempComponent);
        }
        return connectedComponents;
    }

    /**
     * Calculates the 1st Betti number, which represents the graph's fundamental cycles.
     *
     * @return The 1st Betti number of the graph.
     */
    public int betti_1() {
        return edgeCount + betti_0() - vertexCount;
    }

    /**
     * Calculates the centroid of a set of vertices based on average distances.
     * The centroid is the vertex with the minimum maximum distance to other vertices in the set.
     * Or in other words, L∞ centrality.
     *
     * @param vertices The set of vertices for which to find the centroid.
     * @return The centroid vertex.
     */
    public Vertex centroid(Set<Vertex> vertices) {
        Vertex centroid = null;
        double maxDistance = Double.POSITIVE_INFINITY;
        // Iterate over each vertex to find the one with the maximum average distance
        for (final Vertex vertex : vertices) {
            double maxDistanceP = Collections.max(distanceFrom(vertex).values());
            if (maxDistance >= maxDistanceP) {
                maxDistance = maxDistanceP;
                centroid = vertex;
            }
        }
        return centroid;
    }

    /**
     * Generates fitting radii for connected components and returns them in a HashMap.
     * The fitting radius is the maximum distance from the centroid to any vertex in a connected component.
     * As mentioned earlier, this is a great utility in representing disconnected graphs.
     * We consider each connected component a circle of some radius that allows the graph,
     * in its entirety, to be contained within a circle of some radius from the centroid.
     * However, I came to realize that the algorithm used in the base representation isn't
     * true to distances and as such, this is nearly worthless.
     *
     * @return A HashMap containing connected components as keys and their fitting radii as values.
     */
    public HashMap<Graph, Double> getFittingRadii() {
        HashMap<Graph, Double> toReturn = new HashMap<>();
        LinkedList<HashSet<Vertex>> connectedComponents = connectedComponents();

        // Iterate over connected components and calculate fitting radii
        for (HashSet<Vertex> subGraph : connectedComponents) {
            Graph temp = new Graph(0, 0, this.getType(), this.getTypeProbability());

            // Add vertices to the temporary graph
            for (Vertex vert : subGraph) {
                temp.addVertex(vert.getName());
            }

            // Add edges to the temporary graph
            for (Vertex vert : subGraph) {
                for (Edge edge : vert.getOutwardEdges()) {
                    Edge out = temp.addEdge(temp.getVertex(vert.getName()), temp.getVertex(edge.other(vert).getName()),
                            edge.getDistance(), edge.getDirection(), edge.getName());
                }
            }

            // Calculate centroid and distances from the centroid
            Vertex subCentroid = centroid(subGraph);
            temp.setCentroid(subCentroid);
            HashMap<Vertex, Double> distances = temp.distanceFrom(subCentroid);

            // Find the maximum distance as the fitting radius and store in the result map
            Double maxDist = Collections.max(distances.values());
            toReturn.put(temp, maxDist);
        }
        fittingRadii = toReturn;
        return toReturn;
    }


    /**
     * Calculates the dimensions of the graph based on fitting radii of connected components.
     * If there are fewer than 3 connected components, returns the sum of fitting radii for x
     * and the maximum fitting radii for y.
     * Otherwise, returns the square root of the number of components times the maximum fitting radius.
     * Basic Grid offset representation.
     *
     * @return An array containing two integers representing the dimensions of the graph.
     */
    public int[] dimensions() {
        // If fitting radii are not calculated, generate them
        HashMap<Graph, Double> fitMap = (this.fittingRadii == null) ? getFittingRadii() : fittingRadii;
        System.out.println("fit" + fitMap);
        int subGraphCount = fitMap.size();

        // If there are fewer than 3 connected components, return sum and maximum fitting radii
        if (subGraphCount < 3) {
            System.out.println("dims" + Arrays.toString(new int[]{(int) Math.round(fitMap.values().stream().mapToDouble(x -> x).sum()),
                    (int) Math.round(Collections.max(fitMap.values()))}));
            return new int[]{(int) Math.round(fitMap.values().stream().mapToDouble(x -> x).sum()),
                    (int) Math.round(Collections.max(fitMap.values()))};
        }

        // Calculate dimensions as the square root of the number of components times the maximum fitting radius
        double squareRoot = Math.sqrt(subGraphCount);
        System.out.println("dims" + Arrays.toString(new int[]{(int) Math.round(Collections.max(fitMap.values()) * squareRoot),
                (int) Math.round(Collections.max(fitMap.values()) * squareRoot)}));
        return new int[]{(int) Math.round(Collections.max(fitMap.values()) * squareRoot),
                (int) Math.round(Collections.max(fitMap.values()) * squareRoot)};

    }

    /**
     * Gets the width dimension of the graph, calculated based on fitting radii of connected components.
     * If there are fewer than 3 connected components, returns the sum of fitting radii.
     * Otherwise, returns the square root of the number of components times the maximum fitting radius.
     *
     * @return The width dimension of the graph.
     */
    public int getWidth() {
        return dimensions()[0];
    }

    /**
     * Gets the height dimension of the graph, calculated based on fitting radii of connected components.
     * If there are fewer than 3 connected components, returns the sum of fitting radii.
     * Otherwise, returns the square root of the number of components times the maximum fitting radius.
     *
     * @return The height dimension of the graph.
     */
    public int getHeight() {
        return dimensions()[1];
    }

    /**
     * Gets the centroid vertex of the graph, which is a central vertex based on average distances.
     *
     * @return The centroid vertex of the graph.
     */
    public Vertex getCentroid() {
        return centroid;
    }

    /**
     * Sets the centroid vertex of the graph.
     *
     * @param centroid The vertex to be set as the centroid of the graph.
     */
    public void setCentroid(Vertex centroid) {
        this.centroid = centroid;
    }
}

