package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import com.jaiPatel.aisearch.heuristics.*;
import java.util.*;

public class AStarSearch extends AbstractSearchAlgorithm {

    private final Heuristic heuristic;

    private Graph graph;
    private Node start, goal;
    private SearchObserver observer;

    private PriorityQueue<Node> frontier;
    private Set<Node> frontierSet;
    private Set<Node> explored;
    private Map<Node, Node> parentMap;
    private Map<Node, Double> gScores;

    private boolean initialized = false;
    private boolean finished = false;

    private int nodesGenerated = 0;
    private int maxFrontierSize = 0;

    private long startTime;
    private long beforeMem;

    public AStarSearch(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    @Override
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        this.graph = graph;
        this.start = start;
        this.goal = goal;
        this.observer = observer;

        this.parentMap = new HashMap<>();
        this.gScores = new HashMap<>();
        this.explored = new HashSet<>();
        this.frontierSet = new HashSet<>();

        this.frontier = new PriorityQueue<>(
                Comparator.comparingDouble(n -> gScores.getOrDefault(n, Double.POSITIVE_INFINITY)
                        + heuristic.estimate(n, goal))
        );

        parentMap.put(start, null);
        gScores.put(start, 0.0);
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
        notifyObserver(observer, current, frontier, explored, g, explored.size(), g, h, f);

        if (current.equals(goal)) {
            finishSearch();
            return false;
        }

        for (var edge : graph.getNeighbors(current)) {
            Node neighbor = edge.getTo();
            double tentativeG = g + edge.getCost();

            if (!gScores.containsKey(neighbor) || tentativeG < gScores.get(neighbor)) {
                gScores.put(neighbor, tentativeG);
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

    private void finishSearch() {
        finished = true;
        List<Node> path = reconstructPath(parentMap, goal);
        double totalCost = gScores.get(goal);
        observer.onFinish(path, nodesExpanded, totalCost);
    }

    private List<Node> reconstructPath(Map<Node, Node> parent, Node goal) {
        List<Node> path = new ArrayList<>();
        for (Node n = goal; n != null; n = parent.get(n)) path.add(n);
        Collections.reverse(path);
        return path;
    }

    @Override
    public boolean isFinished() {
        return finished || frontier.isEmpty();
    }
}
