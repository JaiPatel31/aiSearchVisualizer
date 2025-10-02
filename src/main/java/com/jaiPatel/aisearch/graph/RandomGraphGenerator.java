package com.jaiPatel.aisearch.graph;

import java.util.*;

public class RandomGraphGenerator {

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

