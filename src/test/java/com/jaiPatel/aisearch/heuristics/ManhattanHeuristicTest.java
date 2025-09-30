package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManhattanHeuristicTest {

    @Test
    void testManhattanDistance() {
        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 3, 4);

        Heuristic h = new ManhattanHeuristic();
        double result = h.estimate(a, b);

        assertEquals(7.0, result, 0.0001, "Manhattan distance should be |3-0| + |4-0| = 7");
    }

    @Test
    void testZeroDistance() {
        Node a = new Node("A", 2, 2);
        Node b = new Node("B", 2, 2);

        Heuristic h = new ManhattanHeuristic();
        assertEquals(0.0, h.estimate(a, b), "Manhattan distance to self should be 0");
    }
}
