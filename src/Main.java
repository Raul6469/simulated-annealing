import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ArrayList<int[]> connections = readEdgeListFile("src/edge-list.txt");
        int[][] adjacencyMatrix = createAdjacencyMatrix(connections);
        generateRandomOrdering(adjacencyMatrix.length);
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

    public static int[] generateRandomOrdering(int nbNodes) {
        int[] randomOrdering = new int[nbNodes];

        ArrayList<Integer> nodes = new ArrayList<>();

        for(int i = 0; i<nbNodes; i++) {
            nodes.add(i);
        }

        for(int i = 0; i<nbNodes; i++) {
            int randomIndex = (int) (Math.random() * nodes.size());
            randomOrdering[i] = nodes.remove(randomIndex);
        }

        return randomOrdering;
    }

    public static void printMatrix(int[][] matrix) {
        for(int[] row : matrix) {
            for(int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}
