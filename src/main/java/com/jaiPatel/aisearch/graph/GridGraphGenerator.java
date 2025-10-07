package com.jaiPatel.aisearch.graph;

import java.util.*;

/**
 * Utility to generate a grid-world graph for pathfinding visualization.
 * Supports adjustable size, obstacle density, connectivity, and weighted edges.
 */
public class GridGraphGenerator {

    public static Graph generateGrid(
            int size,              // N x N
            double obstacleDensity, // e.g. 0.2 = 20% obstacles
            boolean diagonal,       // true = 8-connectivity, false = 4-connectivity
            boolean weighted,       // true = random edge costs
            long seed               // reproducible RNG
    ) {
        Random rand = new Random(seed);
        Graph graph = new Graph();

        // --- Create nodes ---
        Node[][] nodes = new Node[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Node n = new Node(x + "," + y,x,y);
                // randomly mark as blocked
                boolean blocked = rand.nextDouble() < obstacleDensity;
                n.setAttribute("blocked", blocked);
                nodes[x][y] = n;
                graph.addNode(n);
            }
        }

        // --- Add edges based on connectivity ---
        int[][] directions4 = {{1,0},{-1,0},{0,1},{0,-1}};
        int[][] directions8 = {
                {1,0},{-1,0},{0,1},{0,-1},
                {1,1},{-1,1},{1,-1},{-1,-1}
        };
        int[][] directions = diagonal ? directions8 : directions4;

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Node from = nodes[x][y];
                if (Boolean.TRUE.equals(from.getAttribute("blocked"))) continue;

                for (int[] d : directions) {
                    int nx = x + d[0], ny = y + d[1];
                    if (nx < 0 || ny < 0 || nx >= size || ny >= size) continue;

                    Node to = nodes[nx][ny];
                    if (Boolean.TRUE.equals(to.getAttribute("blocked"))) continue;

                    double cost;
                    if (!weighted)
                        cost = (d[0] == 0 || d[1] == 0) ? 1.0 : Math.sqrt(2); // diagonals cost √2
                    else
                        cost = 1.0 + rand.nextDouble() * 9.0; // random [1,10)

                    graph.addEdge(from, to, cost);
                }
            }
        }

        return graph;
    }
}
