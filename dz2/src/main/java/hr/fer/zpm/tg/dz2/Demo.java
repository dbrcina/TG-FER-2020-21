package hr.fer.zpm.tg.dz2;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;

public class Demo {

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            final long start = System.nanoTime();

            @Override
            public void run() {
                System.err.printf(Locale.US, "Runtime: %.6fs%n", (System.nanoTime() - start) * 1e-9);
            }
        }));
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Program expects a path to graph definition file!");
            return;
        }
        Graph g = Graph.fromFile(args[0]);
        int[] coloring = coloring(g);
        boolean isIt4Colored = Arrays.stream(coloring).noneMatch(c -> c > 3);
        System.out.println(isIt4Colored ? 1 : 0);

        if (isIt4Colored) {
            for (int i = 0; i < coloring.length; i++) {
                System.out.printf("Vertex %d -> %d%n", i, coloring[i]);
            }
        }
    }

    private static int[] coloring(Graph g) {
        int vertices = g.vertices;
        int[][] adjacencyMatrix = g.adjacencyM;
        int[] coloring = new int[vertices];
        Arrays.fill(coloring, -1);
        boolean[] availableColors = new boolean[vertices];
        Arrays.fill(availableColors, true);

        // Go through every vertex combination
        for (int v = 0; v < vertices; v++) {
            for (int u = 0; u < vertices; u++) {
                // Skip:
                //  the same vertices,
                //  the ones that are not connected,
                //  the ones that are not colored.
                if (v == u || adjacencyMatrix[v][u] == 0 || coloring[u] == -1) continue;

                // Vertex u has already been colored, so vertex v cannot have the same coloring.
                availableColors[coloring[u]] = false;
            }
            // Find available color for vertex v
            int c;
            for (c = 0; c < vertices; c++) {
                if (availableColors[c]) break;
            }
            coloring[v] = c;
            // Reset available colors.
            Arrays.fill(availableColors, true);
        }

        return coloring;
    }

    private static final class Graph {
        private final int vertices;
        private final int[][] adjacencyM;

        private Graph(int vertices, int[][] adjacencyM) {
            this.vertices = vertices;
            this.adjacencyM = adjacencyM;
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
