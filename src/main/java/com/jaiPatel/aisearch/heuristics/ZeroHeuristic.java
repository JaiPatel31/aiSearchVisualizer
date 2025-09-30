package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;

public class ZeroHeuristic implements Heuristic {
    @Override
    public double estimate(Node current, Node goal) {
        return 0.0;
    }
}

