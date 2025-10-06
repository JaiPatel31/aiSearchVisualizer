package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Node;
import java.util.Collection;
import java.util.List;

/**
 * Observer interface for step-by-step search visualization.
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
     * @param path  The final solution path (may be null or empty if no solution found)
     * @param totalNodesExpanded  The final count of nodes expanded
     * @param totalCost  The total path cost of the found solution
     */
    default void onFinish(List<Node> path, int totalNodesExpanded, double totalCost) {
        // Optional to override
    }
}
