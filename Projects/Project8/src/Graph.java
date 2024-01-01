import java.io.*;
import java.lang.ref.Cleaner;
import java.util.*;
import java.util.PriorityQueue;

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

    public Graph() {
        this(0);
    }

    public Graph(int n) {
        this(n, 0.0);
    }

    public Graph(int n, double probability) {
        this(n, probability, GraphType.UNDIRECTED);
    }

    public Graph(int n, double probability, GraphType type) {
        this(n, probability, type, 0.5);
    }

    @SuppressWarnings("unchecked")
    public Graph(int n, double probability, GraphType graphType, double typeProbability) {
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
        for (int i = 0; i < n; i++) {
            addVertex();
        }
        ArrayList<String> temp = new ArrayList<>(vertices.keySet());
        for (String vertexKey : vertices.keySet()) {
            Vertex vertex = vertices.get(vertexKey);
            temp.remove(0);
            if (temp.isEmpty()) break;
            for (String otherKey : temp) {
                Vertex other = vertices.get(otherKey);
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

    /* *
     * A graph constructor that takes in a filename and builds
     * the graph with the number of vertices and specific edges
     * specified.
     * */
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

    public Random getR() {
        return r;
    }

    public GraphType getType() {
        return type;
    }

    public double getTypeProbability() {
        return typeProbability;
    }

    public void setTypeProbability(double typeProbability) {
        this.typeProbability = typeProbability;
    }

    public HashMap<String, Edge> getEdgeMap() {
        return edges;
    }

    public HashMap<String, Vertex> getVertexMap() {
        return vertices;
    }

    public ToroidalDoublyLinkedList<Integer> incidenceMatrix() {
        if (this.type == GraphType.MIXED)
            throw new IllegalArgumentException("incidenceMatrix() is not supported for Mixed Graphs.");
        ToroidalDoublyLinkedList<Integer> matrix = new ToroidalDoublyLinkedList<>(vertexCount, edgeCount);
        ArrayList<String> edgeList = new ArrayList<>(edges.keySet());
        edgeList.sort(Comparator.comparingInt(a -> Integer.parseInt(a.split("(?<=\\D)(?=\\d)")[1])));
        ArrayList<String> vertexList = new ArrayList<>(vertices.keySet());

        for (int i = 0; i < vertexCount; i++) {
            matrix.getRowTitles().put(i, vertexList.get(i));
        }
        for (int i = 0; i < edgeCount; i++) {
            String edgeName = edgeList.get(i);
            matrix.getColumnTitles().put(i, edgeName);
            CircularLinkedList<Integer> tempList = new CircularLinkedList<>();
            Edge tempEdge = edges.get(edgeName);
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
            matrix.setColumn(i, tempList);
        }
        return matrix;
    }

    public int size() {
        return vertexCount;
    }

    public Iterable<Vertex> getVertices() {
        return vertices.values();
    }

    public Iterable<Edge> getEdges() {
        return edges.values();
    }

    public Vertex addVertex() {
        return addVertex(String.valueOf(vertexCount));
    }

    public Vertex addVertex(String name) {
        if (name == null) throw new IllegalArgumentException("Name cannot be null.");
        if (vertices.get(name) != null) throw new IllegalArgumentException("Name already taken.");
        Vertex tempVertex = new Vertex(name);
        vertices.put(name, tempVertex);
        vertexCount++;
        createdVertices++;
        return tempVertex;
    }

    public Edge addEdge(Vertex u, Vertex v, double distance) {
        EdgeType edgeType = EdgeType.UNDIRECTED;
        if (type == GraphType.DIRECTED || type == GraphType.MIXED) {
            edgeType = (r.nextDouble(0, 1) < 0.5) ? EdgeType.DIRECTED_NORMAL : EdgeType.DIRECTED_INVERTED;
        }
        if (type == GraphType.MIXED && r.nextDouble(0, 1) <= typeProbability) {
            edgeType = EdgeType.UNDIRECTED;
        }
        return addEdge(u, v, distance, edgeType);
    }

    public Edge addEdge(Vertex u, Vertex v, double distance, EdgeType direction) {
        return addEdge(u, v, distance, direction, "Edge" + createdEdges);
    }

    public Edge addEdge(Vertex u, Vertex v, double distance, EdgeType direction, String name) {
        return addEdge(u, v, distance, direction, name, null);
    }

    public Edge addEdge(Vertex u, Vertex v, double distance, EdgeType direction, String name, String identifier) {
        if (direction == null) throw new IllegalArgumentException("Direction must not be null");
        if ((type == GraphType.UNDIRECTED && direction != EdgeType.UNDIRECTED) ||
                (type == GraphType.DIRECTED && direction == EdgeType.UNDIRECTED))
            throw new IllegalArgumentException("Direction must be compatible with GraphType.");
        Edge tempEdge = new Edge(u, v, distance, direction, name, identifier);
        Edge fetchEdge = getEdge(u, v, distance);
        if (type != GraphType.MIXED && fetchEdge != null) {
            u.removeEdge(fetchEdge);
            v.removeEdge(fetchEdge);
            edges.remove(fetchEdge.getName(), fetchEdge);
        }
        if (fetchEdge != null) {
            if (fetchEdge.getDirection() == EdgeType.UNDIRECTED) return fetchEdge;
            if (fetchEdge.vertices()[0].equals(tempEdge.vertices()[1]) && fetchEdge.vertices()[1].equals(tempEdge.vertices()[0])) {
                tempEdge.setDirection(EdgeType.UNDIRECTED);
                remove(fetchEdge);
            }
        }
        u.addEdge(tempEdge);
        v.addEdge(tempEdge);

        //Add the edge to the collection of edges in the graph
        this.edges.put(name, tempEdge);
        edgeCount++;
        createdEdges++;
        return tempEdge;
    }

    public Edge getEdge(Vertex u, Vertex v) {
        return u.getEdgeTo(v);
    }

    public Edge getEdge(Vertex u, Vertex v, String name) {
        return getEdge(name);
    }

    public Edge getEdge(Vertex u, Vertex v, double distance) {
        if (u.getEdgeMap().get(v) == null) return null;
        return u.getEdgeMap().get(v).get(distance);
    }

    public Edge getEdge(String name) {
        return edges.get(name);
    }

    public Vertex getVertex(String name) {
        return vertices.get(name);
    }

    public Vertex getVertex() {
        return (Vertex) vertices.values().toArray()[r.nextInt(0, vertices.size())];
    }

    public boolean remove(Vertex vertex) {
        if (!vertices.containsKey(vertex.getName())) return false;
        HashSet<Edge> incidentEdges = new HashSet<>(vertex.getIncidentEdges());
        vertex.disconnect();
        for (Edge edge : incidentEdges) {
            remove(edge);
        }
        vertexCount--;
        return vertices.remove(vertex.getName(), vertex);
    }

    public boolean remove(Edge edge) {
        if (edge == null) return false;
        if (!edges.containsKey(edge.getName())) return false;
        for (Vertex vertex : edge.vertices()) vertex.removeEdge(edge);
        edgeCount--;
        return edges.remove(edge.getName(), edge);
    }

    public int getCreatedVertices() {
        return createdVertices;
    }

    public int getCreatedEdges() {
        return createdEdges;
    }

    public HashMap<Vertex, Double> distanceFrom(Vertex source) {
        HashMap<Vertex, Double> returnMap = new HashMap<>();
        returnMap.put(source, 0.0);

        HashSet<Vertex> visited = new HashSet<>();
        PriorityQueue<Vertex> pQueue = new PriorityQueue<>();

        source.setPreviousVertex(source);
        pQueue.offer(source);
        source.setRefDistance(0.0);
        for (Vertex vertex : getVertices()) {
            if (vertex != source) {
                vertex.setRefDistance(Double.POSITIVE_INFINITY);
                pQueue.offer(vertex);
            }
        }

        while (!pQueue.isEmpty()) {
            Vertex currVertex = pQueue.poll();
            visited.add(currVertex);
            for (Edge edge : currVertex.getOutwardEdges()) {
                Vertex vertex = edge.other(currVertex);
                Double compoundDistance = currVertex.refDistance() + edge.getDistance();
                if (compoundDistance < vertex.refDistance()) {
                    vertex.setRefDistance(compoundDistance);
                    vertex.setPreviousVertex(currVertex);
                    pQueue.remove(vertex);
                    pQueue.add(vertex);
                }
            }
        }
        for (Vertex vertex : visited) {
            returnMap.put(vertex, vertex.refDistance());
        }
        return returnMap;
    }

    public int betti_0() {
        return connectedComponents().size();
    }

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
            for (Vertex v : neighbors) {
                if (!visited.contains(v))
                    stack.push(v);
            }
            while (!stack.isEmpty()) {
                current = stack.pop();
                unvisited.remove(current);
                tempComponent.add(current);
                visited.add(current);
                neighbors = new HashSet<>(current.adjacentVertices());
                for (Vertex v : neighbors) {
                    if (!visited.contains(v))
                        stack.push(v);
                }
            }
            connectedComponents.add(tempComponent);
        }
        return connectedComponents;
    }

    public int betti_1() {
        return edgeCount + betti_0() - vertexCount;
    }

    public Vertex centroid(Set<Vertex> vertices) {
        Vertex centroid = null;
        double maxDistance = 0.0;
        for (final Vertex vertex : vertices) {
            double averageDistance = distanceFrom(vertex).values().stream().mapToDouble(x -> x).average().orElse(0.0);
            if (maxDistance < averageDistance) {
                maxDistance = averageDistance;
                centroid = vertex;
            }
        }
        return centroid;
    }

    public HashMap<Graph, Double> getFittingRadii() {
        HashMap<Graph, Double> toReturn = new HashMap<>();
        LinkedList<HashSet<Vertex>> connectedComponents = connectedComponents();
        for (HashSet<Vertex> subGraph : connectedComponents) {
            Graph temp = new Graph(0, 0, this.getType(), this.getTypeProbability());

            for (Vertex vert : subGraph) {
                temp.addVertex(vert.getName());
            }
            System.out.println("tempEDges: " + temp.getEdges());
            for (Vertex vert : subGraph) {
                for (Edge edge : vert.getOutwardEdges()) {
                    Edge out = temp.addEdge(temp.getVertex(vert.getName()), temp.getVertex(edge.other(vert).getName()),
                            edge.getDistance(), edge.getDirection(), edge.getName());
                }
            }
            Vertex subCentroid = centroid(subGraph);
            temp.setCentroid(subCentroid);
            System.out.println("tempE: " + temp.getEdges());
            HashMap<Vertex, Double> distances = temp.distanceFrom(subCentroid);
            System.out.println("distances: " + distances);
            System.out.println("centroid: " + subCentroid);
            Double maxDist = Collections.max(distances.values());
            System.out.println("maxDist: " + maxDist);
            toReturn.put(temp, maxDist);
        }
        fittingRadii = toReturn;
        return toReturn;
    }

    public int[] dimensions() {
        HashMap<Graph, Double> fitMap = (this.fittingRadii == null) ? getFittingRadii() : fittingRadii;
        System.out.println("fit" + fitMap);
        int subGraphCount = fitMap.size();
        if (subGraphCount < 3) {
            System.out.println("dims" + Arrays.toString(new int[]{(int) Math.round(fitMap.values().stream().mapToDouble(x -> x).sum()),
                    (int) Math.round(Collections.max(fitMap.values()))}));
            return new int[]{(int) Math.round(fitMap.values().stream().mapToDouble(x -> x).sum()),
                    (int) Math.round(Collections.max(fitMap.values()))};
        }
        double squareRoot = Math.sqrt(subGraphCount);
        System.out.println("dims" + Arrays.toString(new int[]{(int) Math.round(Collections.max(fitMap.values()) * squareRoot),
                (int) Math.round(Collections.max(fitMap.values()) * squareRoot)}));
        return new int[]{(int) Math.round(Collections.max(fitMap.values()) * squareRoot),
                (int) Math.round(Collections.max(fitMap.values()) * squareRoot)};

    }

    public int getWidth() {
        return dimensions()[0];
    }

    public int getHeight() {
        return dimensions()[1];
    }

    public Vertex getCentroid() {
        return centroid;
    }

    public void setCentroid(Vertex centroid) {
        this.centroid = centroid;
    }
}

