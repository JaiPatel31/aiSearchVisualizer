package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;

import java.util.*;

import java.util.*;

import java.util.*;

public class IDDFS extends AbstractSearchAlgorithm {

    @Override
    public SearchResult solve(Graph graph, Node start, Node goal, SearchObserver observer) {
        long startTime = System.nanoTime();
        long beforeMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        int depth = 0;
        int totalExpanded = 0;
        int totalGenerated = 0;
        int maxFrontierSize = 0;
        Set<Node> explored = new HashSet<>();

        while (true) {
            DepthLimitedResult dlr = depthLimitedSearch(graph, start, goal, depth, observer);

            totalExpanded += dlr.nodesExpanded;
            totalGenerated += dlr.nodesGenerated;
            explored.addAll(dlr.explored);
            maxFrontierSize = Math.max(maxFrontierSize, dlr.maxFrontierSize);

            if (dlr.found) {
                long endTime = System.nanoTime();
                long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                int solutionDepth = dlr.path.size() - 1;


                List<Node> path = dlr.path;

                // Calculate total cost along the path
                double totalCost = 0.0;
                for (int i = 0; i < path.size() - 1; i++) {
                    Node from = path.get(i);
                    Node to = path.get(i + 1);
                    totalCost += graph.getEdgeWeight(from, to);
                }

                return new SearchResult(
                        dlr.path,
                        totalCost,
                        totalExpanded,
                        totalGenerated,
                        explored.size(),
                        maxFrontierSize,
                        solutionDepth,
                        (endTime - startTime) / 1_000_000,
                        (afterMem - beforeMem)
                );
            }

            if (!dlr.cutoff) {
                // No solution
                long endTime = System.nanoTime();
                long afterMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                return new SearchResult(
                        Collections.emptyList(),
                        Double.POSITIVE_INFINITY,
                        totalExpanded,
                        totalGenerated,
                        explored.size(),
                        maxFrontierSize,
                        -1,
                        (endTime - startTime) / 1_000_000,
                        (afterMem - beforeMem)
                );
            }

            depth++; // increase depth and retry
        }
    }

    private DepthLimitedResult depthLimitedSearch(Graph graph, Node start, Node goal, int limit, SearchObserver observer) {
        Deque<NodeDepthPair> stack = new ArrayDeque<>();
        Map<Node, Node> parentMap = new HashMap<>();
        Set<Node> explored = new HashSet<>();

        stack.push(new NodeDepthPair(start, 0));
        parentMap.put(start, null);

        int nodesExpanded = 0;
        int nodesGenerated = 0;
        int maxFrontierSize = stack.size();
        boolean cutoff = false;

        while (!stack.isEmpty()) {
            checkControl(); // ✅ pause/resume/stop

            NodeDepthPair ndp = stack.pop();
            Node current = ndp.node;
            int depth = ndp.depth;
            nodesExpanded++;
            explored.add(current);

            if (observer != null) {
                // ✅ convert NodeDepthPair frontier → Node collection
                List<Node> frontierNodes = stack.stream().map(pair -> pair.node).toList();
                observer.onStep(current, frontierNodes, explored);
            }

            if (current.equals(goal)) {
                List<Node> path = reconstructPath(parentMap, goal);
                return new DepthLimitedResult(true, cutoff, path, nodesExpanded, nodesGenerated, explored, maxFrontierSize);
            }

            if (depth < limit) {
                for (var edge : graph.getNeighbors(current)) {
                    Node neighbor = edge.getTo();
                    if (!explored.contains(neighbor)) {
                        stack.push(new NodeDepthPair(neighbor, depth + 1));
                        parentMap.put(neighbor, current);
                        nodesGenerated++;
                    }
                }
            } else {
                cutoff = true; // depth limit reached
            }

            maxFrontierSize = Math.max(maxFrontierSize, stack.size());
        }

        return new DepthLimitedResult(false, cutoff, Collections.emptyList(),
                nodesExpanded, nodesGenerated, explored, maxFrontierSize);
    }

    private List<Node> reconstructPath(Map<Node, Node> parentMap, Node goal) {
        List<Node> path = new ArrayList<>();
        for (Node n = goal; n != null; n = parentMap.get(n)) {
            path.add(n);
        }
        Collections.reverse(path);
        return path;
    }

    // --- Helper classes ---
    private static class NodeDepthPair {
        Node node;
        int depth;
        NodeDepthPair(Node node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }

    private static class DepthLimitedResult {
        boolean found;
        boolean cutoff;
        List<Node> path;
        int nodesExpanded;
        int nodesGenerated;
        Set<Node> explored;
        int maxFrontierSize;

        DepthLimitedResult(boolean found, boolean cutoff, List<Node> path,
                           int nodesExpanded, int nodesGenerated,
                           Set<Node> explored, int maxFrontierSize) {
            this.found = found;
            this.cutoff = cutoff;
            this.path = path;
            this.nodesExpanded = nodesExpanded;
            this.nodesGenerated = nodesGenerated;
            this.explored = explored;
            this.maxFrontierSize = maxFrontierSize;
        }
    }
}

