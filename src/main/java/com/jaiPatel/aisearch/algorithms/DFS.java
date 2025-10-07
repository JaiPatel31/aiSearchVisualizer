package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import java.util.*;

public class DFS extends AbstractSearchAlgorithm {

    private Deque<Node> stack;
    private Set<Node> explored;
    private Map<Node, Node> parentMap;
    private Graph graph;
    private Node start, goal;
    private SearchObserver observer;

    private boolean initialized = false, finished = false;
    private int nodesGenerated = 0, maxFrontierSize = 0, maxFootprintSize = 0;
    private long startTime, beforeMem;

    @Override
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        this.graph = graph; this.start = start; this.goal = goal; this.observer = observer;
        stack = new ArrayDeque<>(); explored = new HashSet<>(); parentMap = new HashMap<>();
        stack.push(start); parentMap.put(start, null);
        nodesGenerated = 1; nodesExpanded = 0; maxFrontierSize = 1;
        startTime = System.nanoTime();
        beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        initialized = true; finished = false;
    }

    @Override
    public boolean step() {
        if (!initialized || finished || stack.isEmpty()) return false;

        Node current = stack.pop();
        explored.add(current); nodesExpanded++;
        notifyObserver(observer, current, stack, explored, 0, explored.size(), 0, 0, 0);

        if (current.equals(goal)) { finishSearch(); return false; }

        List<Node> neighbors = new ArrayList<>();
        for (var edge : graph.getNeighbors(current)) neighbors.add(edge.getTo());
        Collections.reverse(neighbors);

        for (Node neighbor : neighbors) {
            if (!explored.contains(neighbor) && !stack.contains(neighbor)) {
                stack.push(neighbor);
                parentMap.put(neighbor, current);
                nodesGenerated++;
            }
        }

        maxFrontierSize = Math.max(maxFrontierSize, stack.size());
        maxFootprintSize = Math.max(maxFootprintSize, stack.size() + explored.size());
        return !stack.isEmpty();
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


    @Override public boolean isFinished() { return finished || stack.isEmpty(); }
}

