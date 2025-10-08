package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;

import java.util.*;

/**
 * Base class for all search algorithms.
 * Supports pause/resume/stop control and observer notifications.
 * Now supports step-based (incremental) execution for visualization.
 */
public abstract class AbstractSearchAlgorithm implements SearchAlgorithm {

    protected volatile boolean paused = false;
    protected volatile boolean stopped = false;

    protected int nodesExpanded = 0;

    public void pause() { paused = true; }
    public void resume() { paused = false; }
    public void stop() { stopped = true; }

    protected int nodesGenerated = 0;
    protected int maxFrontierSize = 0;
    protected long startTime = 0;

    protected Map<Node, Double> gScore = new HashMap<>();
    protected Map<Node, Double> hScore = new HashMap<>();
    protected Map<Node, Double> fScore = new HashMap<>();

    public int getNodesExpanded() { return nodesExpanded; }
    public int getNodesGenerated() { return nodesGenerated; }
    public int getMaxFrontierSize() { return maxFrontierSize; }
    public long getStartTime() { return startTime; }
    /**
     * Called in each loop iteration to honor pause/stop commands.
     */
    protected void checkControl() {
        while (paused) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
        if (stopped) {
            throw new RuntimeException("Search stopped");
        }
    }

    /**
     * Notify the observer with current search state.
     *
     * @param observer       The observer to notify
     * @param current        Current node being expanded
     * @param frontier       Nodes in frontier/open list
     * @param explored       Nodes explored/visited
     * @param pathCost       Cost from start to current node
     * @param solutionDepth  Depth of current node from start
     * @param g              Cost from start to current node
     * @param h              Heuristic estimate to goal
     * @param f              Total estimated cost (g + h)
     */
    protected void notifyObserver(SearchObserver observer,
                                  Node current,
                                  Collection<Node> frontier,
                                  Collection<Node> explored,
                                  double pathCost,
                                  int solutionDepth,
                                  double g,
                                  double h,
                                  double f) {
        nodesExpanded++;
        if (observer != null) {
            observer.onStep(current, frontier, explored,
                    nodesExpanded, pathCost, solutionDepth, g, h, f);
        }
    }

    // --- 🔹 New methods for incremental model ---

    /**
     * Initialize algorithm state before stepping begins.
     */
    public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
        // Default empty — each subclass overrides
    }

    /**
     * Perform a single step of the algorithm.
     * Return true if there are more steps remaining, false if finished.
     */
    public boolean step() {
        return false;
    }

    /**
     * Return whether the search has finished.
     */
    public boolean isFinished() {
        return stopped;
    }

    // Keep solve() for compatibility (batch mode algorithms)
    @Override
    public SearchResult solve(Graph graph, Node start, Node goal, SearchObserver observer) {
        throw new UnsupportedOperationException(
                "Incremental algorithms should use initialize() and step() instead of solve()."
        );
    }
    protected double calculatePathCost(Graph graph, List<Node> path) {
        double cost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            cost += graph.getEdgeWeight(path.get(i), path.get(i + 1));
        }
        return cost;
    }

    protected List<Node> reconstructPath(Map<Node, Node> parent, Node goal) {
        List<Node> path = new ArrayList<>();
        for (Node n = goal; n != null; n = parent.get(n)) {
            path.add(n);
        }
        Collections.reverse(path);
        return path;
    }

}
