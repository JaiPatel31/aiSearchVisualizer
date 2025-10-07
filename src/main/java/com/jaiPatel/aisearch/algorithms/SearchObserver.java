package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Node;
import java.util.Collection;
import java.util.List;

/**
 * Observer interface for step-by-step search visualization and metrics reporting.
 */
public interface SearchObserver {

    /**
     * Called at each step of the search algorithm.
     */
    void onStep(Node current,
                Collection<Node> frontier,
                Collection<Node> explored,
                int nodesExpanded,
                double pathCost,
                int solutionDepth,
                double g,
                double h,
                double f);

    /**
     * Called once when the search completes (successfully or fails).
     * Provides full benchmarking metrics.
     *
     * @param path              The final solution path (may be null or empty if no solution found)
     * @param totalNodesExpanded Total number of nodes expanded
     * @param totalNodesGenerated Total number of nodes generated
     * @param maxFrontierSize   Maximum frontier size observed
     * @param totalCost         Total path cost of the found solution
     * @param solutionDepth     Depth of the final solution path
     * @param elapsedTimeMs     Wall-clock runtime in milliseconds
     * @param memoryBytes       Peak memory used (approx.)
     */
    default void onFinish(List<Node> path,
                          int totalNodesExpanded,
                          int totalNodesGenerated,
                          int maxFrontierSize,
                          double totalCost,
                          int solutionDepth,
                          long elapsedTimeMs,
                          long memoryBytes) {
        // Optional to override
    }

}