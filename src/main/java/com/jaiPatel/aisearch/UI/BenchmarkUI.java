package com.jaiPatel.aisearch.UI;

import com.jaiPatel.aisearch.benchmark.BenchmarkHarness;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

/**
 * UI utility for displaying benchmark results in a table and bar chart.
 * <p>
 * Shows a JavaFX window with a table of algorithm runtimes and memory usage,
 * and a bar chart visualizing mean runtimes for each algorithm.
 */
public class BenchmarkUI {

    /**
     * Displays benchmark results in a JavaFX window.
     * <p>
     * Shows a table with algorithm name, mean runtime (with std), and mean memory usage (with std).
     * Also displays a bar chart of mean runtimes for each algorithm.
     *
     * @param results List of benchmark results to display
     */
    public static void showResults(List<BenchmarkHarness.Result> results) {
        Stage stage = new Stage();
        stage.setTitle("Benchmark Results");

        TableView<BenchmarkHarness.Result> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Table column for algorithm name
        TableColumn<BenchmarkHarness.Result, String> algoCol = new TableColumn<>("Algorithm");
        algoCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().algorithm()));

        // Table column for mean runtime and standard deviation
        TableColumn<BenchmarkHarness.Result, String> timeCol = new TableColumn<>("Time (ms ± std)");
        timeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f ± %.2f", d.getValue().meanTimeMs(), d.getValue().stdTimeMs())
        ));

        // Table column for mean memory usage and standard deviation
        TableColumn<BenchmarkHarness.Result, String> memCol = new TableColumn<>("Memory (KB ± std)");
        memCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f ± %.2f", d.getValue().meanMemKB(), d.getValue().stdMemKB())
        ));

        table.getColumns().addAll(algoCol, timeCol, memCol);
        table.getItems().addAll(results);

        // Simple runtime chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Mean Runtime (ms)");
        xAxis.setLabel("Algorithm");
        yAxis.setLabel("Time (ms)");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Runtime");
        for (var r : results)
            series.getData().add(new XYChart.Data<>(r.algorithm(), r.meanTimeMs()));
        chart.getData().add(series);

        VBox root = new VBox(10, table, chart);
        root.setPadding(new Insets(10));
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }
}
