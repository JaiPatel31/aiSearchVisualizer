package com.jaiPatel.aisearch.UI;

import com.jaiPatel.aisearch.algorithms.*;
import com.jaiPatel.aisearch.graph.*;
import com.jaiPatel.aisearch.heuristics.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;

import java.util.Collection;
import java.util.List;

/**
 * Controller connecting UI controls and visualization to incremental search algorithms.
 */
public class GraphSearchController {
    private Graph graph;
    private GraphStreamVisualizer visualizer;
    private Timeline timeline;
    private SearchAlgorithm algorithm;

    private Node startNode;
    private Node goalNode;

    // UI controls
    private final ComboBox<String> startBox = new ComboBox<>();
    private final ComboBox<String> goalBox = new ComboBox<>();
    private final ComboBox<String> algorithmBox = new ComboBox<>();
    private final Button playButton = new Button("Play");
    private final Button pauseButton = new Button("Pause");
    private final Button stepButton = new Button("Step");
    private final Button restartButton = new Button("Restart");
    private final Slider speedSlider = new Slider(100, 2000, 500); // ms per step
    private final Label metricsLabel = new Label();


    //metrics panel
    private Label nodesExpandedLabel = new Label("Nodes Expanded: 0");
    private Label pathCostLabel = new Label("Path Cost: 0.0");
    private Label depthLabel = new Label("Solution Depth: 0");
    private Label timeLabel = new Label("Time: 0 ms");
    private Label memoryLabel = new Label("Memory: 0 KB");
    private Label heuristicLabel = new Label("Heuristic (avg): —");

    // Observer instance used by all algorithms
    private SearchObserver observer;

    public BorderPane createUI(Graph graph, SearchAlgorithm defaultAlgorithm) {
        this.graph = graph;
        this.algorithm = defaultAlgorithm;
        this.visualizer = new GraphStreamVisualizer(graph);

        BorderPane root = new BorderPane();
        root.setCenter(visualizer.getView());

        // Populate dropdowns
        startBox.getItems().addAll(graph.getNodes().stream().map(Node::getName).toList());
        goalBox.getItems().addAll(graph.getNodes().stream().map(Node::getName).toList());

        // Add algorithm selection
        algorithmBox.getItems().addAll("BFS", "DFS", "IDDFS", "Best-First Search", "A*");
        algorithmBox.setValue("A*"); // default

        // Control actions
        playButton.setOnAction(e -> {
            if (startBox.getValue() == null || goalBox.getValue() == null) {
                showAlert("Please select both a start and goal node before running the search.");
                return;
            }

            // Initialize search if not yet started
            if (timeline == null || timeline.getStatus() == Timeline.Status.STOPPED ) {
                startSearch();
            }
            timeline.play();
        });
        pauseButton.setOnAction(e -> timeline.pause());
        stepButton.setOnAction(e -> {
            if (startBox.getValue() == null || goalBox.getValue() == null) {
                showAlert("Please select both a start and goal node before stepping through the search.");
                return;
            }

            // Initialize if not already done
            if (timeline == null || timeline.getStatus() == Timeline.Status.STOPPED) {
                startSearch();
                timeline.pause();
            }
            runStep();
        });
        restartButton.setOnAction(e -> restartSearch());
        algorithmBox.setOnAction(e -> updateAlgorithm());

        VBox controls = new VBox(10,
                new Label("Start Node:"), startBox,
                new Label("Goal Node:"), goalBox,
                new Label("Search Algorithm:"), algorithmBox,
                playButton, pauseButton, stepButton, restartButton,
                new Label("Speed (ms/step):"), speedSlider,
                metricsLabel
        );

        VBox summaryPanel = createSummaryPanel();
        VBox legendPanel = createLegendPanel();

        VBox rightPanel = new VBox(20, controls, summaryPanel, legendPanel);
        rightPanel.setPrefWidth(280);
        root.setRight(rightPanel);

        setupTimeline();
        return root;

    }

    // --- Timeline Setup ---
    private void setupTimeline() {
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame stepFrame = new KeyFrame(Duration.millis(speedSlider.getValue()), e -> runStep());
        timeline.getKeyFrames().setAll(stepFrame);

        // When user adjusts the slider, just update the KeyFrame rate — don’t recreate the whole timeline
        speedSlider.valueProperty().addListener((obs, oldV, newV) -> {
            double newDelay = newV.doubleValue();
            boolean wasRunning = timeline.getStatus() == Timeline.Status.RUNNING;
            timeline.stop();
            timeline.getKeyFrames().setAll(
                    new KeyFrame(Duration.millis(newDelay), e -> runStep())
            );
            if (wasRunning) timeline.play();
        });
    }

