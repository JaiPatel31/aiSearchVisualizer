package com.jaiPatel.aisearch.graph;

/**
 * Represents a directed, weighted edge between two nodes in a graph.
 * <p>
 * Each edge has a source node (from), a destination node (to), and an associated cost.
 * Used for graph search and pathfinding algorithms.
 */
public class Edge {
    /** The source node of the edge. */
    private final Node from;
    /** The destination node of the edge. */
    private final Node to;
    /** The cost or weight of traversing this edge. */
    private final double cost;

    /**
     * Constructs an edge between two nodes with a given cost.
     *
     * @param from The source node
     * @param to   The destination node
     * @param cost The cost or weight of the edge
     */
    public Edge(Node from, Node to, double cost) {
        this.from = from;
        this.to = to;
        this.cost = cost;
    }

    /**
     * Returns the source node of the edge.
     * @return The source node
     */
    public Node getFrom() {
        return from;
    }

    /**
     * Returns the destination node of the edge.
     * @return The destination node
     */
    public Node getTo() {
        return to;
    }

    /**
     * Returns the cost or weight of the edge.
     * @return The edge cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * Returns a string representation of the edge in the format:
     * from --(cost)--> to
     * @return String representation of the edge
     */
    @Override
    public String toString() {
        return from + " --(" + cost + ")--> " + to;
    }
}
