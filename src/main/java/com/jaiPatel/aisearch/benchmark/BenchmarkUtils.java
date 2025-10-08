package com.jaiPatel.aisearch.benchmark;

import javafx.scene.chart.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.*;
import java.util.*;

public class BenchmarkUtils {

    public static void saveResultsToCSV(List<BenchmarkHarness.Result> results, String filename) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println("Algorithm,Time(ms),Memory(KB),NodesExpanded,NodesGenerated,FrontierMax,Depth,Cost");
            for (var r : results) {
                pw.printf("%s,%.2f,%.2f,%d,%d,%d,%d,%.2f%n",
                        r.algorithm(), r.meanTimeMs(), r.meanMemKB(),
                        r.nodesExpanded(), r.nodesGenerated(),
                        r.maxFrontier(), r.solutionDepth(), r.pathCost());
            }
            System.out.println("✅ Results saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showResultsChart(List<BenchmarkHarness.Result> results) {
        Map<String, Double> timeMap = new LinkedHashMap<>();
        for (var r : results) timeMap.put(r.algorithm(), r.meanTimeMs());

        CategoryAxis x = new CategoryAxis();
        x.setLabel("Algorithm");

        NumberAxis y = new NumberAxis();
        y.setLabel("Mean Runtime (ms)");

        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle("Runtime Comparison");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (var e : timeMap.entrySet()) {
            series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        chart.getData().add(series);

        Stage stage = new Stage();
        stage.setTitle("Benchmark Results");
        stage.setScene(new Scene(chart, 600, 400));
        stage.show();
    }
}

