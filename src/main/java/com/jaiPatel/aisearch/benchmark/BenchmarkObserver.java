package com.jaiPatel.aisearch.benchmark;

import com.jaiPatel.aisearch.algorithms.SearchObserver;
import com.jaiPatel.aisearch.graph.Node;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Collection;
import java.util.List;

public class BenchmarkObserver implements SearchObserver {
    private long startTime, endTime;
    private long beforeUsed, afterUsed;
    private int nodesExpanded, nodesGenerated, maxFrontier;
    private double totalCost;
    private int depth;

    public void start() {
        System.gc();
        Runtime rt = Runtime.getRuntime();
        beforeUsed = rt.totalMemory() - rt.freeMemory();
        startTime = System.nanoTime();
    }

    @Override
    public void onStep(Node current, Collection<Node> frontier, Collection<Node> explored, int nodesExpanded, double pathCost, int solutionDepth, double g, double h, double f) {

    }

    @Override
    public void onFinish(List<Node> path, int nodesExpanded, int nodesGenerated,
                         int maxFrontierSize, double totalCost,
                         int solutionDepth, long runtimeMs, long memoryBytes) {
        Runtime rt = Runtime.getRuntime();
        afterUsed = rt.totalMemory() - rt.freeMemory();

        this.nodesExpanded = nodesExpanded;
        this.nodesGenerated = nodesGenerated;
        this.maxFrontier = maxFrontierSize;
        this.totalCost = totalCost;
        this.depth = solutionDepth;
        endTime = System.nanoTime();
    }

    public double getRuntimeMs() { return (endTime - startTime) / 1_000_000.0; }
    public double getMemoryKB() { return (afterUsed - beforeUsed) / 1024.0; }
    public int getNodesExpanded() { return nodesExpanded; }
    public int getNodesGenerated() { return nodesGenerated; }
    public int getMaxFrontier() { return maxFrontier; }
    public int getSolutionDepth() { return depth; }
    public double getTotalCost() { return totalCost; }
}
