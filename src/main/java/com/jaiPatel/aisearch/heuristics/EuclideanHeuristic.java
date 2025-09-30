package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;

public class EuclideanHeuristic implements Heuristic {
    @Override
    public double estimate(Node current, Node goal) {
        double dx = current.getX() - goal.getX();
        double dy = current.getY() - goal.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}

