package com.jaiPatel.aisearch.benchmark;

import com.jaiPatel.aisearch.algorithms.*;
import com.jaiPatel.aisearch.graph.*;

import java.util.*;

public class BenchmarkHarness {

    public record Result(
            String algorithm,
            double meanTimeMs,
            double stdTimeMs,
            double meanMemKB,
            double stdMemKB,
            int nodesExpanded,
            int nodesGenerated,
            int maxFrontier,
            int solutionDepth,
            double pathCost
    ) {}

    public static List<Result> runBenchmarks(
            Graph graph,
            Node start,
            Node goal,
            List<? extends AbstractSearchAlgorithm> algorithms,
            int repeats
    ) {
        List<Result> results = new ArrayList<>();

        for (AbstractSearchAlgorithm algo : algorithms) {
            System.out.println("Running " + algo.getClass().getSimpleName());

            List<Double> times = new ArrayList<>();
            List<Double> memories = new ArrayList<>();
            int totalExpanded = 0, totalGenerated = 0, totalFrontier = 0, totalDepth = 0;
            double totalCost = 0.0;

            for (int i = 0; i < repeats; i++) {
                BenchmarkObserver observer = new BenchmarkObserver();
                observer.start();

                try {
                    algo.initialize(graph, start, goal, observer);
                    while (!algo.isFinished() && algo.step()) {
                        // step through
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Error during " + algo.getClass().getSimpleName() + ": " + e.getMessage());
                    continue;
                }

                times.add(observer.getRuntimeMs());
                memories.add(observer.getMemoryKB());
                totalExpanded += observer.getNodesExpanded();
                totalGenerated += observer.getNodesGenerated();
                totalFrontier += observer.getMaxFrontier();
                totalDepth += observer.getSolutionDepth();
                totalCost += observer.getTotalCost();
            }

            double meanTime = mean(times);
            double stdTime = std(times);
            double meanMem = mean(memories);
            double stdMem = std(memories);

            results.add(new Result(
                    algo.getClass().getSimpleName(),
                    round(meanTime, 2),
                    round(stdTime, 2),
                    round(meanMem, 2),
                    round(stdMem, 2),
                    totalExpanded / repeats,
                    totalGenerated / repeats,
                    totalFrontier / repeats,
                    totalDepth / repeats,
                    round(totalCost / repeats, 2)
            ));
        }

        // Print results neatly
        System.out.println("\nAlgorithm,MeanTime(ms),Std(ms),MeanMem(KB),StdMem(KB),Expanded,Generated,Frontier,Depth,Cost");
        for (Result r : results) {
            System.out.printf(
                    "%s,%.2f,%.2f,%.2f,%.2f,%d,%d,%d,%d,%.2f%n",
                    r.algorithm(), r.meanTimeMs(), r.stdTimeMs(),
                    r.meanMemKB(), r.stdMemKB(),
                    r.nodesExpanded(), r.nodesGenerated(),
                    r.maxFrontier(), r.solutionDepth(), r.pathCost()
            );
        }

        return results;
    }

    private static double mean(List<Double> list) {
        return list.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    private static double std(List<Double> list) {
        double mean = mean(list);
        return Math.sqrt(list.stream().mapToDouble(v -> Math.pow(v - mean, 2)).average().orElse(0));
    }

    private static double round(double val, int places) {
        double scale = Math.pow(10, places);
        return Math.round(val * scale) / scale;
    }
}
