package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.Node;
import com.jaiPatel.aisearch.heuristics.ManhattanHeuristic;
import com.jaiPatel.aisearch.heuristics.Heuristic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AStarSearchTest {

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

        // Graph connections with weights
        graph.addEdge(a, b, 1);
        graph.addEdge(b, c, 1);
        graph.addEdge(c, goal, 1); // Path A-B-C-Goal (cost 3)
        graph.addEdge(b, d, 2);
        graph.addEdge(d, e, 2);
        graph.addEdge(e, goal, 2); // Path A-B-D-E-Goal (cost 5)

        heuristic = new ManhattanHeuristic();
    }

    @Test
    void testAStarFindsShortestPath() {
        SearchAlgorithm astar = new AStarSearch(heuristic);
        SearchResult result = astar.solve(graph, a, goal);

        List<Node> path = result.getPath();

        assertEquals(List.of(a, b, c, goal), path, "A* should find the optimal path A-B-C-Goal");
        assertEquals(3.0, result.getCost(), 0.0001, "Optimal path cost should be 3");
    }

    @Test
    void testAStarWithEqualCostPaths() {
        // Add alternative path with same cost
        Node f = new Node("F", 0, 1);
        graph.addEdge(a, f, 1);
        graph.addEdge(f, goal, 2); // Path A-F-Goal also cost 3

        SearchAlgorithm astar = new AStarSearch(heuristic);
        SearchResult result = astar.solve(graph, a, goal);

        assertEquals(3.0, result.getCost(), 0.0001, "A* should still return optimal cost of 3");
        assertEquals(goal, result.getPath().get(result.getPath().size() - 1), "Path should end at Goal");
    }

    @Test
    void testNoPathReturnsInfinity() {
        SearchAlgorithm astar = new AStarSearch(heuristic);
        SearchResult result = astar.solve(graph, goal, a); // no path backwards

        assertTrue(result.getPath().isEmpty(), "Path should be empty when no connection exists");
        assertEquals(Double.POSITIVE_INFINITY, result.getCost(), "Cost should be infinity when no path exists");
    }

    @Test
    void testNodesExpandedAndExploredCounted() {
        SearchAlgorithm astar = new AStarSearch(heuristic);
        SearchResult result = astar.solve(graph, a, goal);

        assertTrue(result.getNodesExpanded() > 0, "Nodes expanded should be positive");
        assertTrue(result.getExploredSize() > 0, "Explored set size should be positive");
    }
}
