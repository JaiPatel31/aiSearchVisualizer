# AI Search Visualizer

AI Search Visualizer is a JavaFX application for visualizing and benchmarking various AI search algorithms on graphs and grid worlds. It supports BFS, DFS, IDDFS, Greedy Best-First Search, and A* algorithms, and allows users to generate random graphs, load preset datasets, and view search metrics and paths interactively.

## Features
- Visualize search algorithms step-by-step on graphs and grids
- Generate random weighted graphs or grid worlds with obstacles
- Load preset city datasets for real-world search scenarios
- Compare algorithms with batch benchmarking and runtime/memory charts
- Interactive UI for selecting start/goal nodes, algorithms, heuristics, and animation speed
- Command-line interface for quick testing and experimentation

## Requirements
- Java 17 or higher
- Maven

## How to Run
1. Clone or download this repository.
2. Open a terminal in the project root directory.
3. Run the following command:

```
mvn javafx:run
```

This will launch the JavaFX application. You can interact with the UI to select graphs, algorithms, and visualize searches.

## Project Structure
- `src/main/java/com/jaiPatel/aisearch/algorithms/` — Search algorithm implementations
- `src/main/java/com/jaiPatel/aisearch/graph/` — Graph, node, edge, and graph generators/loaders
- `src/main/java/com/jaiPatel/aisearch/heuristics/` — Heuristic functions for informed search
- `src/main/java/com/jaiPatel/aisearch/UI/` — JavaFX UI components and controllers
- `src/main/java/com/jaiPatel/aisearch/benchmark/` — Benchmarking utilities and batch runners
- `src/main/java/com/jaiPatel/aisearch/utils/` — Command-line interface
- `src/main/resources/` — Preset datasets (city coordinates, adjacencies)

## Usage Tips
- Use the UI to generate random graphs or load preset datasets.
- Select start and goal nodes, choose an algorithm, and run the search step-by-step or automatically.
- View search metrics, frontier, and path in real time.
- Run batch benchmarks to compare algorithm performance.

## License
This project is for educational and research purposes.

