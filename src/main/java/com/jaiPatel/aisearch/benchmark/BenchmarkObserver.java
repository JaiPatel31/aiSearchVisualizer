package com.jaiPatel.aisearch.benchmark;

import com.jaiPatel.aisearch.algorithms.SearchObserver;
import com.jaiPatel.aisearch.graph.Node;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Collection;
import java.util.List;

/**
 * Observer for benchmarking search algorithms.
 * <p>
 * Tracks timing, memory usage, and search metrics such as nodes expanded/generated,
 * maximum frontier size, solution cost, and solution depth.
 * Used to collect statistics for performance analysis during batch runs.
 */
public class BenchmarkObserver implements SearchObserver {
    /** Start time of the search (nanoseconds). */
    private long startTime, endTime;
    /** Memory usage before and after the search (bytes). */
    private long beforeUsed, afterUsed;
    /** Number of nodes expanded during the search. */
    private int nodesExpanded;
    /** Number of nodes generated during the search. */
    private int nodesGenerated;
    /** Maximum size of the frontier during the search. */
    private int maxFrontier;
    /** Total cost of the solution path. */
    private double totalCost;
    /** Depth of the solution path. */
    private int depth;

    /**
     * Starts the benchmark timer and records initial memory usage.
     * Should be called before the search begins.
     */
    public void start() {
        System.gc();
        Runtime rt = Runtime.getRuntime();
        beforeUsed = rt.totalMemory() - rt.freeMemory();
        startTime = System.nanoTime();
    }

    /**
     * Called on each search step. Not used for benchmarking, but required by the interface.
     *
     * @param current        The current node being expanded
     * @param frontier       The current frontier (open list)
     * @param explored       The set of explored nodes
     * @param nodesExpanded  Number of nodes expanded so far
     * @param pathCost       Cost from start to current node
     * @param solutionDepth  Depth of the current node
     * @param g              Cost from start to current node
     * @param h              Heuristic estimate to goal
     * @param f              Total estimated cost (g + h)
     */
    @Override
    public void onStep(Node current, Collection<Node> frontier, Collection<Node> explored, int nodesExpanded, double pathCost, int solutionDepth, double g, double h, double f) {
        // Not used for batch benchmarking
    }

    /**
     * Called when the search finishes. Records final memory usage and search metrics.
     *
     * @param path            The solution path
     * @param nodesExpanded   Number of nodes expanded
     * @param nodesGenerated  Number of nodes generated
     * @param maxFrontierSize Maximum frontier size
     * @param totalCost       Total cost of the solution path
     * @param solutionDepth   Depth of the solution path
     * @param runtimeMs       Runtime in milliseconds
     * @param memoryBytes     Memory usage in bytes
     */
    @Override
    public void onFinish(List<Node> path, int nodesExpanded, int nodesGenerated,
                         int maxFrontierSize, double totalCost,
                         int solutionDepth, long runtimeMs, long memoryBytes) {
        Runtime rt = Runtime.getRuntime();
        afterUsed = rt.totalMemory() - rt.freeMemory();

        this.nodesExpanded = nodesExpanded;
        this.nodesGenerated = nodesGenerated;
        this.maxFrontier = maxFrontierSize;
        this.totalCost = totalCost;
        this.depth = solutionDepth;
        endTime = System.nanoTime();
    }

    /**
     * Returns the runtime of the search in milliseconds.
     * @return Runtime in milliseconds
     */
    public double getRuntimeMs() { return (endTime - startTime) / 1_000_000.0; }

    /**
     * Returns the memory usage of the search in kilobytes.
     * @return Memory usage in kilobytes
     */
    public double getMemoryKB() { return (afterUsed - beforeUsed) / 1024.0; }

    /**
     * Returns the number of nodes expanded during the search.
     * @return Number of nodes expanded
     */
    public int getNodesExpanded() { return nodesExpanded; }

    /**
     * Returns the number of nodes generated during the search.
     * @return Number of nodes generated
     */
    public int getNodesGenerated() { return nodesGenerated; }

    /**
     * Returns the maximum size of the frontier during the search.
     * @return Maximum frontier size
     */
    public int getMaxFrontier() { return maxFrontier; }

    /**
     * Returns the depth of the solution path.
     * @return Solution path depth
     */
    public int getSolutionDepth() { return depth; }

    /**
     * Returns the total cost of the solution path.
     * @return Total path cost
     */
    public double getTotalCost() { return totalCost; }
}
