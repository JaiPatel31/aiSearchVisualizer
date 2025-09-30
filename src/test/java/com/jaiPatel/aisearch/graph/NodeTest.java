package com.jaiPatel.aisearch.graph;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NodeTest {

    @Test
    void testEqualityById() {
        Node n1 = new Node("A");
        Node n2 = new Node("A");
        Node n3 = new Node("B");

        assertEquals(n1, n2, "Nodes with same ID should be equal");
        assertNotEquals(n1, n3, "Nodes with different IDs should not be equal");
    }

    @Test
    void testHashCodeConsistency() {
        Node n1 = new Node("A");
        Node n2 = new Node("A");

        assertEquals(n1.hashCode(), n2.hashCode(),
                "Equal nodes must have same hash code");
    }

    @Test
    void testToStringReturnsId() {
        Node n = new Node("X");
        assertEquals("X", n.toString());
    }
}