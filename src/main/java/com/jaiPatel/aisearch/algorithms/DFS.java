package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;

import java.util.*;

import java.util.*;

public class DFS extends AbstractSearchAlgorithm {

    @Override
    public SearchResult solve(Graph graph, Node start, Node goal, SearchObserver observer) {
        long startTime = System.nanoTime();
        long beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        Deque<Node> stack = new ArrayDeque<>();
        Map<Node, Node> parentMap = new HashMap<>();
        Set<Node> explored = new HashSet<>();

        stack.push(start);
        parentMap.put(start, null);

        int nodesExpanded = 0;
        int nodesGenerated = 0;
        int maxFrontierSize = stack.size();

        while (!stack.isEmpty()) {
            checkControl(); // ✅ pause/resume/stop hook

            Node current = stack.pop();
            nodesExpanded++;
            explored.add(current);

            if (observer != null) {
                observer.onStep(current, stack, explored);
            }

            if (current.equals(goal)) {
                long endTime = System.nanoTime();
                long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                List<Node> path = reconstructPath(parentMap, goal);
                int solutionDepth = path.size() - 1;

                double totalCost = 0.0;
                for (int i = 0; i < path.size() - 1; i++) {
                    Node from = path.get(i);
                    Node to = path.get(i + 1);
                    totalCost += graph.getEdgeWeight(from, to); // <-- sum edge weights
                }
                return new SearchResult(
                        path,
                        totalCost, // cost = depth (unit weights)
                        nodesExpanded,
                        nodesGenerated,
                        explored.size(),
                        maxFrontierSize,
                        solutionDepth,
                        (endTime - startTime) / 1_000_000, // ms
                        (afterMem - beforeMem) // bytes
                );
            }

            // ✅ Push neighbors in reverse order so left-most expands first
            List<Node> neighbors = new ArrayList<>();
            for (var edge : graph.getNeighbors(current)) {
                neighbors.add(edge.getTo());
            }
            Collections.reverse(neighbors);

            for (Node neighbor : neighbors) {
                if (!explored.contains(neighbor) && !stack.contains(neighbor)) {
                    stack.push(neighbor);
                    parentMap.put(neighbor, current);
                    nodesGenerated++;
                }
            }

            maxFrontierSize = Math.max(maxFrontierSize, stack.size());
        }

        long endTime = System.nanoTime();
        long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // If no path found
        return new SearchResult(
                Collections.emptyList(),
                Double.POSITIVE_INFINITY,
                nodesExpanded,
                nodesGenerated,
                explored.size(),
                maxFrontierSize,
                -1,
                (endTime - startTime) / 1_000_000,
                (afterMem - beforeMem)
        );
    }

    private List<Node> reconstructPath(Map<Node, Node> parent, Node goal) {
        List<Node> path = new ArrayList<>();
        for (Node n = goal; n != null; n = parent.get(n)) {
            path.add(n);
        }
        Collections.reverse(path);
        return path;
    }
}
