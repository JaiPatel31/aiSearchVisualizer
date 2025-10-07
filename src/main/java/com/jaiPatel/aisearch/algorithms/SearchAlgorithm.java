package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;

/**
 * Incremental search algorithm interface.
 * Used for live visualization (start, step, pause, resume, stop).
 */
public interface SearchAlgorithm {

    /**
     * Initializes the algorithm before stepping begins.
     */
    void initialize(Graph graph, Node start, Node goal, SearchObserver observer);

    /**
     * Performs one step of the algorithm.
     *
     * @return true if more steps remain, false if search is finished.
     */
    boolean step();

    /**
     * @return true if the algorithm has finished (goal found or search exhausted).
     */
    boolean isFinished();

    /**
     * Optional: fully solves the problem without visualization.
     * Useful for benchmarking or background runs.
     */
    SearchResult solve(Graph graph, Node start, Node goal, SearchObserver observer);

    default int getNodesExpanded() { return 0; }
    default int getNodesGenerated() { return 0; }
    default int getMaxFrontierSize() { return 0; }
    default long getStartTime() { return 0L; }
}

