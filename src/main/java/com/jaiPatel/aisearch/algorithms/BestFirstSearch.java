package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import com.jaiPatel.aisearch.heuristics.*;
import java.util.*;

public class BestFirstSearch extends AbstractSearchAlgorithm {

    private final Heuristic heuristic;

    private Graph graph;
    private Node start, goal;
    private SearchObserver observer;

    private PriorityQueue<Node> frontier;
    private Set<Node> frontierSet;
    private Set<Node> explored;
    private Map<Node, Node> parentMap;

    private boolean initialized = false;
    private boolean finished = false;

    private int nodesGenerated = 0;
    private int maxFrontierSize = 0;

    private long startTime;
    private long beforeMem;

    public BestFirstSearch(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        this.graph = graph;
        this.start = start;
        this.goal = goal;
        this.observer = observer;

        frontier = new PriorityQueue<>(Comparator.comparingDouble(n -> heuristic.estimate(n, goal)));
        frontierSet = new HashSet<>();
        explored = new HashSet<>();
        parentMap = new HashMap<>();

        frontier.add(start);
        frontierSet.add(start);
        parentMap.put(start, null);

        nodesGenerated = 1;
        nodesExpanded = 0;
        maxFrontierSize = 1;

        startTime = System.nanoTime();
        beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        initialized = true;
        finished = false;
    }

    @Override
    public boolean step() {
        if (!initialized || finished || frontier.isEmpty()) return false;

        Node current = frontier.poll();
        frontierSet.remove(current);
        explored.add(current);
        nodesExpanded++;

        double g = 0;
        double h = heuristic.estimate(current, goal);
        double f = h;

        notifyObserver(observer, current, frontier, explored, 0, explored.size(), g, h, f);

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
                nodesGenerated++;
            }
        }

        maxFrontierSize = Math.max(maxFrontierSize, frontier.size());
        return !frontier.isEmpty();
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
        return finished || frontier.isEmpty();
    }
}



