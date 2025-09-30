package com.jaiPatel.aisearch.heuristics;

import com.jaiPatel.aisearch.graph.Node;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EuclideanHeuristicTest {

    @Test
    void testEuclideanDistance() {
        Node a = new Node("A", 0, 0);
        Node b = new Node("B", 3, 4);

        Heuristic h = new EuclideanHeuristic();
        double result = h.estimate(a, b);

        assertEquals(5.0, result, 0.0001, "Euclidean distance should be sqrt(3^2 + 4^2) = 5");
    }

    @Test
    void testZeroDistance() {
        Node a = new Node("A", 1, 1);
        Node b = new Node("B", 1, 1);

        Heuristic h = new EuclideanHeuristic();
        assertEquals(0.0, h.estimate(a, b), "Euclidean distance to self should be 0");
    }
}
