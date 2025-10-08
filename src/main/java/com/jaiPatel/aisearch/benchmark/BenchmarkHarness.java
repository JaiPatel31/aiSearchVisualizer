package com.jaiPatel.aisearch.benchmark;

import com.jaiPatel.aisearch.algorithms.*;
import com.jaiPatel.aisearch.graph.*;

import java.util.*;

/**
 * Utility class for running search algorithm benchmarks and aggregating results.
 * <p>
 * Provides methods to run multiple search algorithms on a given graph, collect timing and memory statistics,
 * and compute averages and standard deviations for performance analysis.
 */
public class BenchmarkHarness {
    /**
     * Immutable record representing the results of a benchmark run for a single algorithm.
     *
     * @param algorithm      Name of the algorithm
     * @param meanTimeMs     Mean runtime in milliseconds
     * @param stdTimeMs      Standard deviation of runtime in milliseconds
     * @param meanMemKB      Mean memory usage in kilobytes
     * @param stdMemKB       Standard deviation of memory usage in kilobytes
     * @param nodesExpanded  Number of nodes expanded
     * @param nodesGenerated Number of nodes generated
     * @param maxFrontier    Maximum frontier size
     * @param solutionDepth  Solution path depth
     * @param pathCost       Solution path cost
     */
    public record Result(String algorithm, double meanTimeMs, double stdTimeMs,
                         double meanMemKB, double stdMemKB,
                         int nodesExpanded, int nodesGenerated,
                         int maxFrontier, int solutionDepth, double pathCost) {}

    /**
     * Runs the provided search algorithms on the given graph, collecting performance statistics.
     * <p>
     * For each algorithm, runs the search multiple times, measuring runtime and memory usage,
     * and aggregates metrics such as nodes expanded, generated, frontier size, solution depth, and path cost.
     *
     * @param graph      The graph to search
     * @param start      The start node
     * @param goal       The goal node
     * @param algorithms List of search algorithms to benchmark
     * @param repeats    Number of times to repeat each algorithm
     * @return List of Result objects containing aggregated statistics for each algorithm
     */
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

    /**
     * Computes the mean (average) of a list of double values.
     *
     * @param data List of double values
     * @return Mean value
     */
    private static double mean(List<Double> data) {
        return data.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    /**
     * Computes the standard deviation of a list of double values.
     *
     * @param data List of double values
     * @return Standard deviation
     */
    private static double std(List<Double> data) {
        double m = mean(data);
        return Math.sqrt(data.stream().mapToDouble(v -> Math.pow(v - m, 2)).average().orElse(0.0));
    }
}
