import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        ArrayList<int[]> connections = readEdgeListFile("GA2018-19.txt");
        int[][] adjacencyMatrix = createAdjacencyMatrix(connections);
        printMatrix(adjacencyMatrix);

        Ordering initialOrdering = new Ordering(adjacencyMatrix.length);
        initialOrdering.printOrdering();

        System.out.println(initialOrdering.fitness(adjacencyMatrix));

        SimulatedAnnealing simulatedAnnealing = new SimulatedAnnealing(adjacencyMatrix, initialOrdering, 5, 0.005);

        Ordering finalOrdering = simulatedAnnealing.run();
        System.out.println(finalOrdering.fitness(adjacencyMatrix));
        new VizWindow(adjacencyMatrix, initialOrdering, "Initial");
        new VizWindow(adjacencyMatrix, finalOrdering, "Final");
    }

    public static ArrayList<int[]> readEdgeListFile(String filePath) {
        ArrayList<int[]> connections = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            try {
                String line = br.readLine();

                while (line != null) {
                    String[] lineValues = line.split(" ");
                    int[] connection = new int[2];

                    connection[0] = Integer.parseInt(lineValues[0]);
                    connection[1] = Integer.parseInt(lineValues[1]);

                    connections.add(connection);

                    line = br.readLine();
                }
            } finally {
                br.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return connections;
    }

    public static int[][] createAdjacencyMatrix(ArrayList<int[]> connections) {
        int nbNodes = 0;

        for(int[] connection : connections) {
            for(int node : connection) {
                if(node > nbNodes) {
                    nbNodes = node;
                }
            }
        }

        nbNodes++; // Because node 0 exists

        int[][] adjacencyMatrix = new int[nbNodes][nbNodes];

        for(int[] connection : connections) {
            adjacencyMatrix[connection[0]][connection[1]] = 1;
            adjacencyMatrix[connection[1]][connection[0]] = 1;
        }

        return adjacencyMatrix;
    }

    public static void printMatrix(int[][] matrix) {
        for(int[] row : matrix) {
            printArray(row);
        }
    }

    public static void printArray(int[] array) {
        for(int value: array) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}

class SimulatedAnnealing {
    private Ordering ordering;
    private int[][] adjacencyMatrix;
    private double temperature;
    private double coolingRate;
    private ArrayList<Double> history;

    public SimulatedAnnealing(int[][] adjacencyMatrix, Ordering ordering, double temperature, double coolingRate) {
        this.ordering = ordering;
        this.adjacencyMatrix = adjacencyMatrix;
        this.temperature = temperature;
        this.coolingRate = coolingRate;
        this.history = new ArrayList<>();
    }

    public Ordering run() {
        while(this.temperature > 0) {
            this.iterate();
        }
        return this.ordering;
    }

    private void iterate() {
        Ordering newOrdering = new Ordering(this.ordering);
        for(int i = 0; i<this.temperature; i++) {
            newOrdering.mutate();
        }

        if(newOrdering.fitness(this.adjacencyMatrix) < this.ordering.fitness(this.adjacencyMatrix)) {
            this.ordering = new Ordering(newOrdering);
        }
        this.temperature = this.temperature - this.coolingRate;
        System.out.println(this.ordering.fitness(this.adjacencyMatrix));
    }

    public void printHistory() {
        for(Double fitness : this.history) {
            System.out.println(fitness);
        }
    }
}

class Ordering {
    private ArrayList<Node> nodes;

    public Ordering(int nbNodes) {
        this.nodes = new ArrayList<>();
        this.setInitialOrdering(nbNodes);
    }

    public Ordering(Ordering ordering) {
        this.nodes = new ArrayList<>(ordering.nodes);
    }

    private void setInitialOrdering(int nbNodes) {
        for(int i = 0; i<nbNodes; i++) {
            Node node = new Node();
            node.number = i;
            this.nodes.add(node);
        }

        Collections.shuffle(this.nodes);
    }

    private void setNodeCoordinates() {
        double chunk = 2 * Math.PI / this.nodes.size();

        for(int i = 0; i < this.nodes.size(); i++) {
            this.nodes.get(i).x = Math.cos(i * chunk);
            this.nodes.get(i).y = Math.sin(i * chunk);
        }
    }

    public double fitness(int[][] adjacencyMatrix) {
        double totalDistance = 0;

        this.setNodeCoordinates();

        for(int i = 0; i < this.nodes.size(); i++) {
            for(int j = 0; j < this.nodes.size(); j++) {
                if(adjacencyMatrix[i][j] == 1) {
                    Node a = this.findNodeNumber(i);
                    Node b = this.findNodeNumber(j);

                    totalDistance += a.distanceTo(b);
                }
            }
        }

        return totalDistance/2;
    }

    public void mutate() {
        int a, b;
        do {
            a = (int) Math.floor(Math.random() * this.nodes.size());
            b = (int) Math.floor(Math.random() * this.nodes.size());
        } while (a == b);

        Node temp = this.nodes.get(a);
        this.nodes.set(a, this.nodes.get(b));
        this.nodes.set(b, temp);
    }

    private Node findNodeNumber(int nb) {
        for(Node node : this.nodes) {
            if(node.number == nb) {
                return node;
            }
        }
        return null;
    }

    public ArrayList<Node> getOrdering() {
        return this.nodes;
    }

    public void printOrdering() {
        for(Node node : this.nodes) {
            System.out.print(node.number + " ");
        }

        System.out.println();
    }
}

class Node {
    int number;
    double x;
    double y;

    public double distanceTo(Node b) {
        return Math.sqrt( Math.pow(b.x - this.x, 2) + Math.pow(b.y - this.y, 2) );
    }
}

class VizWindow extends JFrame {
    int adj [][];
    int v = 0;
    Ordering ordering;
    double chunk;

    public VizWindow(int[][] adj, Ordering ordering, String title) {
        this.adj = adj;
        this.ordering = ordering;
        this.chunk = 2*Math.PI/this.ordering.getOrdering().size();
        this.v = this.ordering.getOrdering().size();
        setTitle(title);
        setSize(960, 960);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void paint(Graphics g) {
        int radius = 300;
        int mov = 400;

        double w = v;

        for(int i = 0; i<v; i++) {
            for(int j = i+1; j<v; j++) {
                if(adj[ordering.getOrdering().get(i).number][ordering.getOrdering().get(j).number] == 1) {
                    g.drawLine(
                            (int)(((double) Math.cos(i*chunk))*radius + mov),
                            (int)(((double) Math.sin(i*chunk))*radius + mov),
                            (int)(((double) Math.cos(j*chunk))*radius + mov),
                            (int)(((double) Math.sin(j*chunk))*radius + mov)
                    );
                }
            }
        }
    }
}