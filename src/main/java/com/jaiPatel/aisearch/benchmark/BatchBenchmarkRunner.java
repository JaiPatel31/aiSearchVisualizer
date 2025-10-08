package com.jaiPatel.aisearch.benchmark;

import com.jaiPatel.aisearch.algorithms.*;
import com.jaiPatel.aisearch.graph.*;
import com.jaiPatel.aisearch.heuristics.*;

import java.util.*;

/**
 * Runs batch benchmarks over multiple difficulty levels (easy/medium/hard).
 * Each difficulty is defined by grid size and obstacle density.
 * Runs each algorithm multiple times with different seeds and averages results.
 */
public class BatchBenchmarkRunner {

    public static List<BenchmarkHarness.Result> runDifficultySuite() {
        List<BenchmarkHarness.Result> allResults = new ArrayList<>();

        int repeats = 5;
        Random rand = new Random(42); // fixed seed for reproducibility

        // --- Difficulty levels (grid size + obstacle density) ---
        var difficultyLevels = List.of(
                new Difficulty("Easy", 8, 0.1),
                new Difficulty("Medium", 12, 0.25),
                new Difficulty("Hard", 16, 0.4)
        );

        // --- Algorithms to test ---
        List<AbstractSearchAlgorithm> algorithms = List.of(
                new BFS(),
                new DFS(),
                new IDDFS(),
                new BestFirstSearch(new EuclideanHeuristic()),
                new AStarSearch(new EuclideanHeuristic())
        );

        // --- Main benchmark loop ---
        for (Difficulty d : difficultyLevels) {
            System.out.println("\n=== Running " + d.label + " benchmark ===");

            for (int run = 1; run <= repeats; run++) {
                long seed = rand.nextLong();

                Graph g = GridGraphGenerator.generateGrid(
                        d.size, d.density,
                        true, true, seed
                );

                List<Node> nodes = new ArrayList<>(g.getNodes());
                if (nodes.size() < 2) {
                    System.out.println("⚠️ Skipping run — graph too small or disconnected.");
                    continue;
                }

                Node start = nodes.get(0);
                Node goal = nodes.get(nodes.size() - 1);

                System.out.println("-- Seed " + seed + " (" + d.label + ") --");

                // Run all algorithms once on this instance
                List<BenchmarkHarness.Result> runResults =
                        BenchmarkHarness.runBenchmarks(g, start, goal, algorithms, 1);

                // Label results with difficulty name
                for (BenchmarkHarness.Result r : runResults) {
                    allResults.add(new BenchmarkHarness.Result(
                            d.label + " - " + r.algorithm(),
                            r.meanTimeMs(), r.stdTimeMs(),
                            r.meanMemKB(), r.stdMemKB(),
                            r.nodesExpanded(), r.nodesGenerated(),
                            r.maxFrontier(), r.solutionDepth(), r.pathCost()
                    ));
                }
            }
        }

        System.out.println("\n✅ Batch benchmark completed: "
                + allResults.size() + " total results.");
        return allResults;
    }

    private record Difficulty(String label, int size, double density) {}
}