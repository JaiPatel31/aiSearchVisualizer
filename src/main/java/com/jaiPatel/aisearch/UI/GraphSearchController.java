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
import javafx.scene.layout.GridPane;
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
    private final ComboBox<String> graphModeBox = new ComboBox<>();
    private final Button generateGraphButton = new Button("Generate Random Graph");
    private final ComboBox<String> heuristicBox = new ComboBox<>();

    //metrics panel
    private Label nodesExpandedLabel = new Label("Nodes Expanded: 0");
    private Label pathCostLabel = new Label("Path Cost: 0.0");
    private Label depthLabel = new Label("Solution Depth: 0");
    private Label timeLabel = new Label("Time: 0 ms");
    private Label memoryLabel = new Label("Memory: 0 KB");
    private Label heuristicLabel = new Label("Heuristic (avg): —");
    private ListView<String> openListView = new ListView<>();

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
        algorithmBox.setValue("BFS"); // default

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

        // --- Random Graph Controls ---
        graphModeBox.getItems().addAll("Use Existing Graph", "Generate Random Graph");
        graphModeBox.setValue("Use Existing Graph");

        generateGraphButton.setOnAction(e -> showRandomGraphPopup());

// --- Heuristic Controls ---
        heuristicBox.getItems().addAll("Euclidean", "Manhattan", "Zero (Uninformed)");
        heuristicBox.setValue("Euclidean");
        heuristicBox.setDisable(true); // Only active for A* or Best-First

        VBox controls = new VBox(10,
                new Label("Graph Mode:"), graphModeBox,
                generateGraphButton,
                new Label("Start Node:"), startBox,
                new Label("Goal Node:"), goalBox,
                new Label("Search Algorithm:"), algorithmBox,
                new Label("Heuristic:"), heuristicBox,
                playButton, pauseButton, stepButton, restartButton,
                new Label("Speed (ms/step):"), speedSlider,
                metricsLabel
        );

        Label openListLabel = new Label("Frontier (Open List):");
        openListLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        openListView.setPrefHeight(120);
        openListView.setPlaceholder(new Label("Empty"));
        VBox openListPanel = new VBox(5, openListLabel, openListView);
        openListPanel.setStyle("-fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10px;");


        VBox summaryPanel = createSummaryPanel();
        VBox legendPanel = createLegendPanel();

        VBox rightContent = new VBox(20, controls,openListPanel, summaryPanel, legendPanel);
        rightContent.setPrefWidth(280);
        rightContent.setStyle("-fx-padding: 10;");

        ScrollPane rightScroll = new ScrollPane(rightContent);
        rightScroll.setFitToWidth(true);
        rightScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        rightScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        root.setRight(rightScroll);

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
            case "BFS" -> {
                algorithm = new BFS();
                heuristicBox.setDisable(true);
            }
            case "DFS" -> {
                algorithm = new DFS();
                heuristicBox.setDisable(true);
            }
            case "IDDFS" -> {
                algorithm = new IDDFS();
                heuristicBox.setDisable(true);
            }
            case "Best-First Search" -> {
                heuristicBox.setDisable(false);
                algorithm = new BestFirstSearch(getSelectedHeuristic());
            }
            case "A*" -> {
                heuristicBox.setDisable(false);
                algorithm = new AStarSearch(getSelectedHeuristic());
            }
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
                    openListView.getItems().setAll(
                            frontier.stream()
                                    .map(Node::getName)
                                    .toList());
                    updateMetrics(nodesExpanded, pathCost, solutionDepth);
                });
            }

            @Override
            public void onFinish(List<Node> path,
                                 int nodesExpanded,
                                 int nodesGenerated,
                                 int maxFrontierSize,
                                 double totalCost,
                                 int solutionDepth,
                                 long runtimeMs,
                                 long memoryBytes) {
                Platform.runLater(() -> {
                    System.out.println("Highlighting path!");
                    timeline.stop();
                    visualizer.highlightPath(path);
                    updateFinalMetrics(nodesExpanded, nodesGenerated, maxFrontierSize,
                            totalCost, solutionDepth, runtimeMs, memoryBytes);

                    metricsLabel.setText(String.format(
                            "✅ Search complete!\nAlgorithm: %s\nNodes Expanded: %d\nPath Cost: %.2f\nPath Length: %d",
                            algorithmBox.getValue(),
                            nodesExpanded,
                            totalCost,
                            (path != null ? path.size() : 0)
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

        // Clear labels
        metricsLabel.setText("Ready for new search...");
        nodesExpandedLabel.setText("Nodes Expanded: 0");
        pathCostLabel.setText("Path Cost: 0.0");
        depthLabel.setText("Solution Depth: 0");
        timeLabel.setText("Runtime: 0 ms");
        memoryLabel.setText("Memory: 0 KB");
        heuristicLabel.setText("Heuristic (avg): —");

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
    private void updateFinalMetrics(int nodesExpanded, int nodesGenerated,
                                    int maxFrontier, double totalCost,
                                    int solutionDepth, long runtimeMs,
                                    long memoryBytes) {
        Platform.runLater(() -> {
            nodesExpandedLabel.setText("Nodes Expanded: " + nodesExpanded);
            pathCostLabel.setText(String.format("Path Cost: %.2f", totalCost));
            depthLabel.setText("Solution Depth: " + solutionDepth);
            timeLabel.setText("Runtime: " + runtimeMs + " ms");
            memoryLabel.setText(String.format("Memory: %.2f KB", memoryBytes / 1024.0));
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

    private Heuristic getSelectedHeuristic() {
        return switch (heuristicBox.getValue()) {
            case "Manhattan" -> new ManhattanHeuristic();
            case "Zero (Uninformed)" ->new ZeroHeuristic(); // Lambda for uninformed search
            case "Euclidean" -> new EuclideanHeuristic();
            case "Chevyshev" -> new ChebyshevHeuristic();
            default -> new EuclideanHeuristic();
        };
    }

    private void showRandomGraphPopup() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Random Graph Generator");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("General Weighted Graph", "Grid World");
        typeBox.setValue("General Weighted Graph");

        // Shared seed
        TextField seedField = new TextField(String.valueOf(System.currentTimeMillis()));

        // --- General graph section ---
        GridPane generalPane = new GridPane();
        generalPane.setHgap(10);
        generalPane.setVgap(10);

        TextField nodesField = new TextField("10");
        TextField branchingField = new TextField("3");
        TextField minWeightField = new TextField("1");
        TextField maxWeightField = new TextField("10");

        generalPane.addRow(0, new Label("Number of Nodes:"), nodesField);
        generalPane.addRow(1, new Label("Branching Factor:"), branchingField);
        generalPane.addRow(2, new Label("Min Edge Weight:"), minWeightField);
        generalPane.addRow(3, new Label("Max Edge Weight:"), maxWeightField);

        // --- Grid world section ---
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField gridSizeField = new TextField("10");
        TextField densityField = new TextField("0.2");
        ComboBox<String> connectivityBox = new ComboBox<>();
        connectivityBox.getItems().addAll("4-connected (Manhattan)", "8-connected (Diagonal)");
        connectivityBox.setValue("4-connected (Manhattan)");
        CheckBox weightedCheck = new CheckBox("Use weighted costs");
        weightedCheck.setSelected(false);

        gridPane.addRow(0, new Label("Grid Size (NxN):"), gridSizeField);
        gridPane.addRow(1, new Label("Obstacle Density (0–1):"), densityField);
        gridPane.addRow(2, new Label("Connectivity:"), connectivityBox);
        gridPane.addRow(3, weightedCheck);

        // --- Root layout ---
        VBox rootBox = new VBox(12);
        rootBox.getChildren().addAll(
                new HBox(10, new Label("Graph Type:"), typeBox),
                generalPane,
                gridPane,
                new HBox(10, new Label("Seed:"), seedField)
        );

        // show only one section
        gridPane.setVisible(false);
        typeBox.setOnAction(e -> {
            boolean isGrid = typeBox.getValue().equals("Grid World");
            generalPane.setVisible(!isGrid);
            gridPane.setVisible(isGrid);
        });

        dialog.getDialogPane().setContent(rootBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                long seed = Long.parseLong(seedField.getText());
                if (typeBox.getValue().equals("General Weighted Graph")) {
                    int n = Integer.parseInt(nodesField.getText());
                    int b = Integer.parseInt(branchingField.getText());
                    int minW = Integer.parseInt(minWeightField.getText());
                    int maxW = Integer.parseInt(maxWeightField.getText());
                    this.graph = RandomGraphGenerator.generate(n, b, minW, maxW, seed);
                } else {
                    int size = Integer.parseInt(gridSizeField.getText());
                    double density = Double.parseDouble(densityField.getText());
                    boolean diagonal = connectivityBox.getValue().contains("8");
                    boolean weighted = weightedCheck.isSelected();
                    this.graph = GridGraphGenerator.generateGrid(size, density, diagonal, weighted, seed);
                }

                this.visualizer = new GraphStreamVisualizer(graph);
                BorderPane root = (BorderPane) playButton.getScene().getRoot();
                root.setCenter(visualizer.getView());
                visualizer.resetGraph();
                startBox.getItems().setAll(graph.getNodes().stream().map(Node::getName).toList());
                goalBox.getItems().setAll(graph.getNodes().stream().map(Node::getName).toList());
            }
            return null;
        });

        dialog.showAndWait();
    }


}
