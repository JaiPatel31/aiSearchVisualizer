package com.jaiPatel.aisearch.graph;

import java.util.Objects;

public class Node {
    private final String name;
    private final double x, y;

    /** Node with no coordinates */
    public Node(String name) {
        this.name = name;
        this.x = -1;
        this.y = -1;
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
