package com.jaiPatel.aisearch.graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for loading a graph from city coordinates and adjacency files.
 * <p>
 * Loads nodes (cities) from a CSV file and edges (roads) from a text file.
 * Calculates edge weights using the Haversine formula for geographic distance.
 */
public class GraphLoaderSet1 {

    /**
     * Loads a graph from the given coordinates and adjacencies files.
     * <p>
     * Reads city coordinates from a CSV file and roads from a text file.
     * Each city is added as a node, and each road as a bidirectional edge with distance as weight.
     *
     * @param coordinatesFile Path to the CSV file containing city coordinates (name, lat, lon)
     * @param adjacenciesFile Path to the text file containing city adjacencies (roads)
     * @return The loaded Graph object
     * @throws IOException If reading files fails
     */
    public static Graph load(String coordinatesFile, String adjacenciesFile) throws IOException {
        Graph graph = new Graph();
        Map<String, Node> nodeMap = new HashMap<>();

        // Step 1: Read coordinates.csv
        List<String> lines = Files.readAllLines(Paths.get(coordinatesFile));
        System.out.println("Loading cities from " + coordinatesFile);

        for (String line : lines.subList(0, lines.size())) { // skip header
            String[] parts = line.split(",");
            if (parts.length < 3) continue;

            String name = parts[0].trim();
            double lat = Double.parseDouble(parts[1].trim());
            double lon = Double.parseDouble(parts[2].trim());

            Node node = new Node(name, lat, lon);
            graph.addNode(node);
            nodeMap.put(name, node);

            System.out.println(" - Added city: " + name + " (lat=" + lat + ", lon=" + lon + ")");
        }

        // Step 2: Read adjacencies.txt
        List<String> edges = Files.readAllLines(Paths.get(adjacenciesFile));
        System.out.println("\nLoading roads from " + adjacenciesFile);

        for (String line : edges) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length < 2) continue;

            String city1 = parts[0].trim();
            String city2 = parts[1].trim();

            Node n1 = nodeMap.get(city1);
            Node n2 = nodeMap.get(city2);

            if (n1 != null && n2 != null) {
                double dist = haversine(n1.getX(), n1.getY(), n2.getX(), n2.getY());
                graph.addEdge(n1, n2, dist);
                graph.addEdge(n2, n1, dist);

                System.out.println(" - Connected " + city1 + " <-> " + city2 + " (" + dist + ")");
            } else {
                System.err.println(" ⚠️ Road skipped: " + city1 + " <-> " + city2 +
                        " (one of them not found in coordinates.csv)");
            }
        }

        System.out.println("\n✅ Finished loading graph. Total cities: " + graph.getNodes().size());
        return graph;
    }

    /**
     * Calculates the great-circle distance between two coordinates using the Haversine formula.
     *
     * @param lat1 Latitude of the first point
     * @param lon1 Longitude of the first point
     * @param lat2 Latitude of the second point
     * @param lon2 Longitude of the second point
     * @return Distance in kilometers
     */
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
