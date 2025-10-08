package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Node;
import java.util.List;

/**
 * Represents the result of a search algorithm execution.
 * <p>
 * Contains the solution path, cost, and various search metrics such as nodes expanded/generated,
 * explored size, maximum frontier size, solution depth, runtime, and memory usage.
 */
public class SearchResult {
    /** The path from start to goal as a list of nodes. */
    private final List<Node> path;
    /** The total cost of the solution path. */
    private final double cost;
    /** The number of nodes expanded during the search. */
    private final int nodesExpanded;
    /** The number of nodes generated during the search. */
    private final int nodesGenerated;
    /** The number of nodes explored during the search. */
    private final int exploredSize;
    /** The maximum size of the frontier during the search. */
    private final int maxFrontierSize;
    /** The depth of the solution path. */
    private final int solutionDepth;
    /** The runtime of the search in milliseconds. */
    private final long runtimeMillis;
    /** The memory usage of the search in bytes. */
    private final long memoryBytes;

    /**
     * Constructs a SearchResult with all relevant metrics.
     *
     * @param path            The solution path as a list of nodes
     * @param cost            The total cost of the solution path
     * @param nodesExpanded   The number of nodes expanded
     * @param nodesGenerated  The number of nodes generated
     * @param exploredSize    The number of nodes explored
     * @param maxFrontierSize The maximum size of the frontier
     * @param solutionDepth   The depth of the solution path
     * @param runtimeMillis   The runtime in milliseconds
     * @param memoryBytes     The memory usage in bytes
     */
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

    /**
     * Returns the solution path as a list of nodes.
     * @return The solution path
     */
    public List<Node> getPath() {
        return path;
    }

    /**
     * Returns the total cost of the solution path.
     * @return The path cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * Returns the number of nodes expanded during the search.
     * @return Number of nodes expanded
     */
    public int getNodesExpanded() {
        return nodesExpanded;
    }

    /**
     * Returns the number of nodes generated during the search.
     * @return Number of nodes generated
     */
    public int getNodesGenerated() {
        return nodesGenerated;
    }

    /**
     * Returns the number of nodes explored during the search.
     * @return Number of nodes explored
     */
    public int getExploredSize() {
        return exploredSize;
    }

    /**
     * Returns the maximum size of the frontier during the search.
     * @return Maximum frontier size
     */
    public int getMaxFrontierSize() {
        return maxFrontierSize;
    }

    /**
     * Returns the depth of the solution path.
     * @return Solution path depth
     */
    public int getSolutionDepth() {
        return solutionDepth;
    }

    /**
     * Returns the runtime of the search in milliseconds.
     * @return Runtime in milliseconds
     */
    public long getRuntimeMillis() {
        return runtimeMillis;
    }

    /**
     * Returns the memory usage of the search in bytes.
     * @return Memory usage in bytes
     */
    public long getMemoryBytes() {
        return memoryBytes;
    }

}
