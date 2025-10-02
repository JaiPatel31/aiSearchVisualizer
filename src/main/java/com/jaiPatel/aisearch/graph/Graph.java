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