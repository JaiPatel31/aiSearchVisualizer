package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Node;
import java.util.List;

public class SearchResult {
    private final List<Node> path;
    private final double cost;
    private final int nodesExpanded;
    private final int nodesGenerated;
    private final int exploredSize;
    private final int maxFrontierSize;
    private final int solutionDepth;
    private final long runtimeMillis;
    private final long memoryBytes;

    public SearchResult(List<Node> path, double cost, int nodesExpanded, int nodesGenerated,
                        int exploredSize, int maxFrontierSize, int solutionDepth,
                        long runtimeMillis, long memoryBytes) {
        this.path = path;
        this.cost = cost;
        this.nodesExpanded = nodesExpanded;
        this.nodesGenerated = nodesGenerated;
        this.exploredSize = exploredSize;
        this.maxFrontierSize = maxFrontierSize;
        this.solutionDepth = solutionDepth;
        this.runtimeMillis = runtimeMillis;
        this.memoryBytes = memoryBytes;
    }

        public List<Node> getPath() {
            return path;
        }

        public double getCost() {
            return cost;
        }

        public int getNodesExpanded() {
            return nodesExpanded;
        }

        public int getNodesGenerated() {
            return nodesGenerated;
        }

        public int getExploredSize() {
            return exploredSize;
        }

        public int getMaxFrontierSize() {
            return maxFrontierSize;
        }

        public int getSolutionDepth() {
            return solutionDepth;
        }

        public long getRuntimeMillis() {
            return runtimeMillis;
        }

        public long getMemoryBytes() {
            return memoryBytes;
        }


}

