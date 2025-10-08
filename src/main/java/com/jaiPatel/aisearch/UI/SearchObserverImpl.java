package com.jaiPatel.aisearch.UI;

import com.jaiPatel.aisearch.algorithms.SearchObserver;
import com.jaiPatel.aisearch.graph.Node;
import javafx.application.Platform;

import java.util.Collection;
import java.util.List;

public class SearchObserverImpl implements SearchObserver {

    private final GraphStreamVisualizer visualizer;
    private final SearchControlsPanel controls;
    private final Node goal;

    public SearchObserverImpl(GraphStreamVisualizer visualizer, SearchControlsPanel controls, Node goal) {
        this.visualizer = visualizer;
        this.controls = controls;
        this.goal = goal;
    }

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

