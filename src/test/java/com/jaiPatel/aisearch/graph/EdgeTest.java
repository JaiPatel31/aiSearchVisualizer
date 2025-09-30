package com.jaiPatel.aisearch.graph;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class EdgeTest {

    @Test
    void testEdgeProperties() {
        Node a = new Node("A");
        Node b = new Node("B");
        Edge edge = new Edge(a, b, 3.5);

        assertEquals(a, edge.getFrom());
        assertEquals(b, edge.getTo());
        assertEquals(3.5, edge.getCost());
    }

    @Test
    void testToStringFormat() {
        Node a = new Node("A");
        Node b = new Node("B");
        Edge edge = new Edge(a, b, 2.0);

        String result = edge.toString();

        System.out.println(result);
        assertTrue(result.contains("A --(2.0)--> B"), "toString should describe edge direction and cost");
    }
}