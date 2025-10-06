package com.jaiPatel.aisearch.UI;

import com.jaiPatel.aisearch.algorithms.BFS;
import com.jaiPatel.aisearch.algorithms.SearchAlgorithm;
import com.jaiPatel.aisearch.graph.Graph;
import com.jaiPatel.aisearch.graph.GraphLoaderSet1;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

public class GraphSearchUI extends Application {

    private Graph graph;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load graph
        graph = GraphLoaderSet1.load(
                "C:\\Users\\Jai Patel\\Desktop\\aiSearchVisualizer\\src\\main\\resources\\coordinates.csv",
                "C:\\Users\\Jai Patel\\Desktop\\aiSearchVisualizer\\src\\main\\resources\\Adjacencies.txt"
        );

        // Choose algorithm (BFS for now)
        SearchAlgorithm algorithm = new BFS();

        // Create controller
        GraphSearchController controller = new GraphSearchController();
        BorderPane root = controller.createUI(graph, algorithm);

        // Show scene
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("AI Search Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
