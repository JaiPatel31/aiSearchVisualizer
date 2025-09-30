package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;

public interface Heuristic {
    double estimate(Node current, Node goal);
}
