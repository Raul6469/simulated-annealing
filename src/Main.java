import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        ArrayList<int[]> connections = readEdgeListFile("GA2018-19.txt");
        int[][] adjacencyMatrix = createAdjacencyMatrix(connections);
        printMatrix(adjacencyMatrix);

        Scanner reader = new Scanner(System.in);
        reader.nextLine();

        Ordering initialOrdering = new Ordering(adjacencyMatrix.length);
        initialOrdering.printOrdering();

        System.out.println(initialOrdering.fitness(adjacencyMatrix));
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

class Ordering {
    private ArrayList<Node> nodes;

    public Ordering(int nbNodes) {
        this.nodes = new ArrayList<>();
        this.setInitialOrdering(nbNodes);
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
        double chunk = 2 * Math.PI / 2;

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

    private Node findNodeNumber(int nb) {
        for(Node node : this.nodes) {
            if(node.number == nb) {
                return node;
            }
        }
        return null;
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
