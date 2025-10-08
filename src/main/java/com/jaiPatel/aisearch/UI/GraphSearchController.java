package com.jaiPatel.aisearch.UI;

import com.jaiPatel.aisearch.algorithms.*;
import com.jaiPatel.aisearch.benchmark.BatchBenchmarkRunner;
import com.jaiPatel.aisearch.benchmark.BenchmarkHarness;
import com.jaiPatel.aisearch.benchmark.BenchmarkUtils;
import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;
import com.jaiPatel.aisearch.heuristics.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

public class GraphSearchController {

    private Graph graph;
    private SearchAlgorithm algorithm;
    private GraphStreamVisualizer visualizer;

    private final SearchControlsPanel controls = new SearchControlsPanel();
    private Timeline timeline;

    public BorderPane createUI(Graph graph, SearchAlgorithm defaultAlgorithm) {
        this.graph = graph;
        this.algorithm = defaultAlgorithm;
        this.visualizer = new GraphStreamVisualizer(graph);

        BorderPane root = new BorderPane();
        root.setCenter(visualizer.getView());
        root.setRight(controls.build());

        // populate start/goal
        refreshNodePickers();

        // wire algorithm selector
        controls.algorithmBox.setOnAction(e -> selectAlgorithm());

        // buttons
        controls.playButton.setOnAction(e -> {
            if (!ensureStartGoal()) return;
            if (timeline == null || timeline.getStatus() == Timeline.Status.STOPPED) {
                startSearch();
            }
            timeline.play();
        });

        controls.pauseButton.setOnAction(e -> {
            if (timeline != null) timeline.pause();
        });

        controls.stepButton.setOnAction(e -> {
            if (!ensureStartGoal()) return;
            if (timeline == null || timeline.getStatus() == Timeline.Status.STOPPED) {
                startSearch();
                timeline.pause();
            }
            runStep();
        });

        controls.restartButton.setOnAction(e -> {
            if (timeline != null) timeline.stop();
            visualizer.resetGraph();
            controls.resetMetrics();
        });

        controls.generateGraphButton.setOnAction(e -> {
            Graph newGraph = new GraphGeneratorDialog().showDialog();
            if (newGraph != null) updateGraph(newGraph, root);
        });
        controls.runBenchmarkButton.setOnAction(e -> {runBatchBenchmark();
        });

        // speed slider
        setupTimeline();

        // heuristic dropdown enabled only for A*/Best-First
        selectAlgorithm();

        return root;
    }

    private void setupTimeline() {
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(controls.speedSlider.getValue()), e -> runStep());
        timeline.getKeyFrames().setAll(frame);

        controls.speedSlider.valueProperty().addListener((obs, ov, nv) -> {
            boolean running = timeline.getStatus() == Timeline.Status.RUNNING;
            timeline.stop();
            timeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(nv.doubleValue()), e -> runStep()));
            if (running) timeline.play();
        });
    }

    private void selectAlgorithm() {
        String sel = controls.algorithmBox.getValue();
        switch (sel) {
            case "BFS" -> {
                algorithm = new BFS();
                controls.heuristicBox.setDisable(true);
            }
            case "DFS" -> {
                algorithm = new DFS();
                controls.heuristicBox.setDisable(true);
            }
            case "IDDFS" -> {
                algorithm = new IDDFS();
                controls.heuristicBox.setDisable(true);
            }
            case "Best-First Search" -> {
                controls.heuristicBox.setDisable(false);
                algorithm = new BestFirstSearch(getSelectedHeuristic());
            }
            case "A*" -> {
                controls.heuristicBox.setDisable(false);
                algorithm = new AStarSearch(getSelectedHeuristic());
            }
            default -> showAlert("Unknown algorithm: " + sel);
        }
    }

    private Heuristic getSelectedHeuristic() {
        return switch (controls.heuristicBox.getValue()) {
            case "Manhattan" -> new ManhattanHeuristic();
            case "Chebyshev" -> new ChebyshevHeuristic();
            case "Zero (Uninformed)" -> new ZeroHeuristic();
            case "Euclidean" -> new EuclideanHeuristic();
            default -> new EuclideanHeuristic();
        };
    }

    private void startSearch() {
        Node start = graph.getNode(controls.startBox.getValue());
        Node goal  = graph.getNode(controls.goalBox.getValue());

        visualizer.resetGraph();
        controls.statusLabel.setText("Search running...");

        SearchObserverImpl observer = new SearchObserverImpl(visualizer, controls, goal);
        algorithm.initialize(graph, start, goal, observer);
    }

    private void runStep() {
        if (algorithm == null || algorithm.isFinished()) {
            if (timeline != null) timeline.stop();
            return;
        }
        boolean more = algorithm.step();
        if (!more || algorithm.isFinished()) {
            if (timeline != null) timeline.stop();
        }
    }

    private boolean ensureStartGoal() {
        if (controls.startBox.getValue() == null || controls.goalBox.getValue() == null) {
            showAlert("Please select both a start and goal node.");
            return false;
        }
        return true;
    }

    private void updateGraph(Graph newGraph, BorderPane root) {
        this.graph = newGraph;
        this.visualizer = new GraphStreamVisualizer(graph);
        root.setCenter(visualizer.getView());
        visualizer.resetGraph();
        refreshNodePickers();
        controls.resetMetrics();
        if (timeline != null) timeline.stop();
    }

    private void refreshNodePickers() {
        var names = graph.getNodes().stream().map(Node::getName).toList();
        controls.startBox.getItems().setAll(names);
        controls.goalBox.getItems().setAll(names);
        if (!names.isEmpty()) {
            controls.startBox.setValue(names.get(0));
            controls.goalBox.setValue(names.get(Math.max(0, names.size() - 1)));
        }
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setHeaderText(null);
            a.setContentText(msg);
            a.showAndWait();
        });
    }

    private void runBatchBenchmark() {
        controls.statusLabel.setText("Running batch benchmarks...");
        controls.runBenchmarkButton.setDisable(true);

        new Thread(() -> {
            var results = BatchBenchmarkRunner.runDifficultySuite();
            Platform.runLater(() -> {
                controls.benchmarkSummaryLabel.setText("✅ Batch complete! See console or chart view for details.");
                // Optional: save results to file
                BenchmarkUtils.saveResultsToCSV(results, "benchmark_results.csv");
                BenchmarkUtils.showResultsChart(results);
                controls.runBenchmarkButton.setDisable(false);
            });
        }).start();
    }

}
