package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import java.util.*;

/**
 * Depth-First Search (DFS) for live visualization.
 * Implements the DFS algorithm for pathfinding and graph traversal.
 */
public class DFS extends AbstractSearchAlgorithm {

    // Stack representing the frontier (open list)
    private Deque<Node> stack;

    // Set of explored (visited) nodes
    private Set<Node> explored;

    // Map of nodes to their parent nodes
    private Map<Node, Node> parentMap;

    // The graph to search
    private Graph graph;

    // The start and goal nodes
    private Node start, goal;

    // Observer to notify during the search
    private SearchObserver observer;

    // Flags to track initialization and completion
    private boolean initialized = false, finished = false;

    // Tracks the number of nodes generated and the maximum frontier size
    private int nodesGenerated = 0, maxFrontierSize = 0, maxFootprintSize = 0;

    // Tracks the start time and memory usage before the search
    private long startTime, beforeMem;

    /**
     * Initializes the DFS algorithm with the given graph, start and goal nodes, and observer.
     *
     * @param graph    The graph to search
     * @param start    The start node
     * @param goal     The goal node
     * @param observer The observer to notify during the search
     */
    @Override
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        this.graph = graph;
        this.start = start;
        this.goal = goal;
        this.observer = observer;

        stack = new ArrayDeque<>();
        explored = new HashSet<>();
        parentMap = new HashMap<>();

        stack.push(start);
        parentMap.put(start, null);

        nodesGenerated = 1;
        nodesExpanded = 0;
        maxFrontierSize = 1;

        startTime = System.nanoTime();
        beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        initialized = true;
        finished = false;
    }

    /**
     * Performs a single step of the DFS algorithm.
     *
     * @return True if there are more steps remaining, false if the search is finished
     */
    @Override
    public boolean step() {
        if (!initialized || finished || stack.isEmpty()) return false;

        Node current = stack.pop();
        explored.add(current);
        nodesExpanded++;

        notifyObserver(observer, current, stack, explored, 0, explored.size(), 0, 0, 0);

        if (current.equals(goal)) {
            finishSearch();
            return false;
        }

        // Collect neighbors and reverse them to maintain DFS order
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
        maxFootprintSize = Math.max(maxFootprintSize, stack.size() + explored.size());
        return !stack.isEmpty();
    }

    /**
     * Completes the search and notifies the observer with the results.
     */
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

    /**
     * Checks if the search is finished.
     *
     * @return True if the search is finished, false otherwise
     */
    @Override
    public boolean isFinished() {
        return finished || stack.isEmpty();
    }
}