    // --- Algorithm Selection ---
    private void updateAlgorithm() {
        String selected = algorithmBox.getValue();
        switch (selected) {
            case "BFS" -> algorithm = new BFS();
            case "DFS" -> algorithm = new DFS();
            case "IDDFS" -> algorithm = new IDDFS();
            case "Best-First Search" -> algorithm = new BestFirstSearch(new EuclideanHeuristic());
            case "A*" -> algorithm = new AStarSearch(new EuclideanHeuristic());
            default -> showAlert("Unknown algorithm: " + selected);
        }
    }

    // --- Initialize and Start ---
    private void startSearch() {
        if (startBox.getValue() == null || goalBox.getValue() == null) {
            showAlert("Please select both a start and goal node before running the search.");
            return;
        }

        startNode = graph.getNode(startBox.getValue());
        goalNode = graph.getNode(goalBox.getValue());
        visualizer.resetGraph();
        metricsLabel.setText("Search running...");

        // Create observer for visualization
        observer = new SearchObserver() {
            @Override
            public void onStep(Node current, Collection<Node> frontier, Collection<Node> explored,
                               int nodesExpanded, double pathCost, int solutionDepth,
                               double g, double h, double f) {
                Platform.runLater(() -> {
                    visualizer.updateNodeStates(List.copyOf(frontier),
                            List.copyOf(explored),
                            current,
                            goalNode,
                            null);
                    updateMetrics(nodesExpanded, pathCost, solutionDepth);
                });
            }

            @Override
            public void onFinish(List<Node> path, int nodesExpanded, double totalCost) {
                Platform.runLater(() -> {
                    timeline.stop();
                    visualizer.highlightPath(path);
                    metricsLabel.setText(String.format(
                            "✅ Search complete!\nAlgorithm: %s\nNodes expanded: %d\nPath cost: %.2f\nPath length: %d",
                            algorithmBox.getValue(),
                            nodesExpanded,
                            totalCost,
                            path != null ? path.size() : 0
                    ));
                });
            }
        };

        // Initialize incremental search
        algorithm.initialize(graph, startNode, goalNode, observer);
        timeline.play();
    }

    // --- Run One Step ---
    private void runStep() {
        if (algorithm == null || algorithm.isFinished()) {
            timeline.stop();
            return;
        }

        boolean moreSteps = algorithm.step();
        if (!moreSteps || algorithm.isFinished()) {
            timeline.stop();
        }
    }

    // --- Restart ---
    private void restartSearch() {
        if (timeline != null) timeline.stop();
        visualizer.resetGraph();
        metricsLabel.setText("");
    }


    // --- Alert Helper ---
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private VBox createSummaryPanel() {
        Label title = new Label("📊 Search Summary");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox box = new VBox(5,
                title,
                nodesExpandedLabel,
                pathCostLabel,
                depthLabel,
                timeLabel,
                memoryLabel,
                heuristicLabel
        );
        box.setStyle("-fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");
        return box;
    }

    private void updateMetrics(int nodesExpanded, double pathCost, int solutionDepth) {
        Platform.runLater(() -> {
            nodesExpandedLabel.setText("Nodes Expanded: " + nodesExpanded);
            pathCostLabel.setText(String.format("Path Cost: %.2f", pathCost));
            depthLabel.setText("Solution Depth: " + solutionDepth);
        });
    }
    private VBox createLegendPanel() {
        Label title = new Label("Legend");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox box = new VBox(5);
        box.getChildren().add(title);

        box.getChildren().add(createLegendItem(Color.ORANGE, "Current Node"));
        box.getChildren().add(createLegendItem(Color.YELLOW, "Frontier (Open List)"));
        box.getChildren().add(createLegendItem(Color.GREEN, "Visited (Explored)"));
        box.getChildren().add(createLegendItem(Color.RED, "Goal Node"));
        box.getChildren().add(createLegendItem(Color.PURPLE, "Final Path"));
        box.getChildren().add(createLegendItem(Color.BLACK, "Blocked Node"));

        box.setStyle("-fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");
        return box;
    }

    private HBox createLegendItem(Color color, String text) {
        Rectangle rect = new Rectangle(15, 15, color);
        Label label = new Label(text);
        label.setStyle("-fx-padding: 0 0 0 5px;"); // small space between rectangle and text
        HBox hbox = new HBox(rect, label);
        return hbox;
    }
}
