package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IDDFSTest {

    private Graph graph;
    private Node a, b, c, d, e;

    @BeforeEach
    void setUp() {
        graph = new Graph();
        a = new Node("A");
        b = new Node("B");
        c = new Node("C");
        d = new Node("D");
        e = new Node("E");

        // Build a simple graph:
        // A -> B -> D
        // A -> C -> E
        graph.addEdge(a, b, 1);
        graph.addEdge(a, c, 1);
        graph.addEdge(b, d, 1);
        graph.addEdge(c, e, 1);
    }

    @Test
    void testIddfsFindsPath() {
        SearchAlgorithm iddfs = new IDDFS();
        SearchResult result = iddfs.solve(graph, a, e);

        List<Node> path = result.getPath();

        assertFalse(path.isEmpty(), "Path should not be empty");
        assertEquals(a, path.get(0), "Path should start at A");
        assertEquals(e, path.get(path.size() - 1), "Path should end at E");
    }

    @Test
    void testIddfsGuaranteesShortestPath() {
        graph.addEdge(b, e, 1); // Shortcut A -> B -> E

        SearchAlgorithm iddfs = new IDDFS();
        SearchResult result = iddfs.solve(graph, a, e);

        assertEquals(2, result.getCost(), "IDDFS should return the shortest path length of 2");
        assertEquals(a, result.getPath().get(0), "Path should start at A");
        assertEquals(e, result.getPath().get(result.getPath().size() - 1), "Path should end at E");
        assertEquals(3, result.getPath().size(), "Path should have 3 nodes (2 steps)");
    }


    @Test
    void testNoPathReturnsInfinity() {
        SearchAlgorithm iddfs = new IDDFS();
        SearchResult result = iddfs.solve(graph, d, a); // no path backwards

        assertTrue(result.getPath().isEmpty(), "Path should be empty when no connection exists");
        assertEquals(Double.POSITIVE_INFINITY, result.getCost(), "Cost should be infinity when no path exists");
    }

    @Test
    void testNodesExpandedAndExploredCounted() {
        SearchAlgorithm iddfs = new IDDFS();
        SearchResult result = iddfs.solve(graph, a, e);

        assertTrue(result.getNodesExpanded() > 0, "Nodes expanded should be positive");
        assertTrue(result.getExploredSize() > 0, "Explored set size should be positive");
    }
}
