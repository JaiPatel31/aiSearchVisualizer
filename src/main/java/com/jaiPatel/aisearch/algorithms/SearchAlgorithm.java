package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;

public interface SearchAlgorithm {
    SearchResult solve(Graph graph, Node start, Node goal, SearchObserver observer);
}
