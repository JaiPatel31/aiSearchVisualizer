package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DFSTest {

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
    void testDfsFindsPath() {
        SearchAlgorithm dfs = new DFS();
        SearchResult result = dfs.solve(graph, a, e);

        List<Node> path = result.getPath();

        assertFalse(path.isEmpty(), "Path should not be empty");
        assertEquals(a, path.get(0), "Path should start at A");
        assertEquals(e, path.get(path.size() - 1), "Path should end at E");
    }

    @Test
    void testDfsDoesNotGuaranteeShortestPath() {
        graph.addEdge(b, e, 1); // Shortcut from B -> E
        SearchAlgorithm dfs = new DFS();
        SearchResult result = dfs.solve(graph, a, e);

        // DFS may return A->B->E or A->C->E depending on traversal order
        assertEquals(a, result.getPath().get(0));
        assertEquals(e, result.getPath().get(result.getPath().size() - 1));
        assertTrue(result.getCost() >= 2, "DFS should find a path, not necessarily shortest");
    }

    @Test
    void testNoPathReturnsInfinity() {
        SearchAlgorithm dfs = new DFS();
        SearchResult result = dfs.solve(graph, d, a); // no path backwards

        assertTrue(result.getPath().isEmpty(), "Path should be empty when no connection exists");
        assertEquals(Double.POSITIVE_INFINITY, result.getCost(), "Cost should be infinity when no path exists");
    }

    @Test
    void testNodesExpandedAndExploredCounted() {
        SearchAlgorithm dfs = new DFS();
        SearchResult result = dfs.solve(graph, a, e);

        assertTrue(result.getNodesExpanded() > 0, "Nodes expanded should be positive");
        assertTrue(result.getExploredSize() > 0, "Explored set size should be positive");
    }
}
