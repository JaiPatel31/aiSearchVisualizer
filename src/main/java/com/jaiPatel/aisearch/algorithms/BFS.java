package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;

import java.util.*;

/**
 * Incremental Breadth-First Search (BFS) for live visualization.
 * Implements the BFS algorithm for pathfinding and graph traversal.
 */
public class BFS extends AbstractSearchAlgorithm {

    // Queue representing the frontier (open list)
    private Queue<Node> frontier;

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
     * Initializes the BFS algorithm with the given graph, start and goal nodes, and observer.
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

        frontier = new LinkedList<>();
        explored = new HashSet<>();
        parentMap = new HashMap<>();

        frontier.add(start);
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
     * Performs a single step of the BFS algorithm.
     *
     * @return True if there are more steps remaining, false if the search is finished
     */
    @Override
    public boolean step() {
        if (!initialized || finished || frontier.isEmpty()) return false;

        Node current = frontier.poll();
        explored.add(current);
        nodesExpanded++;

        notifyObserver(observer, current, frontier, explored, 0, explored.size(), 0, 0, 0);

        if (current.equals(goal)) {
            finishSearch();
            return false;
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
        maxFootprintSize = Math.max(maxFootprintSize, frontier.size() + explored.size());
        return !frontier.isEmpty();
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
        return finished || frontier.isEmpty();
    }
}