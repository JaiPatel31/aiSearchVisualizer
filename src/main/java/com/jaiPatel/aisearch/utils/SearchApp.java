package com.jaiPatel.aisearch.utils;

import com.jaiPatel.aisearch.algorithms.*;
import com.jaiPatel.aisearch.graph.*;
import com.jaiPatel.aisearch.heuristics.*;

import java.io.IOException;
import java.util.*;

/**
 * Command-line application for running AI search algorithms on various graph datasets.
 * <p>
 * Allows users to select a graph source (preset or random), choose start and goal nodes,
 * select a search algorithm, and view search results including path, cost, and metrics.
 * Supports BFS, DFS, IDDFS, Greedy Best-First Search, and A* algorithms.
 */
public class SearchApp {

    /** Scanner for reading user input from the console. */
    private static final Scanner sc = new Scanner(System.in);

    /**
     * Main entry point for the AI Search Visualizer CLI.
     * <p>
     * Prompts the user to select a graph source, loads the graph, allows selection of start/goal nodes,
     * prompts for algorithm choice, runs the selected search, and displays results.
     *
     * @param args Command-line arguments (unused)
     * @throws Exception If loading files or running the search fails
     */
    public static void main(String[] args) throws Exception {
        System.out.println("=== AI Search Visualizer CLI ===");
        System.out.println("Choose graph source:");
        System.out.println("1. Preset Graph Set 1 (Kansas towns)");
        System.out.println("2. Preset Graph Set 2 (KC Metro 100 cities)");
        System.out.println("3. Random Graph Generator");

        int choice = Integer.parseInt(sc.nextLine());

        Graph graph;
        if (choice == 1) {
            graph = GraphLoaderSet1.load(
                    "src/main/resources/coordinates.csv",
                    "src/main/resources/Adjacencies.txt"
            );
        } else if (choice == 2) {
            graph = GraphLoaderSet2.load(
                    "src/main/resources/KC_Metro_100_Cities___Nodes.csv"
            );
        } else {
            graph = createRandomGraph();
        }

        System.out.println("\n✅ Graph loaded. Total cities: " + graph.getNodes().size());
        System.out.println("Available cities:");
        for (Node node : graph.getNodes()) {
            System.out.print(node.getName() + " ");
        }
        System.out.println("\n");

        System.out.println("Enter start city:");
        String startName = sc.nextLine();
        System.out.println("Enter goal city:");
        String goalName = sc.nextLine();

        Node start = graph.getNode(startName);
        Node goal = graph.getNode(goalName);

        if (start == null || goal == null) {
            System.err.println("❌ Error: One of the cities was not found in this dataset.");
            return;
        }

        // Algorithm selection
        System.out.println("\nChoose algorithm:");
        System.out.println("1. BFS");
        System.out.println("2. DFS");
        System.out.println("3. IDDFS");
        System.out.println("4. Greedy Best-First Search (GBFS)");
        System.out.println("5. A*");

        int algoChoice = Integer.parseInt(sc.nextLine());
        SearchAlgorithm algo;
        Heuristic heuristic = new EuclideanHeuristic(); // default heuristic for GBFS/A*

        switch (algoChoice) {
            case 1 -> algo = new BFS();
            case 2 -> algo = new DFS();
            case 3 -> algo = new IDDFS();
            case 4 -> algo = new BestFirstSearch(heuristic);
            case 5 -> algo = new AStarSearch(heuristic);
            default -> {
                System.err.println("❌ Invalid choice.");
                return;
            }
        }

        // Run search
        SearchResult result;
        if (algo instanceof AbstractSearchAlgorithm) {
            // use observer version
            result = ((AbstractSearchAlgorithm) algo).solve(graph, start, goal, null);
        } else {
            result = algo.solve(graph, start, goal, null);
        }

        // Display results
        System.out.println("\n=== Search Results ===");
        System.out.println("Algorithm: " + algo.getClass().getSimpleName());
        System.out.println("Path: " + result.getPath());
        System.out.println("Cost: " + result.getCost());
        System.out.println("Nodes Expanded: " + result.getNodesExpanded());
        System.out.println("Explored Set Size: " + result.getExploredSize());
    }

    // Helper to create random graph
    private static Graph createRandomGraph() {
        System.out.println("Enter number of nodes (N): ");
        int n = Integer.parseInt(sc.nextLine());

        System.out.println("Enter expected branching factor (b): ");
        int b = Integer.parseInt(sc.nextLine());

        System.out.println("Enter min edge weight: ");
        int minW = Integer.parseInt(sc.nextLine());

        System.out.println("Enter max edge weight: ");
        int maxW = Integer.parseInt(sc.nextLine());

        System.out.println("Enter random seed (integer): ");
        long seed = Long.parseLong(sc.nextLine());

        return RandomGraphGenerator.generate(n, b, minW, maxW, seed);
    }
}
