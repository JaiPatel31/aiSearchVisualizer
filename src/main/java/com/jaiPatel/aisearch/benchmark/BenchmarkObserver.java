package com.jaiPatel.aisearch.benchmark;

import com.jaiPatel.aisearch.algorithms.SearchObserver;
import com.jaiPatel.aisearch.graph.Node;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Collection;
import java.util.List;

public class BenchmarkObserver implements SearchObserver {
    private long startTimeNs, endTimeNs;
    private long peakMemBytes = 0;
    private int nodesExpanded, nodesGenerated, maxFrontier;
    private double totalCost;
    private int depth;

    private long getUsedMemory() {
        MemoryUsage heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        return heap.getUsed();
    }

    public void start() {
        System.gc();
        startTimeNs = System.nanoTime();
        peakMemBytes = getUsedMemory();
    }

    @Override
    public void onStep(Node current, Collection<Node> frontier, Collection<Node> explored,
                       int nodesExpanded, double pathCost, int solutionDepth,
                       double g, double h, double f) {
        this.nodesExpanded = nodesExpanded;
        this.maxFrontier = Math.max(maxFrontier, frontier.size() + explored.size());
        this.depth = Math.max(depth, solutionDepth);

        long memNow = getUsedMemory();
        if (memNow > peakMemBytes) peakMemBytes = memNow;
    }

    @Override
    public void onFinish(List<Node> path, int nodesExpanded, int nodesGenerated,
                         int maxFrontierSize, double totalCost,
                         int solutionDepth, long runtimeMs, long memoryBytes) {
        this.nodesExpanded = nodesExpanded;
        this.nodesGenerated = nodesGenerated;
        this.maxFrontier = maxFrontierSize;
        this.totalCost = totalCost;
        this.depth = solutionDepth;
        this.endTimeNs = System.nanoTime();
    }

    public double getRuntimeMs() { return (endTimeNs - startTimeNs) / 1_000_000.0; }
    public double getMemoryKB() { return peakMemBytes / 1024.0; }
    public int getNodesExpanded() { return nodesExpanded; }
    public int getNodesGenerated() { return nodesGenerated; }
    public int getMaxFrontier() { return maxFrontier; }
    public int getSolutionDepth() { return depth; }
    public double getTotalCost() { return totalCost; }
}
