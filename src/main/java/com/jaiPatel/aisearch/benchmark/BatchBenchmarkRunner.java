package com.jaiPatel.aisearch.benchmark;

import com.jaiPatel.aisearch.algorithms.*;
import com.jaiPatel.aisearch.graph.*;
import com.jaiPatel.aisearch.heuristics.*;

import java.util.*;

/**
 * Runs batch benchmarks over multiple difficulty levels (easy/medium/hard).
 * <p>
 * Each difficulty is defined by grid size and obstacle density.
 * Runs each algorithm multiple times with different seeds and averages results.
 */
public class BatchBenchmarkRunner {

    /**
     * Runs the full suite of benchmarks for all difficulty levels and algorithms.
     * <p>
     * For each difficulty (Easy, Medium, Hard), generates random grid graphs with obstacles,
     * ensures solvability, and runs all search algorithms multiple times with different seeds.
     * Results are labeled and aggregated for analysis.
     *
     * @return List of benchmark results for all runs and algorithms
     */
    public static List<BenchmarkHarness.Result> runDifficultySuite() {
        List<BenchmarkHarness.Result> allResults = new ArrayList<>();

        int repeats = 5; // Number of runs per difficulty
        Random rand = new Random(42); // fixed seed for reproducibility

        // --- Difficulty levels (grid size + obstacle density) ---
        var difficultyLevels = List.of(
                new Difficulty("Easy", 8, 0.1),   // Small grid, low density
                new Difficulty("Medium", 12, 0.25), // Medium grid, moderate density
                new Difficulty("Hard", 16, 0.4)    // Large grid, high density
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
                Graph g;
                Node start, goal;

                // Generate a solvable grid graph for this run
                while (true) {
                    g = GridGraphGenerator.generateGrid(d.size, d.density, true, true, seed);
                    List<Node> nodes = new ArrayList<>(g.getNodes());
                    if (nodes.size() < 2) {
                        seed = rand.nextLong();
                        continue;
                    }
                    start = nodes.get(0);
                    goal = nodes.get(nodes.size() - 1);

                    if (GridGraphGenerator.isSolvable(g, start, goal)) break;
                    System.out.println("❌ Unsolvable graph (seed=" + seed + "), regenerating...");
                    seed = rand.nextLong();
                }

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

    /**
     * Represents a difficulty level for benchmarking.
     * <p>
     * Each difficulty is defined by a label, grid size, and obstacle density.
     * Used to parameterize grid generation and benchmark runs.
     *
     * @param label   Name of the difficulty (e.g., "Easy")
     * @param size    Grid size (number of nodes per side)
     * @param density Obstacle density (fraction of blocked cells)
     */
    private record Difficulty(String label, int size, double density) {}
}