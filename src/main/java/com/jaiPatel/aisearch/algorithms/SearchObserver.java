package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;
import java.util.Collection;

public interface SearchObserver {
    void onStep(Node current, Collection<Node> frontier, Collection<Node> explored);
}
