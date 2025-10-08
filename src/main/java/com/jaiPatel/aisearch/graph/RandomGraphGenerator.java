package com.jaiPatel.aisearch.graph;

import java.util.*;

/**
 * Utility class for generating random undirected, weighted graphs.
 * <p>
 * Each generated graph contains a specified number of nodes, random coordinates for each node,
 * and random edges with weights between given bounds. The branching factor controls the average
 * number of edges per node. All edges are undirected and have random weights.
 */
public class RandomGraphGenerator {

    /**
     * Generates a random undirected, weighted graph.
     * <p>
     * Each node is assigned random coordinates for heuristic and visualization purposes.
     * Edges are randomly created between nodes, with weights in the specified range.
     * No self-loops are created. All edges are bidirectional.
     *
     * @param n              Number of nodes in the graph
     * @param branchingFactor Average number of edges per node
     * @param minWeight      Minimum edge weight
     * @param maxWeight      Maximum edge weight
     * @param seed           Random seed for reproducibility
     * @return The generated Graph object
     */
    public static Graph generate(int n, int branchingFactor, int minWeight, int maxWeight, long seed) {
        Graph graph = new Graph();
        Random rand = new Random(seed);

        // Step 1: Create nodes
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            // assign random x,y coordinates (for heuristics & visualization)
            int x = rand.nextInt(1000);
            int y = rand.nextInt(1000);
            Node node = new Node("N" + i, x, y);
            graph.addNode(node);
            nodes.add(node);
        }

        // Step 2: Create edges
        for (Node from : nodes) {
            // ensure branching factor ~ b
            int edges = rand.nextInt(branchingFactor) + 1;
            for (int j = 0; j < edges; j++) {
                Node to = nodes.get(rand.nextInt(n));
                if (to == from) continue; // no self-loops

                // assign random weight
                int weight = rand.nextInt(maxWeight - minWeight + 1) + minWeight;

                // add undirected edge
                graph.addEdge(from, to, weight);
                graph.addEdge(to, from, weight);
            }
        }

        return graph;
    }
}
