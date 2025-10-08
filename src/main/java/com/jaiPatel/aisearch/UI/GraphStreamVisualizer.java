package com.jaiPatel.aisearch.UI;

import com.jaiPatel.aisearch.graph.*;
import javafx.application.Platform;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.layout.springbox.implementations.LinLog;

import java.util.List;

/**
 * Visualizes a graph using GraphStream and JavaFX for AI search algorithms.
 * <p>
 * Handles node and edge setup, style configuration, and dynamic updates for search visualization.
 * Supports both grid and general graphs, and provides methods for step-by-step updates and path highlighting.
 */
public class GraphStreamVisualizer {
    /** The AI search graph to visualize. */
    private final Graph aiGraph;
    /** The underlying GraphStream graph object. */
    private final SingleGraph gsGraph;
    /** The GraphStream viewer for JavaFX integration. */
    private FxViewer viewer;
    /** The JavaFX view panel displaying the graph. */
    private FxViewPanel viewPanel;

    /**
     * Constructs a GraphStreamVisualizer for the given AI search graph.
     * Sets up nodes, edges, and styles for visualization.
     *
     * @param aiGraph The graph to visualize
     */
    public GraphStreamVisualizer(Graph aiGraph) {
        this.aiGraph = aiGraph;
        this.gsGraph = new SingleGraph("AI Search Graph");
        setupNodesAndEdges();
        setupStyles();
    }

    /**
     * Adds nodes and edges from the AI graph to the GraphStream graph.
     * Sets node labels and edge labels for visualization.
     */
    private void setupNodesAndEdges() {
        for (Node n : aiGraph.getNodes()) {
            org.graphstream.graph.Node gsNode = gsGraph.addNode(n.getName());
            gsNode.setAttribute("ui.label", n.getName());
        }

        for (Node from : aiGraph.getNodes()) {
            for (Edge e : aiGraph.getEdgesFrom(from)) {
                String n1 = from.getName();
                String n2 = e.getTo().getName();
                String id = (n1.compareTo(n2) < 0) ? n1 + "-" + n2 : n2 + "-" + n1;
                if (gsGraph.getEdge(id) == null) {
                    org.graphstream.graph.Edge gsEdge = gsGraph.addEdge(id, n1, n2, false);
                    gsEdge.setAttribute("ui.label", String.format("%.1f", e.getCost()));
                }
            }
        }
    }

    /**
     * Resets all node and edge styles before a new search run.
     * Sets default colors and removes any previous class attributes.
     */
    public void resetGraph() {
        Platform.runLater(() -> {
            for (org.graphstream.graph.Node node : gsGraph) {
                node.setAttribute("ui.style", "fill-color: cornflowerblue;");
                node.removeAttribute("ui.class");
            }
            gsGraph.edges().forEach(edge -> {
                edge.setAttribute("ui.style", "fill-color: lightgray;");
                edge.removeAttribute("ui.class");
            });
        });
    }

    private void setupStyles() {
        gsGraph.setAttribute("ui.stylesheet",
                "node { size: 18px; fill-color: cornflowerblue; text-alignment: above; text-size: 14px; }" +
                        "node.current { fill-color: orange; }" +
                        "node.frontier { fill-color: yellow; }" +
                        "node.visited { fill-color: lightgreen; }" +
                        "node.blocked { fill-color: grey; }" +
                        "node.goal { fill-color: red; }" +
                        "edge.path { fill-color: purple; size: 3px; }" +
                        "edge { fill-color: grey; text-size: 12px; }"
        );
    }

