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

    private static final StringBuilder SB = new StringBuilder();
    private static final String EDGE = "->";
    private static String path;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Program expects a path to graph definition file!");
            return;
        }
        Graph g = Graph.fromFile(args[0]);
        boolean verbose = false;
        int longestPath = findLongestPath(g, verbose);
        if (verbose) {
            System.out.println(path);
        }
        System.out.println(longestPath);
    }

    private static int findLongestPath(Graph g, boolean verbose) {
        int vertices = g.getVertices();
        int[][] adjacencyM = g.getAdjacencyM();
        int longestPath = 0;
        BitSet visited = new BitSet(vertices);
        for (int i = 0; i < vertices; i++) {
            int dfsPathLength = dfs(i, vertices, adjacencyM, visited, verbose);
            if (dfsPathLength > longestPath) {
                longestPath = dfsPathLength;
                if (verbose) {
                    SB.setLength(SB.length() - EDGE.length());
                    path = SB.toString();
                }
            }
            if (visited.cardinality() == vertices) {
                // if every vertex has been visited i.e Hamiltonian path
                break;
            } else { // reset visited set
                visited.clear();
                if (verbose) {
                    SB.setLength(0);
                }
            }
        }
        return longestPath;
    }

    private static int dfs(int i, int vertices, int[][] adjacencyM, BitSet visited, boolean verbose) {
        visited.set(i);
        if (verbose) {
            SB.append(i + 1).append("->");
        }
        for (int j = 0; j < vertices; j++) {
            if (!visited.get(j) && adjacencyM[i][j] == 1) {
                return 1 + dfs(j, vertices, adjacencyM, visited, verbose);
            }
        }
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
