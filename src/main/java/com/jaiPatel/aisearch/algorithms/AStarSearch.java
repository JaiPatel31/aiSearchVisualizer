package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import com.jaiPatel.aisearch.heuristics.*;
import java.util.*;

/**
 * A* Search with full heuristic tracking (g, h, f) and observer updates.
 * Implements the A* search algorithm for pathfinding and graph traversal.
 */
public class AStarSearch extends AbstractSearchAlgorithm {

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

    // Maps for g, h, and f scores of nodes
    private Map<Node, Double> gScores, hScores, fScores;

    // Flags to track initialization and completion
    private boolean initialized = false, finished = false;

    // Tracks the number of nodes generated and the maximum frontier size
    private int nodesGenerated = 0, maxFrontierSize = 0;

    // Tracks the start time and memory usage before the search
    private long startTime, beforeMem;

    /**
     * Constructs an AStarSearch instance with the given heuristic.
     *
     * @param heuristic The heuristic function to use
     */
    public AStarSearch(Heuristic heuristic) { this.heuristic = heuristic; }

    /**
     * Initializes the A* search algorithm with the given graph, start and goal nodes, and observer.
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

        parentMap = new HashMap<>();
        gScores = new HashMap<>();
        hScores = new HashMap<>();
        fScores = new HashMap<>();

        explored = new HashSet<>();
        frontierSet = new HashSet<>();

        frontier = new PriorityQueue<>(Comparator.comparingDouble(
                n -> gScores.getOrDefault(n, Double.POSITIVE_INFINITY)
                        + heuristic.estimate(n, goal)));

        parentMap.put(start, null);
        gScores.put(start, 0.0);
        hScores.put(start, heuristic.estimate(start, goal));
        fScores.put(start, hScores.get(start));
        frontier.add(start);
        frontierSet.add(start);

        nodesGenerated = 1;
        nodesExpanded = 0;
        maxFrontierSize = 1;
        startTime = System.nanoTime();
        beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        initialized = true;
        finished = false;
    }

    /**
     * Performs a single step of the A* search algorithm.
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

        double g = gScores.getOrDefault(current, 0.0);
        double h = heuristic.estimate(current, goal);
        double f = g + h;
        hScores.put(current, h);
        fScores.put(current, f);

        notifyObserver(observer, current, frontier, explored, nodesExpanded, explored.size(), g, h, f);

        if (current.equals(goal)) {
            finishSearch();
            return false;
        }

        for (var edge : graph.getNeighbors(current)) {
            Node neighbor = edge.getTo();
            double tentativeG = g + edge.getCost();
            if (!gScores.containsKey(neighbor) || tentativeG < gScores.get(neighbor)) {
                gScores.put(neighbor, tentativeG);
                hScores.put(neighbor, heuristic.estimate(neighbor, goal));
                fScores.put(neighbor, tentativeG + hScores.get(neighbor));
                parentMap.put(neighbor, current);

                if (!frontierSet.contains(neighbor)) {
                    frontier.add(neighbor);
                    frontierSet.add(neighbor);
                    nodesGenerated++;
                }
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
        double totalCost = gScores.getOrDefault(goal, Double.POSITIVE_INFINITY);
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
     * Returns the total estimated cost (f) for the given node.
     *
     * @param n The node to evaluate
     * @return The total estimated cost
     */
    public double getFValue(Node n) { return fScores.getOrDefault(n, 0.0); }

    /**
     * Checks if the search is finished.
     *
     * @return True if the search is finished, false otherwise
     */
    @Override public boolean isFinished() { return finished || frontier.isEmpty(); }
}