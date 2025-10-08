package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;

/**
 * Interface for heuristic functions used in search algorithms.
 * <p>
 * Implementations provide an estimate of the cost from the current node to the goal node.
 * Used by algorithms such as A* and Best-First Search to guide search decisions.
 */
public interface Heuristic {
    /**
     * Estimates the cost from the current node to the goal node.
     *
     * @param current The current node
     * @param goal    The goal node
     * @return Estimated cost from current to goal
     */
    double estimate(Node current, Node goal);
}
