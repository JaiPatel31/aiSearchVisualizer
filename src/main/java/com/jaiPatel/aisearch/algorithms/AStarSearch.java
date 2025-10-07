package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import com.jaiPatel.aisearch.heuristics.*;
import java.util.*;

public class AStarSearch extends AbstractSearchAlgorithm {

    private final Heuristic heuristic;
    private Graph graph; private Node start, goal; private SearchObserver observer;

    private PriorityQueue<Node> frontier;
    private Set<Node> frontierSet, explored;
    private Map<Node, Node> parentMap;
    private Map<Node, Double> gScores;

    private boolean initialized = false, finished = false;
    private int nodesGenerated = 0, maxFrontierSize = 0, maxFootprintSize = 0;
    private long startTime, beforeMem;

    public AStarSearch(Heuristic heuristic) { this.heuristic = heuristic; }

    @Override
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        this.graph = graph; this.start = start; this.goal = goal; this.observer = observer;
        parentMap = new HashMap<>(); gScores = new HashMap<>();
        explored = new HashSet<>(); frontierSet = new HashSet<>();
        frontier = new PriorityQueue<>(Comparator.comparingDouble(
                n -> gScores.getOrDefault(n, Double.POSITIVE_INFINITY) + heuristic.estimate(n, goal)));

        parentMap.put(start, null);
        gScores.put(start, 0.0);
        frontier.add(start);
        frontierSet.add(start);

        nodesGenerated = 1; nodesExpanded = 0; maxFrontierSize = 1;
        startTime = System.nanoTime();
        beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        initialized = true; finished = false;
    }

    @Override
    public boolean step() {
        if (!initialized || finished || frontier.isEmpty()) return false;

        Node current = frontier.poll();
        frontierSet.remove(current);
        explored.add(current);
        nodesExpanded++;

        double g = gScores.getOrDefault(current, 0.0);
        double h = heuristic.estimate(current, goal);
        double f = g + h;
        notifyObserver(observer, current, frontier, explored, g, explored.size(), g, h, f);

        if (current.equals(goal)) { finishSearch(); return false; }

        for (var edge : graph.getNeighbors(current)) {
            Node neighbor = edge.getTo();
            double tentativeG = g + edge.getCost();
            if (!gScores.containsKey(neighbor) || tentativeG < gScores.get(neighbor)) {
                gScores.put(neighbor, tentativeG);
                parentMap.put(neighbor, current);
                if (!frontierSet.contains(neighbor)) {
                    frontier.add(neighbor);
                    frontierSet.add(neighbor);
                    nodesGenerated++;
                }
            }
        }

        maxFrontierSize = Math.max(maxFrontierSize, frontier.size());
        maxFootprintSize = Math.max(maxFootprintSize, frontier.size() + explored.size());
        return !frontier.isEmpty();
    }

    private void finishSearch() {
        finished = true;

        List<Node> path = reconstructPath(parentMap, goal);
        double totalCost = gScores.getOrDefault(goal, Double.POSITIVE_INFINITY);
        int solutionDepth = path.size() - 1;

        long endTime = System.nanoTime();
        long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        long runtimeMs = (endTime - startTime) / 1_000_000;
        long memoryBytes = afterMem - beforeMem;

        if (observer != null) {
            observer.onFinish(
                    path,
                    nodesExpanded,
                    nodesGenerated,
                    maxFrontierSize,
                    totalCost,
                    solutionDepth,
                    runtimeMs,
                    memoryBytes
            );
        }
    }


    @Override public boolean isFinished() { return finished || frontier.isEmpty(); }
}

