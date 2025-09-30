package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;

import java.util.*;

public class BFS implements SearchAlgorithm {
    @Override
    public SearchResult solve(Graph graph, Node start, Node goal) {
        Queue<Node> frontier = new LinkedList<>();
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

        return new SearchResult(Collections.emptyList(), Double.POSITIVE_INFINITY, nodesExpanded, explored.size());
    }
}
