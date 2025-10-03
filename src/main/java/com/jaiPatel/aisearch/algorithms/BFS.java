package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;

import java.util.*;

import java.util.*;

public class BFS extends AbstractSearchAlgorithm {

    @Override
    public SearchResult solve(Graph graph, Node start, Node goal, SearchObserver observer) {
        long startTime = System.nanoTime();
        long beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        Queue<Node> frontier = new LinkedList<>();
        Map<Node, Node> parentMap = new HashMap<>();
        Set<Node> explored = new HashSet<>();

        frontier.add(start);
        parentMap.put(start, null);

        int nodesExpanded = 0;
        int nodesGenerated = 0;
        int maxFrontierSize = frontier.size();

        while (!frontier.isEmpty()) {
            checkControl(); // ✅ pause/resume/stop hook

            Node current = frontier.poll();
            nodesExpanded++;
            explored.add(current);

            if (observer != null) {
                observer.onStep(current, frontier, explored);
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

            for (var edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTo();
                if (!explored.contains(neighbor) && !frontier.contains(neighbor)) {
                    frontier.add(neighbor);
                    parentMap.put(neighbor, current);
                    nodesGenerated++;
                }
            }

            maxFrontierSize = Math.max(maxFrontierSize, frontier.size());
        }

        long endTime = System.nanoTime();
        long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        return new SearchResult(
                Collections.emptyList(),
                Double.POSITIVE_INFINITY,
                nodesExpanded,
                nodesGenerated,
                explored.size(),
                maxFrontierSize,
                -1, // no solution depth
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
