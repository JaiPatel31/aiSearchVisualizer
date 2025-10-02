package com.jaiPatel.aisearch.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {
    private Graph graph;
    private Node a, b, c;

    @BeforeEach
    void setUp() {
        graph = new Graph();
        a = new Node("A");
        b = new Node("B");
        c = new Node("C");

        graph.addEdge(a, b, 1.0);
        graph.addEdge(b, c, 2.0);
    }

    @Test
    void testAddAndGetNodes() {
        Collection<Node> nodes = graph.getNodes();
        assertTrue(nodes.contains(a));
        assertTrue(nodes.contains(b));
        assertTrue(nodes.contains(c));
    }

    @Test
    void testGetNeighbors() {
        List<Edge> neighbors = graph.getNeighbors(a);
        assertEquals(1, neighbors.size());
        assertEquals(b, neighbors.get(0).getTo());
    }

    @Test
    void testEmptyNeighborsForUnknownNode() {
        Node d = new Node("D");
        assertTrue(graph.getNeighbors(d).isEmpty());
    }

    @Test
    void testToStringContainsAllEdges() {
        String result = graph.toString();
        assertTrue(result.contains("A ->"));
        assertTrue(result.contains("B ->"));
        assertTrue(result.contains("C ->"));
    }
}