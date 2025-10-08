package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;

/**
 * Heuristic function using Manhattan distance for grid-based pathfinding.
 * <p>
 * The Manhattan distance is the sum of the absolute differences in x and y coordinates.
 * Suitable for grids where movement is only allowed in horizontal and vertical directions.
 */
public class ManhattanHeuristic implements Heuristic {
    /**
     * Estimates the cost from the current node to the goal node using Manhattan distance.
     *
     * @param current The current node
     * @param goal    The goal node
     * @return The Manhattan distance between current and goal
     */
    @Override
    public double estimate(Node current, Node goal) {
        return Math.abs(current.getX() - goal.getX()) + Math.abs(current.getY() - goal.getY());
    }
}
