package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChebyshevHeuristicTest {

    @Test
    void testChebyshevDistance() {
        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 3, 4);

        Heuristic h = new ChebyshevHeuristic();
        double result = h.estimate(a, b);

        assertEquals(4.0, result, 0.0001, "Chebyshev distance should be max(|3|, |4|) = 4");
    }

    @Test
    void testZeroDistance() {
        Node a = new Node("A", 5, 5);
        Node b = new Node("B", 5, 5);

        Heuristic h = new ChebyshevHeuristic();
        assertEquals(0.0, h.estimate(a, b), "Chebyshev distance to self should be 0");
    }
}
