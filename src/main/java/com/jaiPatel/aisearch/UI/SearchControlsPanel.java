package com.jaiPatel.aisearch.UI;

import com.jaiPatel.aisearch.benchmark.BenchmarkHarness;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class SearchControlsPanel {

    // exposed controls
    public final ComboBox<String> startBox = new ComboBox<>();
    public final ComboBox<String> goalBox = new ComboBox<>();
    public final ComboBox<String> algorithmBox = new ComboBox<>();
    public final ComboBox<String> heuristicBox = new ComboBox<>();
    public final Button playButton = new Button("Play");
    public final Button pauseButton = new Button("Pause");
    public final Button stepButton = new Button("Step");
    public final Button restartButton = new Button("Restart");
    public final Button generateGraphButton = new Button("Generate Random/Grid Graph");
    public final Slider speedSlider = new Slider(100, 2000, 500);
    public final Label statusLabel = new Label("Ready");
    public final ListView<String> openListView = new ListView<>();

    // metrics labels
    public final Label nodesExpandedLabel = new Label("Nodes Expanded: 0");
    public final Label pathCostLabel     = new Label("Path Cost: 0.00");
    public final Label depthLabel        = new Label("Solution Depth: 0");
    public final Label timeLabel         = new Label("Runtime: 0 ms");
    public final Label memoryLabel       = new Label("Memory: 0 KB");
    public final Label heuristicLabel    = new Label("Heuristic (current): —");
    //benchmarking
    final Button runBenchmarkButton = new Button("Run Batch Benchmark");
    final TableView<BenchmarkHarness.Result> benchmarkTable = new TableView<>();
    final Label benchmarkSummaryLabel = new Label();

    public ScrollPane build() {
        // algorithm & heuristic defaults
        algorithmBox.getItems().addAll("BFS", "DFS", "IDDFS", "Best-First Search", "A*");
        algorithmBox.setValue("BFS");

        heuristicBox.getItems().addAll("Euclidean", "Manhattan", "Chebyshev", "Zero (Uninformed)");
        heuristicBox.setValue("Euclidean");
        heuristicBox.setDisable(true); // only for A* / Best-First

        // open list ui
        Label openListLabel = new Label("Frontier (Open List)");
        openListLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        openListView.setPrefHeight(140);
        VBox openListPanel = boxed(openListLabel, openListView);

        // summary/metrics
        Label summaryTitle = new Label("Search Summary");
        summaryTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        VBox summary = boxed(summaryTitle, nodesExpandedLabel, pathCostLabel, depthLabel, timeLabel, memoryLabel, heuristicLabel);

        // legend
        Label legendTitle = new Label("Legend");
        legendTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        VBox legend = boxed(
                legendTitle,
                legendItem(Color.ORANGE, "Current Node"),
                legendItem(Color.YELLOW, "Frontier (Open List)"),
                legendItem(Color.LIGHTGREEN, "Visited (Explored)"),
                legendItem(Color.RED, "Goal Node"),
                legendItem(Color.PURPLE, "Final Path"),
                legendItem(Color.GRAY, "Blocked")
        );
        //benchmarking
        Label benchmarkLabel = new Label("Benchmarking");
        benchmarkLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox benchmarkBox = new VBox(10, benchmarkLabel, runBenchmarkButton, benchmarkTable, benchmarkSummaryLabel);
        benchmarkBox.setStyle("-fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");
        benchmarkTable.setPrefHeight(200);

        VBox controls = new VBox(
                10,
                new Label("Start Node:"), startBox,
                new Label("Goal Node:"), goalBox,
                new Label("Algorithm:"), algorithmBox,
                new Label("Heuristic:"), heuristicBox,
                new Label("Speed (ms/step):"), speedSlider,
                new HBox(8, playButton, pauseButton, stepButton, restartButton),
                generateGraphButton,
                statusLabel,
                openListPanel,
                summary,
                legend,
                benchmarkBox
        );
        controls.setPrefWidth(300);
        controls.setStyle("-fx-padding: 10;");

        ScrollPane scroller = new ScrollPane(controls);
        scroller.setFitToWidth(true);
        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scroller;
    }

    private VBox boxed(javafx.scene.Node... children) {
        VBox box = new VBox(6, children);
        box.setStyle("-fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");
        return box;
    }

    private HBox legendItem(Color color, String text) {
        Rectangle rect = new Rectangle(15, 15, color);
        Label label = new Label(text);
        label.setStyle("-fx-padding: 0 0 0 6px;");
        return new HBox(8, rect, label);
    }

    public void resetMetrics() {
        statusLabel.setText("Ready");
        nodesExpandedLabel.setText("Nodes Expanded: 0");
        pathCostLabel.setText("Path Cost: 0.00");
        depthLabel.setText("Solution Depth: 0");
        timeLabel.setText("Runtime: 0 ms");
        memoryLabel.setText("Memory: 0 KB");
        heuristicLabel.setText("Heuristic (current): —");
        openListView.getItems().clear();
    }


}

