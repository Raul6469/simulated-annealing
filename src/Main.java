import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ArrayList<int[]> connections = readEdgeListFile("src/edge-list.txt");
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
}
