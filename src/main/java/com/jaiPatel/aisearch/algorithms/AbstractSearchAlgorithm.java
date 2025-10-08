package com.jaiPatel.aisearch.algorithms;

        import com.jaiPatel.aisearch.graph.*;

        import java.util.*;

        /**
         * Base class for all search algorithms.
         * Provides common functionality such as pause/resume/stop control,
         * observer notifications, and step-based execution for visualization.
         */
        public abstract class AbstractSearchAlgorithm implements SearchAlgorithm {

            // Indicates whether the algorithm is paused
            protected volatile boolean paused = false;

            // Indicates whether the algorithm is stopped
            protected volatile boolean stopped = false;

            // Tracks the number of nodes expanded during the search
            protected int nodesExpanded = 0;

            /**
             * Pauses the execution of the algorithm.
             */
            public void pause() { paused = true; }

            /**
             * Resumes the execution of the algorithm.
             */
            public void resume() { paused = false; }

            /**
             * Stops the execution of the algorithm.
             */
            public void stop() { stopped = true; }

            // Tracks the number of nodes generated during the search
            protected int nodesGenerated = 0;

            // Tracks the maximum size of the frontier during the search
            protected int maxFrontierSize = 0;

            // Records the start time of the search
            protected long startTime = 0;

            // Maps to store g, h, and f scores for nodes
            protected Map<Node, Double> gScore = new HashMap<>();
            protected Map<Node, Double> hScore = new HashMap<>();
            protected Map<Node, Double> fScore = new HashMap<>();

            /**
             * Returns the number of nodes expanded during the search.
             *
             * @return Number of nodes expanded
             */
            public int getNodesExpanded() { return nodesExpanded; }

            /**
             * Returns the number of nodes generated during the search.
             *
             * @return Number of nodes generated
             */
            public int getNodesGenerated() { return nodesGenerated; }

            /**
             * Returns the maximum size of the frontier during the search.
             *
             * @return Maximum frontier size
             */
            public int getMaxFrontierSize() { return maxFrontierSize; }

            /**
             * Returns the start time of the search.
             *
             * @return Start time in milliseconds
             */
            public long getStartTime() { return startTime; }

            /**
             * Checks the control state (pause/stop) of the algorithm.
             * If paused, the thread sleeps until resumed.
             * If stopped, a RuntimeException is thrown to terminate the search.
             */
            protected void checkControl() {
                while (paused) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}
                }
                if (stopped) {
                    throw new RuntimeException("Search stopped");
                }
            }

            /**
             * Notifies the observer with the current state of the search.
             *
             * @param observer       The observer to notify
             * @param current        Current node being expanded
             * @param frontier       Nodes in the frontier/open list
             * @param explored       Nodes that have been explored/visited
             * @param pathCost       Cost from the start node to the current node
             * @param solutionDepth  Depth of the current node from the start
             * @param g              Cost from the start to the current node
             * @param h              Heuristic estimate to the goal
             * @param f              Total estimated cost (g + h)
             */
            protected void notifyObserver(SearchObserver observer,
                                          Node current,
                                          Collection<Node> frontier,
                                          Collection<Node> explored,
                                          double pathCost,
                                          int solutionDepth,
                                          double g,
                                          double h,
                                          double f) {
                nodesExpanded++;
                if (observer != null) {
                    observer.onStep(current, frontier, explored,
                            nodesExpanded, pathCost, solutionDepth, g, h, f);
                }
            }

            /**
             * Initializes the algorithm state before stepping begins.
             * Subclasses should override this method to set up specific state.
             *
             * @param graph    The graph to search
             * @param start    The start node
             * @param goal     The goal node
             * @param observer The observer to notify during the search
             */
            public void initialize(Graph graph, Node start, Node goal, SearchObserver observer) {
                // Default empty — each subclass overrides
            }

            /**
             * Performs a single step of the algorithm.
             *
             * @return True if there are more steps remaining, false if finished
             */
            public boolean step() {
                return false;
            }

            /**
             * Checks whether the search has finished.
             *
             * @return True if the search is finished, false otherwise
             */
            public boolean isFinished() {
                return stopped;
            }

            /**
             * Solves the search problem in batch mode.
             * This method is unsupported for incremental algorithms.
             *
             * @param graph    The graph to search
             * @param start    The start node
             * @param goal     The goal node
             * @param observer The observer to notify during the search
             * @return SearchResult containing the result of the search
             * @throws UnsupportedOperationException Always thrown for incremental algorithms
             */
            @Override
            public SearchResult solve(Graph graph, Node start, Node goal, SearchObserver observer) {
                throw new UnsupportedOperationException(
                        "Incremental algorithms should use initialize() and step() instead of solve()."
                );
            }

            /**
             * Calculates the total cost of a path in the graph.
             *
             * @param graph The graph containing the path
             * @param path  The list of nodes representing the path
             * @return The total cost of the path
             */
            protected double calculatePathCost(Graph graph, List<Node> path) {
                double cost = 0;
                for (int i = 0; i < path.size() - 1; i++) {
                    cost += graph.getEdgeWeight(path.get(i), path.get(i + 1));
                }
                return cost;
            }

            /**
             * Reconstructs the path from the start node to the goal node.
             *
             * @param parent A map of nodes to their parents
             * @param goal   The goal node
             * @return A list of nodes representing the reconstructed path
             */
            protected List<Node> reconstructPath(Map<Node, Node> parent, Node goal) {
                List<Node> path = new ArrayList<>();
                for (Node n = goal; n != null; n = parent.get(n)) {
                    path.add(n);
                }
                Collections.reverse(path);
                return path;
            }

        }