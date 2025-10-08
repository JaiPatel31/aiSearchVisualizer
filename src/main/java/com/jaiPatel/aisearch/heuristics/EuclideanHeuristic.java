package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;

/**
 * Heuristic function using Euclidean distance for grid-based or spatial pathfinding.
 * <p>
 * The Euclidean distance is the straight-line distance between two points in 2D space.
 * Suitable for graphs where movement cost is proportional to geometric distance.
 */
public class EuclideanHeuristic implements Heuristic {
    /**
     * Estimates the cost from the current node to the goal node using Euclidean distance.
     *
     * @param current The current node
     * @param goal    The goal node
     * @return The Euclidean distance between current and goal
     */
    @Override
    public double estimate(Node current, Node goal) {
        double dx = current.getX() - goal.getX();
        double dy = current.getY() - goal.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
