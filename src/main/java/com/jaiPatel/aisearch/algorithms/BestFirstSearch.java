package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;
import com.jaiPatel.aisearch.heuristics.Heuristic;

import java.util.*;

import java.util.*;

public class BestFirstSearch extends AbstractSearchAlgorithm {

    private final Heuristic heuristic;

    public BestFirstSearch(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public SearchResult solve(Graph graph, Node start, Node goal, SearchObserver observer) {
        long startTime = System.nanoTime();
        long beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        PriorityQueue<Node> frontier = new PriorityQueue<>(
                Comparator.comparingDouble(n -> heuristic.estimate(n, goal))
        );

        Map<Node, Node> parentMap = new HashMap<>();
        Set<Node> explored = new HashSet<>();

        frontier.add(start);
        parentMap.put(start, null);

        int nodesExpanded = 0;
        int nodesGenerated = 1; // start node counts as generated
        int maxFrontierSize = frontier.size();

        while (!frontier.isEmpty()) {
            checkControl(); // pause/resume/stop

            Node current = frontier.poll();
            nodesExpanded++;
            explored.add(current);

            if (observer != null) {
                List<Node> frontierNodes = new ArrayList<>(frontier);
                observer.onStep(current, frontierNodes, explored);
            }

            if (current.equals(goal)) {
                List<Node> path = reconstructPath(parentMap, goal);
                long endTime = System.nanoTime();
                long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                // Calculate total cost along the path
                double totalCost = 0.0;
                for (int i = 0; i < path.size() - 1; i++) {
                    Node from = path.get(i);
                    Node to = path.get(i + 1);
                    totalCost += graph.getEdgeWeight(from, to);
                }
                return new SearchResult(
                        path,
                        totalCost,
                        nodesExpanded,
                        nodesGenerated,
                        explored.size(),
                        maxFrontierSize,
                        path.size() - 1,
                        (endTime - startTime) / 1_000_000,
                        (afterMem - beforeMem)
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

        // Goal not found
        long endTime = System.nanoTime();
        long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

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

    private List<Node> reconstructPath(Map<Node, Node> parentMap, Node goal) {
        List<Node> path = new ArrayList<>();
        for (Node n = goal; n != null; n = parentMap.get(n)) {
            path.add(n);
        }
        Collections.reverse(path);
        return path;
    }
}

