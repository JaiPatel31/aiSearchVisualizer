package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;
import com.jaiPatel.aisearch.graph.Edge;
import com.jaiPatel.aisearch.heuristics.Heuristic;

import java.util.*;

import java.util.*;

public class AStarSearch extends AbstractSearchAlgorithm {

    private final Heuristic heuristic;

    public AStarSearch(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public SearchResult solve(Graph graph, Node start, Node goal, SearchObserver observer) {
        long startTime = System.nanoTime();
        long beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        Map<Node, Node> parentMap = new HashMap<>();
        Map<Node, Double> gScores = new HashMap<>(); // cost from start to node
        Set<Node> explored = new HashSet<>();

        PriorityQueue<Node> frontier = new PriorityQueue<>(
                Comparator.comparingDouble(n -> gScores.getOrDefault(n, Double.POSITIVE_INFINITY)
                        + heuristic.estimate(n, goal))
        );

        parentMap.put(start, null);
        gScores.put(start, 0.0);
        frontier.add(start);

        int nodesExpanded = 0;
        int nodesGenerated = 1;
        int maxFrontierSize = frontier.size();

        while (!frontier.isEmpty()) {
            checkControl(); // pause/resume/stop

            Node current = frontier.poll();
            nodesExpanded++;
            explored.add(current);

            if (observer != null) {
                observer.onStep(current, new ArrayList<>(frontier), explored);
            }

            if (current.equals(goal)) {
                List<Node> path = reconstructPath(parentMap, goal);
                long endTime = System.nanoTime();
                long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                return new SearchResult(
                        path,
                        gScores.get(goal),
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
                double tentativeG = gScores.get(current) + edge.getCost();

                if (!gScores.containsKey(neighbor) || tentativeG < gScores.get(neighbor)) {
                    gScores.put(neighbor, tentativeG);
                    parentMap.put(neighbor, current);
                    if (!frontier.contains(neighbor)) {
                        frontier.add(neighbor);
                        nodesGenerated++;
                    }
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
