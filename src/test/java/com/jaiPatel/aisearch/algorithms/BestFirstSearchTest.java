package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;
import com.jaiPatel.aisearch.heuristics.ManhattanHeuristic;
import com.jaiPatel.aisearch.heuristics.Heuristic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BestFirstSearchTest {

    private Graph graph;
    private Node a, b, c, d, e, goal;
    private Heuristic heuristic;

    @BeforeEach
    void setUp() {
        graph = new Graph();
        a = new Node("A", 0, 0);
        b = new Node("B", 1, 0);
        c = new Node("C", 2, 0);
        d = new Node("D", 1, 1);
        e = new Node("E", 2, 1);
        goal = new Node("Goal", 3, 1);

        // Graph connections
        graph.addEdge(a, b, 1);
        graph.addEdge(b, c, 1);
        graph.addEdge(c, goal, 1);
        graph.addEdge(b, d, 1);
        graph.addEdge(d, e, 1);
        graph.addEdge(e, goal, 1);

        heuristic = new ManhattanHeuristic();
    }

    @Test
    void testGbfsFindsAPath() {
        SearchAlgorithm gbfs = new BestFirstSearch(heuristic);
        SearchResult result = gbfs.solve(graph, a, goal);

        List<Node> path = result.getPath();

        assertFalse(path.isEmpty(), "Path should not be empty");
        assertEquals(a, path.get(0), "Path should start at A");
        assertEquals(goal, path.get(path.size() - 1), "Path should end at Goal");
    }

    @Test
    void testGbfsNotGuaranteedOptimal() {
        // Add a misleading edge: direct but longer path
        Node f = new Node("F", 0, 1);
        graph.addEdge(a, f, 5);
        graph.addEdge(f, goal, 5);

        SearchAlgorithm gbfs = new BestFirstSearch(heuristic);
        SearchResult result = gbfs.solve(graph, a, goal);

        // GBFS may take a suboptimal path due to heuristic greediness
        assertEquals(goal, result.getPath().get(result.getPath().size() - 1),
                "Path should still reach the goal");
        assertTrue(result.getCost() >= 2,
                "Cost may be suboptimal, should be >= 2");
    }

    @Test
    void testNoPathReturnsInfinity() {
        SearchAlgorithm gbfs = new BestFirstSearch(heuristic);
        SearchResult result = gbfs.solve(graph, goal, a); // no path backwards

        assertTrue(result.getPath().isEmpty(), "Path should be empty when no connection exists");
        assertEquals(Double.POSITIVE_INFINITY, result.getCost(),
                "Cost should be infinity when no path exists");
    }

    @Test
    void testNodesExpandedAndExploredCounted() {
        SearchAlgorithm gbfs = new BestFirstSearch(heuristic);
        SearchResult result = gbfs.solve(graph, a, goal);

        assertTrue(result.getNodesExpanded() > 0, "Nodes expanded should be positive");
        assertTrue(result.getExploredSize() > 0, "Explored set size should be positive");
    }
}
