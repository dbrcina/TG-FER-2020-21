package hr.fer.zpm.tg.dz1;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Locale;

public class Demo {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            final long start = System.nanoTime();

            @Override
            public void run() {
                System.err.printf(Locale.US, "Runtime: %.5fs%n", (System.nanoTime() - start) * 1e-9);
            }
        }));
    }

    private static boolean isHamiltonian;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Program expects a path to graph definition file!");
            return;
        }
        Graph g = Graph.fromFile(args[0]);
        int longestPath = findLongestPath(g);
        System.out.println(longestPath);
    }

    private static int findLongestPath(Graph g) {
        int vertices = g.getVertices();
        int[][] adjacencyM = g.getAdjacencyM();
        int longestPath = 0;
        BitSet visited = new BitSet(vertices);
        for (int i = 0; i < vertices; i++) {
            longestPath = Math.max(longestPath, dfs(i, vertices, adjacencyM, visited));
            // break if graph is Hamiltonian
            if (isHamiltonian) break;
            // reset visited set
            visited.clear();
        }
        return longestPath;
    }

    private static int dfs(int i, int vertices, int[][] adjacencyM, BitSet visited) {
        visited.set(i);
        isHamiltonian = visited.cardinality() == vertices;
        int longestPath = 0;
        for (int j = 0; j < vertices && !isHamiltonian; j++) {
            if (!visited.get(j) && adjacencyM[i][j] == 1) {
                longestPath = Math.max(longestPath, 1 + dfs(j, vertices, adjacencyM, (BitSet) visited.clone()));
            }
        }
        return longestPath;
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
            return adjacencyM;
        }

        public static Graph fromFile(String file) throws IOException {
            int vertices = 0;
            int[][] adjacencyM = null;
            boolean firstRead = true;
            int row = 0;
            try (BufferedReader br = Files.newBufferedReader(Paths.get(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.isEmpty()) continue;
                    line = line.strip();
                    if (firstRead) {
                        vertices = Integer.parseInt(line);
                        adjacencyM = new int[vertices][vertices];
                        firstRead = false;
                    } else {
                        adjacencyM[row++] = Arrays.stream(line.split("\\s+"))
                                .mapToInt(Integer::parseInt)
                                .toArray();
                        if (row == vertices) break;
                    }
                }
            }
            return new Graph(vertices, adjacencyM);
        }
    }

}
