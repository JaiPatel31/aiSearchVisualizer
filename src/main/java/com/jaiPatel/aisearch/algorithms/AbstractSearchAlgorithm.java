package com.jaiPatel.aisearch.algorithms;

import com.jaiPatel.aisearch.graph.*;

public abstract class AbstractSearchAlgorithm implements SearchAlgorithm {
    protected volatile boolean paused = false;
    protected volatile boolean stopped = false;

    public void pause() { paused = true; }
    public void resume() { paused = false; }
    public void stop() { stopped = true; }

    /**
     * Called in each loop iteration to honor pause/stop commands.
     */
    protected void checkControl() {
        while (paused) {
            try { Thread.sleep(100); }
            catch (InterruptedException ignored) {}
        }
        if (stopped) {
            throw new RuntimeException("Search stopped");
        }
    }

    @Override
    public abstract SearchResult solve(Graph graph, Node start, Node goal, SearchObserver observer);
}
