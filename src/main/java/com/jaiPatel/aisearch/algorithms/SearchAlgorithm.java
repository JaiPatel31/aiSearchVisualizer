package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;

/**
 * Interface for incremental search algorithms used in live visualization and benchmarking.
 * <p>
 * Implementations should support step-wise execution, pausing, resuming, and stopping.
 * This interface also provides optional methods for batch solving and search metrics.
 */
public interface SearchAlgorithm {

    /**
     * Initializes the algorithm before stepping begins.
     *
     * @param graph    The graph to search
     * @param start    The start node
     * @param goal     The goal node
     * @param observer The observer to notify during the search
     */
    void initialize(Graph graph, Node start, Node goal, SearchObserver observer);

    /**
     * Performs one step of the algorithm.
     *
     * @return true if more steps remain, false if search is finished
     */
    boolean step();

    /**
     * Checks if the algorithm has finished (goal found or search exhausted).
     *
     * @return true if the algorithm has finished
     */
    boolean isFinished();

    /**
     * Fully solves the problem without visualization (optional).
     * Useful for benchmarking or background runs.
     *
     * @param graph    The graph to search
     * @param start    The start node
     * @param goal     The goal node
     * @param observer The observer to notify during the search
     * @return SearchResult containing the result of the search
     */
    SearchResult solve(Graph graph, Node start, Node goal, SearchObserver observer);

    /**
     * Returns the number of nodes expanded during the search.
     *
     * @return Number of nodes expanded
     */
    default int getNodesExpanded() { return 0; }

    /**
     * Returns the number of nodes generated during the search.
     *
     * @return Number of nodes generated
     */
    default int getNodesGenerated() { return 0; }

    /**
     * Returns the maximum size of the frontier during the search.
     *
     * @return Maximum frontier size
     */
    default int getMaxFrontierSize() { return 0; }

    /**
     * Returns the start time of the search in milliseconds.
     *
     * @return Start time in milliseconds
     */
    default long getStartTime() { return 0L; }
}
