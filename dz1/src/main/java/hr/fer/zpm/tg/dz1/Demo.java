package hr.fer.zpm.tg.dz1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Demo {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Program expects a path to graph definition file !");
            return;
        }

        Graph g = Graph.fromFile(args[0]);
        System.out.println();
    }

    private static int findLongestPath(Graph g) {
        return 0;
    }

    private static final class Graph {

        private final int vertices;
        private final int[][] adjacencyM;

        private Graph(int vertices, int[][] adjacencyM) {
            this.vertices = vertices;
            this.adjacencyM = adjacencyM;
        }

        public int getVertices() {
            return vertices;
        }

        public int[][] getAdjacencyM() {
            return Arrays.stream(adjacencyM)
                    .map(int[]::clone)
                    .toArray(int[][]::new);
        }

        public static Graph fromFile(String file) throws IOException {
            int vertices = 0;
            int[][] neighbourhoodM = null;
            boolean firstRead = true;
            int row = 0;
            try (BufferedReader br = Files.newBufferedReader(Paths.get(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.isEmpty()) continue;
                    line = line.strip();
                    if (firstRead) {
                        vertices = Integer.parseInt(line);
                        neighbourhoodM = new int[vertices][vertices];
                        firstRead = false;
                    } else {
                        neighbourhoodM[row++] = Arrays.stream(line.split("\\s+"))
                                .mapToInt(Integer::parseInt)
                                .toArray();
                        if (row == vertices) break;
                    }
                }
            }
            return new Graph(vertices, neighbourhoodM);
        }
    }

}
