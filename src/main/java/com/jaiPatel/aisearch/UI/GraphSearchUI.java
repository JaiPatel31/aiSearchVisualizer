package com.jaiPatel.aisearch.UI;

import com.jaiPatel.aisearch.algorithms.BFS;
import com.jaiPatel.aisearch.algorithms.SearchAlgorithm;
import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.GraphLoaderSet1;
import com.jaiPatel.aisearch.graph.GraphLoaderSet2;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

import java.nio.file.Paths;
import java.util.Objects;

/**
 * Entry point for the AI Search Visualizer application.
 * <p>
 * Loads a default static graph from resource files and initializes the UI with a default search algorithm (BFS).
 * Users can later switch to random graph generation or other algorithms via the UI.
 */
public class GraphSearchUI extends Application {

    /** The graph currently loaded in the visualizer. */
    private Graph graph;

    /**
     * JavaFX application entry point. Sets up the main window and UI.
     * <p>
     * Loads the default graph from CSV and adjacency files, initializes the default search algorithm,
     * and attaches the main controller to the UI. Configures the scene and displays the window.
     *
     * @param primaryStage The primary stage for this application
     * @throws Exception If loading resources or initializing the UI fails
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load default graph (user can later switch to random mode in UI)
        graph = GraphLoaderSet1.load(
                Paths.get(Objects.requireNonNull(getClass().getResource("/coordinates.csv")).toURI()).toString(),
                Paths.get(Objects.requireNonNull(getClass().getResource("/Adjacencies.txt")).toURI()).toString()
        );

        // Default algorithm: BFS
        SearchAlgorithm algorithm = new BFS();

        // Create and attach controller
        GraphSearchController controller = new GraphSearchController();
        BorderPane root = controller.createUI(graph, algorithm);

        // Configure scene
        Scene scene = new Scene(root, 1200, 700);


        primaryStage.setTitle("AI Search Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main method for launching the JavaFX application.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
