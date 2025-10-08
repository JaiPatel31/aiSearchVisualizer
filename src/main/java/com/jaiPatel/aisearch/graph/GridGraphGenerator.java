package com.jaiPatel.aisearch.graph;

import java.util.*;

/**
 * Grid-world generator that guarantees a valid path between start and goal.
 * Supports 4/8 connectivity and weighted or unit costs.
 */
public class GridGraphGenerator {

    public static Graph generateGrid(
            int size,
            double obstacleDensity,
            boolean diagonal,
            boolean weighted,
            long seed
    ) {
        Random rand = new Random(seed);
        Graph graph;
        int attempts = 0;
        int maxAttempts = 30;

        do {
            graph = new Graph();
            Node[][] nodes = new Node[size][size];

            // --- create nodes ---
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    Node n = new Node(x + "," + y, x, y);
                    boolean blocked = rand.nextDouble() < obstacleDensity;
                    n.setAttribute("blocked", blocked);
                    nodes[x][y] = n;
                    graph.addNode(n);
                }
            }

            // --- add edges ---
            int[][] dirs4 = {{1,0},{-1,0},{0,1},{0,-1}};
            int[][] dirs8 = {
                    {1,0},{-1,0},{0,1},{0,-1},
                    {1,1},{-1,1},{1,-1},{-1,-1}
            };
            int[][] dirs = diagonal ? dirs8 : dirs4;

            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    Node from = nodes[x][y];
                    if (Boolean.TRUE.equals(from.getAttribute("blocked"))) continue;
                    for (int[] d : dirs) {
                        int nx = x + d[0], ny = y + d[1];
                        if (nx < 0 || ny < 0 || nx >= size || ny >= size) continue;
                        Node to = nodes[nx][ny];
                        if (Boolean.TRUE.equals(to.getAttribute("blocked"))) continue;

                        double cost = weighted
                                ? 1.0 + rand.nextDouble() * 9.0
                                : ((d[0] == 0 || d[1] == 0) ? 1.0 : Math.sqrt(2));
                        graph.addEdge(from, to, cost);
                    }
                }
            }

            Node start = nodes[0][0];
            Node goal  = nodes[size - 1][size - 1];
            start.setAttribute("blocked", false);
            goal.setAttribute("blocked", false);

            attempts++;
            if (isReachable(graph, start, goal, diagonal))
                return graph;

        } while (attempts < maxAttempts);

        throw new RuntimeException("Failed to generate solvable grid after " + maxAttempts + " attempts");
    }

    /** Connectivity-aware reachability check */
    private static boolean isReachable(Graph g, Node start, Node goal, boolean diagonal) {
        if (start == null || goal == null) return false;

        Queue<Node> q = new LinkedList<>();
        Set<Node> visited = new HashSet<>();
        q.add(start);
        visited.add(start);

        while (!q.isEmpty()) {
            Node current = q.poll();
            if (current.equals(goal)) return true;
            for (var e : g.getNeighbors(current)) {
                Node n = e.getTo();
                if (!visited.contains(n)) {
                    visited.add(n);
                    q.add(n);
                }
            }
        }
        return false;
    }

    public static boolean isSolvable(Graph g, Node start, Node goal) {
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Node n = queue.poll();
            if (n.equals(goal)) return true;
            for (var edge : g.getEdgesFrom(n)) {
                Node neighbor = edge.getTo();
                if (!Boolean.TRUE.equals(neighbor.getAttribute("blocked")) && visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }
        return false;
    }
}