    /** Create a stable view panel */
    public FxViewPanel getView() {
        viewer = new FxViewer(gsGraph, FxViewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewPanel = (FxViewPanel) viewer.addDefaultView(false);
        viewPanel.setMinSize(600, 600);

        boolean isGridGraph = aiGraph.getNodes().stream().anyMatch(n -> n.getX() >= 0 && n.getY() >= 0);

        if (isGridGraph) {
            viewer.disableAutoLayout();

            double spacing = 60;  // pixels between nodes
            double margin = 50;   // margin from window edges

            for (Node n : aiGraph.getNodes()) {
                org.graphstream.graph.Node gsNode = gsGraph.getNode(n.getName());
                if (gsNode != null) {
                    double x = margin + n.getX() * spacing;
                    double y = margin + n.getY() * spacing;
                    gsNode.setAttribute("xyz", x, -y, 0);
                }
            }
        } else {
            var layout = new LinLog();
            layout.setQuality(0.7);
            layout.setGravityFactor(0.9);
            viewer.enableAutoLayout(layout);
        }

        return viewPanel;
    }



    /** Step-by-step visualization updates */
    public void updateNodeStates(List<Node> frontier, List<Node> visited, Node current, Node goal, List<Node> blocked) {
        Platform.runLater(() -> {
            // Reset node styles
            for (org.graphstream.graph.Node gsNode : gsGraph)
                gsNode.removeAttribute("ui.class");

            // Reset edge styles (don't color them during step updates)
            for (org.graphstream.graph.Edge gsEdge : gsGraph.edges().toArray(org.graphstream.graph.Edge[]::new))
                gsEdge.removeAttribute("ui.class");

            // Color node groups
            if (visited != null) {
                visited.forEach(n -> {
                    org.graphstream.graph.Node node = gsGraph.getNode(n.getName());
                    if (node != null) node.setAttribute("ui.class", "visited");
                });
            }

            if (frontier != null) {
                frontier.forEach(n -> {
                    org.graphstream.graph.Node node = gsGraph.getNode(n.getName());
                    if (node != null) node.setAttribute("ui.class", "frontier");
                });
            }

            if (current != null) {
                org.graphstream.graph.Node node = gsGraph.getNode(current.getName());
                if (node != null) node.setAttribute("ui.class", "current");
            }

            if (goal != null) {
                org.graphstream.graph.Node node = gsGraph.getNode(goal.getName());
                if (node != null) node.setAttribute("ui.class", "goal");
            }

            if (blocked != null) {
                blocked.forEach(n -> {
                    org.graphstream.graph.Node node = gsGraph.getNode(n.getName());
                    if (node != null) node.setAttribute("ui.class", "blocked");
                });
            }
        });
    }

    /** Highlight final optimal path after search finishes */
    public void highlightPath(List<Node> path) {
        System.out.println("Highlighting path: " + path);
        if (path == null || path.size() < 2) return;

        Platform.runLater(() -> {
            // Keep start/goal nodes distinctly colored
            org.graphstream.graph.Node start = gsGraph.getNode(path.getFirst().getName());
            org.graphstream.graph.Node goal = gsGraph.getNode(path.getLast().getName());
            if (start != null) start.setAttribute("ui.class", "visited");
            if (goal != null) goal.setAttribute("ui.class", "goal");

            // Highlight only the final path edges
            for (int i = 0; i < path.size() - 1; i++) {
                String id1 = path.get(i).getName() + "-" + path.get(i + 1).getName();
                String id2 = path.get(i + 1).getName() + "-" + path.get(i).getName();
                org.graphstream.graph.Edge e = gsGraph.getEdge(id1);
                if (e == null) e = gsGraph.getEdge(id2);
                if (e != null) e.setAttribute("ui.class", "path");
            }
        });
    }
    private void centerGridView() {
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;

        for (org.graphstream.graph.Node node : gsGraph) {
            Number xAttr = node.getNumber("x");
            Number yAttr = node.getNumber("y");
            if (xAttr == null || yAttr == null) continue;
            double x = xAttr.doubleValue();
            double y = yAttr.doubleValue();
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        double offsetX = -(minX + maxX) / 2;
        double offsetY = -(minY + maxY) / 2;

        for (org.graphstream.graph.Node node : gsGraph) {
            Number xAttr = node.getNumber("x");
            Number yAttr = node.getNumber("y");
            if (xAttr == null || yAttr == null) continue;
            node.setAttribute("xyz",
                    xAttr.doubleValue() + offsetX,
                    yAttr.doubleValue() + offsetY,
                    0);
        }
    }

}
