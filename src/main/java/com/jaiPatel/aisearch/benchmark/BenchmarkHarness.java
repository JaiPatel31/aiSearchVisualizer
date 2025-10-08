package com.jaiPatel.aisearch.benchmark;

import com.jaiPatel.aisearch.algorithms.*;
import com.jaiPatel.aisearch.graph.*;

import java.util.*;

public class BenchmarkHarness {
    public record Result(String algorithm, double meanTimeMs, double stdTimeMs,
                         double meanMemKB, double stdMemKB,
                         int nodesExpanded, int nodesGenerated,
                         int maxFrontier, int solutionDepth, double pathCost) {}

    public static List<Result> runBenchmarks(Graph graph, Node start, Node goal,
                                             List<? extends SearchAlgorithm> algorithms,
                                             int repeats) {
        List<Result> results = new ArrayList<>();

        for (SearchAlgorithm algo : algorithms) {
            System.out.println("Running " + algo.getClass().getSimpleName());
            List<Double> times = new ArrayList<>();
            List<Double> memories = new ArrayList<>();
            int totalExpanded = 0, totalGenerated = 0, totalFrontier = 0, totalDepth = 0;
            double totalCost = 0.0;

            for (int i = 0; i < repeats; i++) {
                // --- initialize per-run observer ---
                BenchmarkObserver observer = new BenchmarkObserver();
                observer.start();

                long startTime = System.nanoTime();

                algo.initialize(graph, start, goal, observer);

                int safetyCounter = 0; // avoid infinite loop if algorithm fails
                while (!algo.isFinished() && safetyCounter++ < 10_000_000) {
                    algo.step();
                }

                long endTime = System.nanoTime();

                // --- stable timing & memory measurement ---
                double runtimeMs = (endTime - startTime) / 1_000_000.0;
                double memoryKB = observer.getMemoryKB();
                if (memoryKB <= 0) {
                    memoryKB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0;
                }


                if (Double.isNaN(runtimeMs) || runtimeMs < 0) runtimeMs = 0;
                if (Double.isNaN(memoryKB) || memoryKB < 0) memoryKB = 0;

                times.add(runtimeMs);
                memories.add(memoryKB);

                totalExpanded += observer.getNodesExpanded();
                totalGenerated += observer.getNodesGenerated();
                totalFrontier += observer.getMaxFrontier();
                totalDepth += observer.getSolutionDepth();

                double cost = observer.getTotalCost();
                if (Double.isFinite(cost)) totalCost += cost;
            }

            results.add(new Result(
                    algo.getClass().getSimpleName(),
                    mean(times), std(times),
                    mean(memories), std(memories),
                    totalExpanded / repeats,
                    totalGenerated / repeats,
                    totalFrontier / repeats,
                    totalDepth / repeats,
                    totalCost / repeats
            ));
        }
        return results;
    }

    private static double mean(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private static double std(List<Double> data) {
        double m = mean(data);
        return Math.sqrt(data.stream().mapToDouble(v -> Math.pow(v - m, 2)).average().orElse(0.0));
    }
}
