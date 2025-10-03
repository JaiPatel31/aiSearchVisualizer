package com.jaiPatel.aisearch.graph;

import java.util.*;

public class Graph {

    private final Map<Node, List<Edge>> adjacencyList = new HashMap<>();
    private final Map<String, Node> nameToNode = new HashMap<>();

    public void addNode(Node node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
        nameToNode.put(node.getName(), node);
    }

    public void addEdge(Node from, Node to, double weight) {
        addNode(from);
        addNode(to);
        adjacencyList.get(from).add(new Edge(from, to, weight));
    }
    public double getEdgeWeight(Node from, Node to) {
        List<Edge> edges = adjacencyList.get(from);
        if (edges != null) {
            for (Edge edge : edges) {
                if (edge.getTo().equals(to)) {
                    return edge.getCost();
                }
            }
        }
        return 0.0; // or throw an exception if no edge exists
    }
    public List<Edge> getNeighbors(Node node) {
        return adjacencyList.getOrDefault(node, Collections.emptyList());
    }

    // ✅ Fix: getNode by name
    public Node getNode(String name) {
        return nameToNode.get(name);
    }

    // Optionally: return all nodes
    public Collection<Node> getNodes() {
        return adjacencyList.keySet();
    }
}