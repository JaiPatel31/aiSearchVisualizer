package com.jaiPatel.aisearch.UI;

import com.jaiPatel.aisearch.benchmark.BenchmarkHarness;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class BenchmarkUI {

    public static void showResults(List<BenchmarkHarness.Result> results) {
        Stage stage = new Stage();
        stage.setTitle("Benchmark Results");

        TableView<BenchmarkHarness.Result> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<BenchmarkHarness.Result, String> algoCol = new TableColumn<>("Algorithm");
        algoCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().algorithm()));

        TableColumn<BenchmarkHarness.Result, String> timeCol = new TableColumn<>("Time (ms ± std)");
        timeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f ± %.2f", d.getValue().meanTimeMs(), d.getValue().stdTimeMs())
        ));

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
