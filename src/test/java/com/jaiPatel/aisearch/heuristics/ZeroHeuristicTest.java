package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ZeroHeuristicTest {

    @Test
    void testAlwaysZero() {
        Node a = new Node("A", 1, 1);
        Node b = new Node("B", 10, 10);

        Heuristic h = new ZeroHeuristic();
        assertEquals(0.0, h.estimate(a, b), "Zero heuristic should always return 0");
    }

    @Test
    void testZeroWithSameNode() {
        Node a = new Node("A", 3, 7);

        Heuristic h = new ZeroHeuristic();
        assertEquals(0.0, h.estimate(a, a), "Zero heuristic should return 0 even for same node");
    }
}
