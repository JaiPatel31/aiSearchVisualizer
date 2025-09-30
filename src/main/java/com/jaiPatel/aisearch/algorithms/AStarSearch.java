package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;
import com.jaiPatel.aisearch.graph.Edge;
import com.jaiPatel.aisearch.heuristics.Heuristic;

import java.util.*;

public class AStarSearch implements SearchAlgorithm {

    private final Heuristic heuristic;

    public AStarSearch(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public SearchResult solve(Graph graph, Node start, Node goal) {
        // Priority queue ordered by f(n) = g(n) + h(n)
        PriorityQueue<Node> frontier = new PriorityQueue<>(
                Comparator.comparingDouble(n -> gScore.getOrDefault(n, Double.POSITIVE_INFINITY)
                        + heuristic.estimate(n, goal))
        );

        Map<Node, Node> parentMap = new HashMap<>();
        gScore = new HashMap<>();
        Set<Node> explored = new HashSet<>();

        gScore.put(start, 0.0);
        frontier.add(start);

        int nodesExpanded = 0;

        while (!frontier.isEmpty()) {
            Node current = frontier.poll();
            nodesExpanded++;

            if (current.equals(goal)) {
                // Reconstruct path
                List<Node> path = new ArrayList<>();
                double totalCost = gScore.get(current);

                for (Node n = goal; n != null; n = parentMap.get(n)) {
                    path.add(n);
                }
                Collections.reverse(path);

                return new SearchResult(path, totalCost, nodesExpanded, explored.size());
            }

            explored.add(current);

            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTo();
                double tentativeG = gScore.get(current) + edge.getCost();

                if (tentativeG < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    parentMap.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);

                    // Only add to frontier if not already explored
                    if (!explored.contains(neighbor)) {
                        frontier.add(neighbor);
                    }
                }
            }
        }

        // No path found
        return new SearchResult(Collections.emptyList(), Double.POSITIVE_INFINITY, nodesExpanded, explored.size());
    }

    // Keep gScore accessible inside comparator
    private Map<Node, Double> gScore = new HashMap<>();
}

