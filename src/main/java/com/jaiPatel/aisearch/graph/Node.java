package com.jaiPatel.aisearch.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Node {
    private final String name;
    private final double x, y;

    // Optional attributes (for grid, heuristic, or random graph metadata)
    private final Map<String, Object> attributes = new HashMap<>();

    /** Node with no coordinates */
    public Node(String name) {
        this(name, -1, -1);
    }

    /** Node with coordinates */
    public Node(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // === Optional attribute support ===
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return Objects.equals(name, node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
