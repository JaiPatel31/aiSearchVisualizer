package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;

public class ManhattanHeuristic implements Heuristic {
    @Override
    public double estimate(Node current, Node goal) {
        return Math.abs(current.getX() - goal.getX()) + Math.abs(current.getY() - goal.getY());
    }
}

