package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import java.util.*;

public class DFS extends AbstractSearchAlgorithm {

    private Deque<Node> stack;
    private Set<Node> explored;
    private Map<Node, Node> parentMap;

    private Graph graph;
    private Node start;
    private Node goal;
    private SearchObserver observer;

    private boolean initialized = false;
    private boolean finished = false;

    private int nodesGenerated = 0;
    private int maxFrontierSize = 0;

    private long startTime;
    private long beforeMem;

    @Override
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        this.graph = graph;
        this.start = start;
        this.goal = goal;
        this.observer = observer;

        this.stack = new ArrayDeque<>();
        this.explored = new HashSet<>();
        this.parentMap = new HashMap<>();

        this.stack.push(start);
        this.parentMap.put(start, null);

        this.nodesGenerated = 1;
        this.nodesExpanded = 0;
        this.maxFrontierSize = 1;

        this.startTime = System.nanoTime();
        this.beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        this.initialized = true;
        this.finished = false;
    }

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

        List<Node> neighbors = new ArrayList<>();
        for (var edge : graph.getNeighbors(current)) {
            neighbors.add(edge.getTo());
        }
        Collections.reverse(neighbors); // maintain standard DFS order

        for (Node neighbor : neighbors) {
            if (!explored.contains(neighbor) && !stack.contains(neighbor)) {
                stack.push(neighbor);
                parentMap.put(neighbor, current);
                nodesGenerated++;
            }
        }

        maxFrontierSize = Math.max(maxFrontierSize, stack.size());
        return !stack.isEmpty();
    }

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
        return finished || stack.isEmpty();
    }
}
