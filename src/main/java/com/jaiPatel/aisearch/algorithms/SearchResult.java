package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Node;
import java.util.List;

public class SearchResult {
    private final List<Node> path;
    private final double cost;
    private final int nodesExpanded;
    private final int exploredSize;

    public SearchResult(List<Node> path, double cost, int nodesExpanded, int exploredSize) {
        this.path = path;
        this.cost = cost;
        this.nodesExpanded = nodesExpanded;
        this.exploredSize = exploredSize;
    }

    public List<Node> getPath() { return path; }
    public double getCost() { return cost; }
    public int getNodesExpanded() { return nodesExpanded; }
    public int getExploredSize() { return exploredSize; }
}
