package com.jaiPatel.aisearch.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a node in a graph, such as a city or grid cell.
 * <p>
 * Each node has a name and optional coordinates (x, y).
 * Additional attributes can be attached for use in search algorithms or metadata.
 */
public class Node {
    /** The name of the node (unique identifier). */
    private final String name;
    /** The x-coordinate of the node (optional, -1 if not set). */
    private final double x;
    /** The y-coordinate of the node (optional, -1 if not set). */
    private final double y;

    /** Optional attributes (for grid, heuristic, or random graph metadata). */
    private final Map<String, Object> attributes = new HashMap<>();

    /**
     * Constructs a node with no coordinates.
     *
     * @param name The name of the node
     */
    public Node(String name) {
        this(name, -1, -1);
    }

    /**
     * Constructs a node with coordinates.
     *
     * @param name The name of the node
     * @param x    The x-coordinate
     * @param y    The y-coordinate
     */
    public Node(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the name of the node.
     * @return The node name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the x-coordinate of the node.
     * @return The x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the node.
     * @return The y-coordinate
     */
    public double getY() {
        return y;
    }

    // === Optional attribute support ===

    /**
     * Sets an attribute for this node.
     *
     * @param key   The attribute key
     * @param value The attribute value
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * Gets the value of an attribute for this node.
     *
     * @param key The attribute key
     * @return The attribute value, or null if not set
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * Checks if this node has a given attribute.
     *
     * @param key The attribute key
     * @return True if the attribute exists, false otherwise
     */
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    /**
     * Checks equality based on node name.
     * @param o The object to compare
     * @return True if names are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return Objects.equals(name, node.name);
    }

    /**
     * Computes hash code based on the node name.
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Returns the string representation of the node (its name).
     * @return The node name
     */
    @Override
    public String toString() {
        return name;
    }
}
