package com.jaiPatel.aisearch.graph;

import java.util.*;

/**
 * Represents a directed, weighted graph for search and pathfinding algorithms.
 * <p>
 * Provides methods to add nodes and edges, retrieve neighbors, edge weights, and nodes by name.
 * Internally uses adjacency lists for efficient graph operations.
 */
public class Graph {

    /** Maps each node to its list of outgoing edges (adjacency list). */
    private final Map<Node, List<Edge>> adjacencyList = new HashMap<>();
    /** Maps node names to Node objects for fast lookup. */
    private final Map<String, Node> nameToNode = new HashMap<>();

    /**
     * Adds a node to the graph. If the node already exists, does nothing.
     * Also updates the name-to-node mapping.
     *
     * @param node The node to add
     */
    public void addNode(Node node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
        nameToNode.put(node.getName(), node);
    }

    /**
     * Adds a directed edge from one node to another with a given weight.
     * If either node does not exist, it is added to the graph.
     *
     * @param from   The source node
     * @param to     The destination node
     * @param weight The weight or cost of the edge
     */
    public void addEdge(Node from, Node to, double weight) {
        addNode(from);
        addNode(to);
        adjacencyList.get(from).add(new Edge(from, to, weight));
    }

    /**
     * Returns the list of outgoing edges from a given node.
     *
     * @param node The node whose edges to retrieve
     * @return List of outgoing edges from the node
     */
    public List<Edge> getEdgesFrom(Node node) {
        return adjacencyList.getOrDefault(node, Collections.emptyList());
    }

    /**
     * Returns the weight of the edge from one node to another.
     * If no such edge exists, returns 0.0.
     *
     * @param from The source node
     * @param to   The destination node
     * @return The weight of the edge, or 0.0 if no edge exists
     */
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

    /**
     * Returns the list of neighboring edges for a given node.
     *
     * @param node The node whose neighbors to retrieve
     * @return List of neighboring edges
     */
    public List<Edge> getNeighbors(Node node) {
        return adjacencyList.getOrDefault(node, Collections.emptyList());
    }

    /**
     * Returns the node object corresponding to the given name.
     *
     * @param name The name of the node
     * @return The Node object, or null if not found
     */
    public Node getNode(String name) {
        return nameToNode.get(name);
    }

    /**
     * Returns a collection of all nodes in the graph.
     *
     * @return Collection of all nodes
     */
    public Collection<Node> getNodes() {
        return adjacencyList.keySet();
    }
}