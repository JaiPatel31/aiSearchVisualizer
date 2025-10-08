package com.jaiPatel.aisearch.UI;

import com.jaiPatel.aisearch.graph.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Dialog for generating random graphs or grid worlds for visualization and search.
 * <p>
 * Provides a JavaFX dialog that allows the user to select graph type (general weighted or grid world),
 * configure parameters, and generate a new Graph instance using the selected options.
 * Supports configuration of node count, branching factor, edge weights, grid size, obstacle density,
 * connectivity, and random seed.
 */
public class GraphGeneratorDialog {

    /**
     * Shows the graph generator dialog and returns the generated Graph.
     * <p>
     * The dialog allows the user to select between a general weighted graph and a grid world,
     * configure relevant parameters, and generate the graph. Returns the generated Graph instance
     * or null if the dialog is cancelled.
     *
     * @return The generated Graph, or null if cancelled
     */
    public Graph showDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Generate Graph");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("General Weighted Graph", "Grid World");
        typeBox.setValue("General Weighted Graph");

        TextField seedField = new TextField(String.valueOf(System.currentTimeMillis()));

        // general weighted graph pane
        GridPane generalPane = new GridPane();
        generalPane.setHgap(10); generalPane.setVgap(10);
        TextField nodesField = new TextField("20");
        TextField branchingField = new TextField("3");
        TextField minW = new TextField("1");
        TextField maxW = new TextField("10");
        generalPane.addRow(0, new Label("Nodes:"), nodesField);
        generalPane.addRow(1, new Label("Branching Factor:"), branchingField);
        generalPane.addRow(2, new Label("Min Weight:"), minW);
        generalPane.addRow(3, new Label("Max Weight:"), maxW);

        // grid world pane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10); gridPane.setVgap(10);
        TextField gridSize = new TextField("12");
        TextField density = new TextField("0.2");
        ComboBox<String> connectivity = new ComboBox<>();
        connectivity.getItems().addAll("4-connected (Manhattan)", "8-connected (Diagonal)");
        connectivity.setValue("4-connected (Manhattan)");
        CheckBox weighted = new CheckBox("Weighted costs");
        gridPane.addRow(0, new Label("Grid Size (N x N):"), gridSize);
        gridPane.addRow(1, new Label("Obstacle Density (0–1):"), density);
        gridPane.addRow(2, new Label("Connectivity:"), connectivity);
        gridPane.add(weighted, 1, 3);

        VBox root = new VBox(12,
                new HBox(10, new Label("Type:"), typeBox),
                generalPane,
                gridPane,
                new HBox(10, new Label("Seed:"), seedField)
        );

        gridPane.setVisible(false);
        typeBox.setOnAction(e -> {
            boolean isGrid = typeBox.getValue().equals("Grid World");
            generalPane.setVisible(!isGrid);
            gridPane.setVisible(isGrid);
        });

        dialog.getDialogPane().setContent(root);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final Graph[] out = new Graph[1];

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                long seed = Long.parseLong(seedField.getText());
                if (typeBox.getValue().equals("General Weighted Graph")) {
                    int n  = Integer.parseInt(nodesField.getText());
                    int b  = Integer.parseInt(branchingField.getText());
                    int mn = Integer.parseInt(minW.getText());
                    int mx = Integer.parseInt(maxW.getText());
                    out[0] = RandomGraphGenerator.generate(n, b, mn, mx, seed);
                } else {
                    int n  = Integer.parseInt(gridSize.getText());
                    double p = Double.parseDouble(density.getText());
                    boolean diag = connectivity.getValue().contains("8");
                    boolean w = weighted.isSelected();
                    out[0] = GridGraphGenerator.generateGrid(n, p, diag, w, seed);
                }
            }
            return null;
        });

        dialog.showAndWait();
        return out[0];
    }
}
