package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import java.util.*;

/**
 * Iterative Deepening Depth-First Search (IDDFS)
 * Combines BFS's optimal depth discovery with DFS's low memory footprint.
 * Restarts a depth-limited DFS from the start node each time the limit increases.
 */
public class IDDFS extends AbstractSearchAlgorithm {

    private Graph graph;
    private Node start, goal;
    private SearchObserver observer;

    private int currentDepth = 0;
    private static final int MAX_DEPTH = 1000; // safety cap
    private boolean finished = false, initialized = false;

    private Deque<NodeDepth> stack;
    private Map<Node, Node> parentMap;
    private Set<Node> explored;

    // Metrics
    private int maxFrontierSize = 0, maxFootprintSize = 0, nodesGenerated = 0;
    private long startTime, beforeMem;

    /** Helper structure for stack entries (node + depth) */
    private static class NodeDepth {
        final Node node;
        final int depth;
        NodeDepth(Node n, int d) { node = n; depth = d; }
    }

    @Override
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        this.graph = graph;
        this.start = start;
        this.goal = goal;
        this.observer = observer;

        this.currentDepth = 0;
        this.finished = false;
        this.initialized = true;

        startNewDepth(); // initialize first depth-limited search

        startTime = System.nanoTime();
        beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    /**
     * Performs one "frame" or step of the IDDFS search.
     * Returns true while search should continue; false when complete.
     */
    @Override
    public boolean step() {
        if (!initialized || finished) return false;

        // Safety cap
        if (currentDepth > MAX_DEPTH) {
            finishNoPath();
            return false;
        }

        // If current depth search is exhausted, go deeper
        if (stack.isEmpty()) {
            currentDepth++;
            if (currentDepth > MAX_DEPTH) {
                finishNoPath();
                return false;
            }
            startNewDepth();
            System.out.println("🔁 Increasing depth to " + currentDepth);
            return true; // keep timeline running
        }

        // Pop from stack (DFS order)
        NodeDepth nd = stack.pop();
        Node current = nd.node;
        explored.add(current);
        nodesExpanded++;

        // Notify UI (visualization)
        notifyObserver(observer, current, extractFrontier(), explored, 0, explored.size(), 0, 0, 0);

        // Goal check
        if (current.equals(goal)) {
            finishSearch();
            return false;
        }

        // Expand neighbors within current depth limit
        if (nd.depth < currentDepth) {
            List<Edge> neighbors = graph.getNeighbors(current);
            // Reverse for consistent left-to-right DFS order (optional for UI)
            Collections.reverse(neighbors);

            for (Edge edge : neighbors) {
                Node neighbor = edge.getTo();
                if (!explored.contains(neighbor)) {
                    stack.push(new NodeDepth(neighbor, nd.depth + 1));
                    parentMap.put(neighbor, current);
                    nodesGenerated++;
                }
            }
        }

        maxFrontierSize = Math.max(maxFrontierSize, stack.size());
        maxFootprintSize = Math.max(maxFootprintSize, stack.size() + explored.size());
        return true;
    }

    /** Prepares for a new depth-limited DFS iteration. */
    private void startNewDepth() {
        stack = new ArrayDeque<>();
        explored = new HashSet<>();
        parentMap = new HashMap<>();
        stack.push(new NodeDepth(start, 0));
        parentMap.put(start, null);
    }

    /** Builds a list of nodes in the current frontier (for visualization). */
    private List<Node> extractFrontier() {
        List<Node> frontier = new ArrayList<>(stack.size());
        for (NodeDepth nd : stack) frontier.add(nd.node);
        return frontier;
    }

    /** Called when goal is found. */
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
                    path, nodesExpanded, nodesGenerated,
                    maxFrontierSize, totalCost, solutionDepth,
                    runtimeMs, memoryBytes
            );
        }

        System.out.println("✅ IDDFS found goal at depth " + currentDepth + ".");
    }

    /** Called when goal not found up to MAX_DEPTH. */
    private void finishNoPath() {
        finished = true;
        long endTime = System.nanoTime();
        long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long runtimeMs = (endTime - startTime) / 1_000_000;
        long memoryBytes = afterMem - beforeMem;

        if (observer != null) {
            observer.onFinish(
                    Collections.emptyList(), nodesExpanded, nodesGenerated,
                    maxFrontierSize, Double.POSITIVE_INFINITY, 0,
                    runtimeMs, memoryBytes
            );
        }

        System.out.println("⚠️ No path found (reached depth " + currentDepth + ").");
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}

