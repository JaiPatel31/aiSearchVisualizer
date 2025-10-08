package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import java.util.*;

public class IDDFS extends AbstractSearchAlgorithm {

    private Graph graph;
    private Node start, goal;
    private SearchObserver observer;

    private int currentDepth = 0;
    private final int MAX_DEPTH = 1000; // safety cap for unreachable goals
    private boolean finished = false, initialized = false;

    private Deque<NodeDepth> stack;
    private Set<Node> explored;
    private Map<Node, Node> parentMap;

    private int maxFrontierSize = 0, maxFootprintSize = 0, nodesGenerated = 0;
    private long startTime, beforeMem;

    private static class NodeDepth {
        Node node;
        int depth;
        NodeDepth(Node n, int d) { node = n; depth = d; }
    }

    @Override
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        this.graph = graph;
        this.start = start;
        this.goal = goal;
        this.observer = observer;

        resetDepthLimited();
        startTime = System.nanoTime();
        beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        initialized = true;
        finished = false;
    }

    private void resetDepthLimited() {
        stack = new ArrayDeque<>();
        explored = new HashSet<>();
        parentMap = new HashMap<>();
        stack.push(new NodeDepth(start, 0));
        parentMap.put(start, null);
    }

    @Override
    public boolean step() {
        if (!initialized || finished) return false;

        // reached maximum depth → give up
        if (currentDepth > MAX_DEPTH) {
            finishNoPath();
            return false;
        }

        // depth exhausted → increase and restart
        if (stack.isEmpty()) {
            currentDepth++;
            resetDepthLimited();
            return true;
        }

        NodeDepth nd = stack.pop();
        Node current = nd.node;
        explored.add(current);
        nodesExpanded++;

        notifyObserver(observer, current, extractFrontier(), explored, 0, explored.size(), 0, 0, 0);

        if (current.equals(goal)) {
            finishSearch();
            return false;
        }

        // expand children if within depth limit
        if (nd.depth < currentDepth) {
            for (var edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTo();
                // skip already-explored or ancestor nodes to prevent cycles
                if (!explored.contains(neighbor) && !isAncestor(neighbor, current)) {
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

    private boolean isAncestor(Node candidate, Node current) {
        Node parent = parentMap.get(current);
        while (parent != null) {
            if (parent.equals(candidate)) return true;
            parent = parentMap.get(parent);
        }
        return false;
    }

    private List<Node> extractFrontier() {
        List<Node> f = new ArrayList<>();
        for (NodeDepth nd : stack) f.add(nd.node);
        return f;
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
                    path, nodesExpanded, nodesGenerated,
                    maxFrontierSize, totalCost, solutionDepth,
                    runtimeMs, memoryBytes
            );
        }
    }

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

        System.out.println("⚠️ IDDFS reached max depth " + MAX_DEPTH + " without finding a path.");
    }

    @Override
    public boolean isFinished() { return finished; }
}

