package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import java.util.*;

public class IDDFS extends AbstractSearchAlgorithm {

    private Graph graph;
    private Node start, goal;
    private SearchObserver observer;

    private int currentDepth = 0;
    private boolean finished = false;
    private boolean initialized = false;

    private Deque<NodeDepth> stack;
    private Set<Node> explored;
    private Map<Node, Node> parentMap;

    private long startTime;
    private long beforeMem;

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

        this.startTime = System.nanoTime();
        this.beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        this.initialized = true;
        this.finished = false;
    }

    private void resetDepthLimited() {
        this.stack = new ArrayDeque<>();
        this.explored = new HashSet<>();
        this.parentMap = new HashMap<>();
        this.stack.push(new NodeDepth(start, 0));
        this.parentMap.put(start, null);
    }

    @Override
    public boolean step() {
        if (!initialized || finished) return false;
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

        if (nd.depth < currentDepth) {
            for (var edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getTo();
                if (!explored.contains(neighbor)) {
                    stack.push(new NodeDepth(neighbor, nd.depth + 1));
                    parentMap.put(neighbor, current);
                }
            }
        }

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
        observer.onFinish(path, nodesExpanded, totalCost);
    }

    private List<Node> reconstructPath(Map<Node, Node> parent, Node goal) {
        List<Node> path = new ArrayList<>();
        for (Node n = goal; n != null; n = parent.get(n)) path.add(n);
        Collections.reverse(path);
        return path;
    }

    private double calculatePathCost(Graph graph, List<Node> path) {
        double cost = 0;
        for (int i = 0; i < path.size() - 1; i++)
            cost += graph.getEdgeWeight(path.get(i), path.get(i + 1));
        return cost;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}
