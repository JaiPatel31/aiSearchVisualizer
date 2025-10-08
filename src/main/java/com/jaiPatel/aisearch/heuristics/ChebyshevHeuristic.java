package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;

/**
 * Heuristic function using Chebyshev distance for grid-based pathfinding.
 * <p>
 * The Chebyshev distance is the maximum of the absolute differences in x and y coordinates.
 * Suitable for grids where movement in any direction (including diagonals) costs the same.
 */
public class ChebyshevHeuristic implements Heuristic {
    /**
     * Estimates the cost from the current node to the goal node using Chebyshev distance.
     *
     * @param current The current node
     * @param goal    The goal node
     * @return The Chebyshev distance between current and goal
     */
    @Override
    public double estimate(Node current, Node goal) {
        return Math.max(Math.abs(current.getX() - goal.getX()),
                Math.abs(current.getY() - goal.getY()));
    }
}
