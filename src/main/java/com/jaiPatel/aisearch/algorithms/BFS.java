package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;

import java.util.*;

/**
 * Incremental BFS for live visualization.
 */
public class BFS extends AbstractSearchAlgorithm {

    private Queue<Node> frontier;
    private Set<Node> explored;
    private Map<Node, Node> parentMap;

    private Graph graph;
    private Node start;
    private Node goal;
    private SearchObserver observer;

    private boolean initialized = false;
    private boolean finished = false;

    private int nodesGenerated = 0;
    private int nodesExpanded = 0;
    private int maxFrontierSize = 0;

    private long startTime;
    private long beforeMem;

    /** Called once before stepping starts */
    @Override
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        this.graph = graph;
        this.start = start;
        this.goal = goal;
        this.observer = observer;

        this.frontier = new LinkedList<>();
        this.explored = new HashSet<>();
        this.parentMap = new HashMap<>();

        this.frontier.add(start);
        this.parentMap.put(start, null);

        this.nodesGenerated = 0;
        this.nodesExpanded = 0;
        this.maxFrontierSize = 1;

        this.startTime = System.nanoTime();
        this.beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        this.initialized = true;
        this.finished = false;
    }

    /** Runs one BFS step — return true if more steps remain */
    @Override
    public boolean step() {
        if (!initialized || finished || frontier.isEmpty()) return false;

        Node current = frontier.poll();
        explored.add(current);
        nodesExpanded++;

        // Notify UI (all metrics updated live)
        notifyObserver(observer, current, frontier, explored,
                0, // path cost (BFS = uniform cost)
                explored.size(),
                0, 0, 0);

        // Check goal
        if (current.equals(goal)) {
            finishSearch();
            return false;
        }

        // Expand neighbors
        for (var edge : graph.getNeighbors(current)) {
            Node neighbor = edge.getTo();
            if (!explored.contains(neighbor) && !frontier.contains(neighbor)) {
                frontier.add(neighbor);
                parentMap.put(neighbor, current);
                nodesGenerated++;
            }
        }

        maxFrontierSize = Math.max(maxFrontierSize, frontier.size());
        return !frontier.isEmpty();
    }

    /** Called automatically when goal is found */
    private void finishSearch() {
        finished = true;

        List<Node> path = reconstructPath(parentMap, goal);
        double totalCost = calculatePathCost(graph, path);
        int solutionDepth = path.size() - 1;

        long endTime = System.nanoTime();
        long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        observer.onFinish(path, nodesExpanded, totalCost);

        new SearchResult(
                path,
                totalCost,
                nodesExpanded,
                nodesGenerated,
                explored.size(),
                maxFrontierSize,
                solutionDepth,
                (endTime - startTime) / 1_000_000,
                (afterMem - beforeMem)
        );
    }

    private double calculatePathCost(Graph graph, List<Node> path) {
        double cost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            cost += graph.getEdgeWeight(path.get(i), path.get(i + 1));
        }
        return cost;
    }

    private List<Node> reconstructPath(Map<Node, Node> parent, Node goal) {
        List<Node> path = new ArrayList<>();
        for (Node n = goal; n != null; n = parent.get(n)) {
            path.add(n);
        }
        Collections.reverse(path);
        return path;
    }

    @Override
    public boolean isFinished() {
        return finished || frontier.isEmpty();
    }
}



