package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import com.jaiPatel.aisearch.heuristics.*;
import java.util.*;

/**
 * Greedy Best-First Search with heuristic tracking and observer updates.
 * Implements the Best-First Search algorithm for pathfinding and graph traversal.
 */
public class BestFirstSearch extends AbstractSearchAlgorithm {

    // The heuristic used to estimate the cost to the goal
    private final Heuristic heuristic;

    // The graph to search
    private Graph graph;

    // The start and goal nodes
    private Node start, goal;

    // Observer to notify during the search
    private SearchObserver observer;

    // Priority queue for the frontier (open list)
    private PriorityQueue<Node> frontier;

    // Set of nodes in the frontier
    private Set<Node> frontierSet;

    // Set of explored (visited) nodes
    private Set<Node> explored;

    // Map of nodes to their parent nodes
    private Map<Node, Node> parentMap;

    // Map for heuristic scores of nodes
    private Map<Node, Double> hScores;

    // Flags to track initialization and completion
    private boolean initialized = false, finished = false;

    // Tracks the number of nodes generated and the maximum frontier size
    private int nodesGenerated = 0, maxFrontierSize = 0;

    // Tracks the start time and memory usage before the search
    private long startTime, beforeMem;

    /**
     * Constructs a BestFirstSearch instance with the given heuristic.
     *
     * @param heuristic The heuristic function to use
     */
    public BestFirstSearch(Heuristic heuristic) { this.heuristic = heuristic; }

    /**
     * Initializes the Best-First Search algorithm with the given graph, start and goal nodes, and observer.
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

    /**
     * Performs a single step of the Best-First Search algorithm.
     *
     * @return True if there are more steps remaining, false if the search is finished
     */
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
            observer.onFinish(path, nodesExpanded, nodesGenerated, maxFrontierSize,
                    totalCost, solutionDepth, runtimeMs, memoryBytes);
        }
    }

    /**
     * Returns the heuristic value (h) for the given node.
     *
     * @param n The node to evaluate
     * @return The heuristic value
     */
    public double getHeuristicValue(Node n) { return hScores.getOrDefault(n, 0.0); }

    /**
     * Checks if the search is finished.
     *
     * @return True if the search is finished, false otherwise
     */
    @Override public boolean isFinished() { return finished || frontier.isEmpty(); }
}