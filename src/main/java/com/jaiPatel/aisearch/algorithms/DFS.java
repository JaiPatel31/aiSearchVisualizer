package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;

import java.util.*;

public class DFS implements SearchAlgorithm {
    @Override
    public SearchResult solve(Graph graph, Node start, Node goal) {
        Deque<Node> stack = new ArrayDeque<>();
        Map<Node, Node> parentMap = new HashMap<>();
        Set<Node> explored = new HashSet<>();

        stack.push(start);
        explored.add(start);

        int nodesExpanded = 0;

        while (!stack.isEmpty()) {
            Node current = stack.pop();
            nodesExpanded++;

            if (current.equals(goal)) {
                // reconstruct path
                List<Node> path = new ArrayList<>();
                for (Node n = goal; n != null; n = parentMap.get(n)) {
                    path.add(n);
                }
                Collections.reverse(path);
                return new SearchResult(path, path.size() - 1, nodesExpanded, explored.size());
            }

            // Push neighbors in reverse order (so left-most gets explored first)
            List<Node> neighbors = new ArrayList<>();
            for (var edge : graph.getNeighbors(current)) {
                neighbors.add(edge.getTo());
            }
            Collections.reverse(neighbors);

            for (Node neighbor : neighbors) {
                if (!explored.contains(neighbor)) {
                    stack.push(neighbor);
                    explored.add(neighbor);
                    parentMap.put(neighbor, current);
                }
            }
        }

        // If no path found
        return new SearchResult(Collections.emptyList(), Double.POSITIVE_INFINITY, nodesExpanded, explored.size());
    }
}
