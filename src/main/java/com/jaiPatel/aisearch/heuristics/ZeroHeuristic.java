package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;

/**
 * Heuristic function that always returns zero.
 * <p>
 * Used for algorithms that require a heuristic but should behave like uniform-cost search (Dijkstra's algorithm).
 * Effectively disables heuristic guidance.
 */
public class ZeroHeuristic implements Heuristic {
    /**
     * Estimates the cost from the current node to the goal node.
     * Always returns zero.
     *
     * @param current The current node
     * @param goal    The goal node
     * @return Always 0.0
     */
    @Override
    public double estimate(Node current, Node goal) {
        return 0.0;
    }
}
