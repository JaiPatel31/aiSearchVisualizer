package com.jaiPatel.aisearch.UI;

import com.jaiPatel.aisearch.algorithms.SearchObserver;
import com.jaiPatel.aisearch.graph.Node;
import javafx.application.Platform;

import java.util.Collection;
import java.util.List;

/**
 * Implementation of SearchObserver for live UI updates in the AI Search Visualizer.
 * <p>
 * Updates the graph visualization and control panel metrics in response to search algorithm events.
 * Handles step-by-step updates and final result display, including path highlighting and metrics.
 */
public class SearchObserverImpl implements SearchObserver {

    /** Visualizer for updating the graph display. */
    private final GraphStreamVisualizer visualizer;
    /** Control panel for updating metrics and UI controls. */
    private final SearchControlsPanel controls;
    /** The goal node for the current search. */
    private final Node goal;

    /**
     * Constructs a SearchObserverImpl for UI updates.
     *
     * @param visualizer The graph visualizer
     * @param controls   The control panel for metrics and controls
     * @param goal       The goal node for the search
     */
    public SearchObserverImpl(GraphStreamVisualizer visualizer, SearchControlsPanel controls, Node goal) {
        this.visualizer = visualizer;
        this.controls = controls;
        this.goal = goal;
    }

    /**
     * Called on each search step to update the UI.
     * <p>
     * Updates node states, frontier list, and live metrics (nodes expanded, path cost, depth, heuristic values).
     *
     * @param current       The current node being expanded
     * @param frontier      The current frontier (open list)
     * @param explored      The set of explored nodes
     * @param nodesExpanded Number of nodes expanded so far
     * @param pathCost      Cost from start to current node
     * @param solutionDepth Depth of the current node
     * @param g             Cost from start to current node
     * @param h             Heuristic estimate to goal
     * @param f             Total estimated cost (g + h)
     */
    @Override
    public void onStep(Node current,
                       Collection<Node> frontier,
                       Collection<Node> explored,
                       int nodesExpanded,
                       double pathCost,
                       int solutionDepth,
                       double g,
                       double h,
                       double f) {
        Platform.runLater(() -> {
            visualizer.updateNodeStates(
                    frontier == null ? List.of() : List.copyOf(frontier),
                    explored == null ? List.of() : List.copyOf(explored),
                    current,
                    goal,
                    null
            );

            // open list ordering
            controls.openListView.getItems().setAll(
                    frontier == null ? List.of() :
                            frontier.stream().map(Node::getName).toList()
            );

            // live metrics
            controls.nodesExpandedLabel.setText("Nodes Expanded: " + nodesExpanded);
            controls.pathCostLabel.setText(String.format("Path Cost: %.2f", pathCost));
            controls.depthLabel.setText("Solution Depth: " + solutionDepth);

            // heuristic display (works for uninformed too; will show g/h/f as given by algorithm)
            controls.heuristicLabel.setText(String.format("Heuristic (current): g=%.2f, h=%.2f, f=%.2f", g, h, f));
        });
    }

    /**
     * Called when the search finishes to update the UI with final results.
     * <p>
     * Highlights the final path and updates all metrics (nodes expanded, cost, depth, runtime, memory).
     *
     * @param path               The solution path
     * @param totalNodesExpanded Total nodes expanded during the search
     * @param totalNodesGenerated Total nodes generated during the search
     * @param maxFrontierSize    Maximum frontier size during the search
     * @param totalCost          Total cost of the solution path
     * @param solutionDepth      Depth of the solution path
     * @param elapsedTimeMs      Elapsed runtime in milliseconds
     * @param memoryBytes        Memory usage in bytes
     */
    @Override
    public void onFinish(List<Node> path,
                         int totalNodesExpanded,
                         int totalNodesGenerated,
                         int maxFrontierSize,
                         double totalCost,
                         int solutionDepth,
                         long elapsedTimeMs,
                         long memoryBytes) {
        Platform.runLater(() -> {
            visualizer.highlightPath(path);
            controls.statusLabel.setText("✅ Search complete!");

            controls.nodesExpandedLabel.setText("Nodes Expanded: " + totalNodesExpanded);
            controls.pathCostLabel.setText(String.format("Path Cost: %.2f", totalCost));
            controls.depthLabel.setText("Solution Depth: " + solutionDepth);
            controls.timeLabel.setText("Runtime: " + elapsedTimeMs + " ms");
            controls.memoryLabel.setText(String.format("Memory: %.2f KB", memoryBytes / 1024.0));
        });

    }
}
