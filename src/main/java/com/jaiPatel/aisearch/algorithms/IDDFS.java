package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;

import java.util.*;

public class IDDFS implements SearchAlgorithm {

    @Override
    public SearchResult solve(Graph graph, Node start, Node goal) {
        int depth = 0;
        int totalExpanded = 0;
        Set<Node> explored = new HashSet<>();

        while (true) {
            // Run depth-limited search with the current depth
            DepthLimitedResult dlr = depthLimitedSearch(graph, start, goal, depth);

            totalExpanded += dlr.nodesExpanded;
            explored.addAll(dlr.explored);

            if (dlr.found) {
                return new SearchResult(dlr.path, dlr.path.size() - 1, totalExpanded, explored.size());
            }

            if (!dlr.cutoff) {
                // No more nodes to expand, goal not found
                return new SearchResult(Collections.emptyList(), Double.POSITIVE_INFINITY, totalExpanded, explored.size());
            }

            depth++; // Increase depth and try again
        }
    }

    private DepthLimitedResult depthLimitedSearch(Graph graph, Node start, Node goal, int limit) {
        Deque<NodeDepthPair> stack = new ArrayDeque<>();
        Map<Node, Node> parentMap = new HashMap<>();
        Set<Node> explored = new HashSet<>();

        stack.push(new NodeDepthPair(start, 0));
        explored.add(start);

        int nodesExpanded = 0;
        boolean cutoff = false;

        while (!stack.isEmpty()) {
            NodeDepthPair ndp = stack.pop();
            Node current = ndp.node;
            int depth = ndp.depth;
            nodesExpanded++;

            if (current.equals(goal)) {
                // reconstruct path
                List<Node> path = new ArrayList<>();
                for (Node n = goal; n != null; n = parentMap.get(n)) {
                    path.add(n);
                }
                Collections.reverse(path);
                return new DepthLimitedResult(true, cutoff, path, nodesExpanded, explored);
            }

            if (depth < limit) {
                // Push neighbors
                for (var edge : graph.getNeighbors(current)) {
                    Node neighbor = edge.getTo();
                    if (!explored.contains(neighbor)) {
                        stack.push(new NodeDepthPair(neighbor, depth + 1));
                        explored.add(neighbor);
                        parentMap.put(neighbor, current);
                    }
                }
            } else {
                cutoff = true; // We hit the depth limit
            }
        }

        return new DepthLimitedResult(false, cutoff, Collections.emptyList(), nodesExpanded, explored);
    }

    // Helper classes
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
        Set<Node> explored;

        DepthLimitedResult(boolean found, boolean cutoff, List<Node> path, int nodesExpanded, Set<Node> explored) {
            this.found = found;
            this.cutoff = cutoff;
            this.path = path;
            this.nodesExpanded = nodesExpanded;
            this.explored = explored;
        }
    }
}

