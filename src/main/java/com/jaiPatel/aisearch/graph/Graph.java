package com.jaiPatel.aisearch.graph;

import java.util.*;

public class Graph {
    private final Map<Node, List<Edge>> adjacencyList = new HashMap<>();

    public void addNode(Node node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(Node from, Node to, double weight) {
        addNode(from);
        addNode(to);
        adjacencyList.get(from).add(new Edge(from, to, weight));
    }

    public void addUndirectedEdge(Node node1, Node node2, double weight) {
        addEdge(node1, node2, weight);
        addEdge(node2, node1, weight);
    }

    public List<Edge> getNeighbors(Node node) {
        return adjacencyList.getOrDefault(node, Collections.emptyList());
    }

    public Set<Node> getNodes() {
        return adjacencyList.keySet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node node : adjacencyList.keySet()) {
            sb.append(node).append(" -> ").append(adjacencyList.get(node)).append("\n");
        }
        return sb.toString();
    }
}