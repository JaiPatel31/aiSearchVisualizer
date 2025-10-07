package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import java.util.*;

public class IDDFS extends AbstractSearchAlgorithm {

    private Graph graph; private Node start, goal; private SearchObserver observer;
    private int currentDepth = 0; private boolean finished = false, initialized = false;
    private Deque<NodeDepth> stack; private Set<Node> explored; private Map<Node, Node> parentMap;
    private int maxFrontierSize = 0, maxFootprintSize = 0, nodesGenerated = 0;;
    private long startTime, beforeMem;

    private static class NodeDepth {
        Node node; int depth;
        NodeDepth(Node n, int d) { node = n; depth = d; }
    }

    @Override
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        this.graph = graph; this.start = start; this.goal = goal; this.observer = observer;
        resetDepthLimited();
        startTime = System.nanoTime();
        beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        initialized = true; finished = false;
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
        if (stack.isEmpty()) { currentDepth++; resetDepthLimited(); return true; }

        NodeDepth nd = stack.pop();
        Node current = nd.node;
        explored.add(current); nodesExpanded++;

        notifyObserver(observer, current, extractFrontier(), explored, 0, explored.size(), 0, 0, 0);

        if (current.equals(goal)) { finishSearch(); return false; }

        if (nd.depth < currentDepth) {
            for (var edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTo();
                if (!explored.contains(neighbor)) {
                    stack.push(new NodeDepth(neighbor, nd.depth + 1));
                    parentMap.put(neighbor, current);
                    nodesGenerated++;  // ✅ count newly generated nodes
                }
            }
        }

        maxFrontierSize = Math.max(maxFrontierSize, stack.size());
        maxFootprintSize = Math.max(maxFootprintSize, stack.size() + explored.size());
        return true;
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
                    path,
                    nodesExpanded,
                    nodesGenerated,
                    stack.size(), // approximate frontier at end
                    totalCost,
                    solutionDepth,
                    runtimeMs,
                    memoryBytes
            );
        }
    }


    @Override public boolean isFinished() { return finished; }
}
