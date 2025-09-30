package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;
import com.jaiPatel.aisearch.heuristics.Heuristic;

import java.util.*;

public class BestFirstSearch implements SearchAlgorithm {

    private final Heuristic heuristic;

    public BestFirstSearch(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public SearchResult solve(Graph graph, Node start, Node goal) {
        // Priority queue sorted by h(n)
        PriorityQueue<Node> frontier = new PriorityQueue<>(
                Comparator.comparingDouble(n -> heuristic.estimate(n, goal))
        );

        Map<Node, Node> parentMap = new HashMap<>();
        Set<Node> explored = new HashSet<>();

        frontier.add(start);
        explored.add(start);

        int nodesExpanded = 0;

        while (!frontier.isEmpty()) {
            Node current = frontier.poll();
            nodesExpanded++;

            if (current.equals(goal)) {
                // Reconstruct path
                List<Node> path = new ArrayList<>();
                for (Node n = goal; n != null; n = parentMap.get(n)) {
                    path.add(n);
                }
                Collections.reverse(path);

                // cost = number of steps (edges in path)
                return new SearchResult(path, path.size() - 1, nodesExpanded, explored.size());
            }

            for (var edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTo();
                if (!explored.contains(neighbor)) {
                    frontier.add(neighbor);
                    explored.add(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }

        // If no path was found
        return new SearchResult(Collections.emptyList(), Double.POSITIVE_INFINITY, nodesExpanded, explored.size());
    }
}

