package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import com.jaiPatel.aisearch.heuristics.*;
import java.util.*;

/**
 * Greedy Best-First Search with heuristic tracking and observer updates.
 */
public class BestFirstSearch extends AbstractSearchAlgorithm {

    private final Heuristic heuristic;
    private Graph graph;
    private Node start, goal;
    private SearchObserver observer;

    private PriorityQueue<Node> frontier;
    private Set<Node> frontierSet, explored;
    private Map<Node, Node> parentMap;
    private Map<Node, Double> hScores;

    private boolean initialized = false, finished = false;
    private int nodesGenerated = 0, maxFrontierSize = 0;
    private long startTime, beforeMem;

    public BestFirstSearch(Heuristic heuristic) { this.heuristic = heuristic; }

    @Override
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        this.graph = graph;
        this.start = start;
        this.goal = goal;
        this.observer = observer;

        hScores = new HashMap<>();
        frontier = new PriorityQueue<>(Comparator.comparingDouble(n -> heuristic.estimate(n, goal)));
        frontierSet = new HashSet<>();
        explored = new HashSet<>();
        parentMap = new HashMap<>();

        frontier.add(start);
        frontierSet.add(start);
        parentMap.put(start, null);
        hScores.put(start, heuristic.estimate(start, goal));

        nodesGenerated = 1;
        nodesExpanded = 0;
        maxFrontierSize = 1;
        startTime = System.nanoTime();
        beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        initialized = true;
        finished = false;
    }

    @Override
    public boolean step() {
        if (!initialized || finished || frontier.isEmpty()) return false;

        Node current = frontier.poll();
        frontierSet.remove(current);
        explored.add(current);
        nodesExpanded++;

        double h = heuristic.estimate(current, goal);
        hScores.put(current, h);
        notifyObserver(observer, current, frontier, explored, nodesExpanded, 0, 0, h, h);

        if (current.equals(goal)) {
            finishSearch();
            return false;
        }

        for (var edge : graph.getNeighbors(current)) {
            Node neighbor = edge.getTo();
            if (!explored.contains(neighbor) && !frontierSet.contains(neighbor)) {
                frontier.add(neighbor);
                frontierSet.add(neighbor);
                parentMap.put(neighbor, current);
                hScores.put(neighbor, heuristic.estimate(neighbor, goal));
                nodesGenerated++;
            }
        }

        maxFrontierSize = Math.max(maxFrontierSize, frontier.size());
        return !frontier.isEmpty();
    }

    private void finishSearch() {
        finished = true;
        List<Node> path = reconstructPath(parentMap, goal);
        double totalCost = calculatePathCost(graph, path);
        int solutionDepth = path.size() - 1;

        long endTime = System.nanoTime();
        long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long runtimeMs = (endTime - startTime) / 1_000_000;
        long memoryBytes = afterMem - beforeMem;

        if (observer != null) {
            observer.onFinish(path, nodesExpanded, nodesGenerated, maxFrontierSize,
                    totalCost, solutionDepth, runtimeMs, memoryBytes);
        }
    }

    public double getHeuristicValue(Node n) { return hScores.getOrDefault(n, 0.0); }

    @Override public boolean isFinished() { return finished || frontier.isEmpty(); }
}




