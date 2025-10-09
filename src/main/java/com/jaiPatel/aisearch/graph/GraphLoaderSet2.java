package com.jaiPatel.aisearch.graph;

import com.opencsv.CSVReader;
import com.fasterxml.jackson.databind.*;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Utility class for loading a graph from a CSV file with embedded JSON connections.
 * <p>
 * Loads nodes (cities) from a CSV file and edges (roads) from a JSON array in the CSV.
 * Calculates edge weights using the Haversine formula for geographic distance.
 */
public class GraphLoaderSet2 {

    /** Jackson ObjectMapper for parsing JSON connection arrays. */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Loads a graph from the given CSV file.
     * <p>
     * Reads city coordinates and connections from the CSV file. Each city is added as a node,
     * and each connection (in JSON format) is added as a bidirectional edge with distance as weight.
     *
     * @param filePath Path to the CSV file containing city data and connections
     * @return The loaded Graph object
     * @throws IOException If reading the file fails
     */
    public static Graph load(String filePath) throws IOException {
        System.out.println("📂 Starting to load graph from CSV: " + filePath);
        Graph graph = new Graph();
        Map<String, Node> nodeMap = new HashMap<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = reader.readAll();
            System.out.println("🧾 CSV read complete. Total rows (including header): " + rows.size());

            // Step 1: Create nodes from CSV rows
            System.out.println("🪄 Creating nodes...");
            for (String[] parts : rows.subList(1, rows.size())) { // skip header
                if (parts.length < 4) {
                    System.out.println("⚠️ Skipping malformed row (expected >=4 cols): " + Arrays.toString(parts));
                    continue;
                }

                String name = parts[0].replace("\"", "").trim();
                double lat = Double.parseDouble(parts[1]);
                double lon = Double.parseDouble(parts[2]);

                Node node = new Node(name, lat, lon);
                graph.addNode(node);
                nodeMap.put(name, node);

                System.out.println("✅ Added node: " + name + " (lat=" + lat + ", lon=" + lon + ")");
            }

            System.out.println("🌍 Total nodes created: " + nodeMap.size());

            // Step 2: Create edges from connections JSON
            System.out.println("🔗 Creating edges from JSON connections...");
            int edgeCount = 0;
            for (String[] parts : rows.subList(1, rows.size())) {
                if (parts.length < 4) continue;

                String fromName = parts[0].replace("\"", "").trim();
                Node from = nodeMap.get(fromName);
                if (from == null) {
                    System.out.println("⚠️ Node not found for 'from': " + fromName);
                    continue;
                }

                String connectionsJson = parts[3].trim();
                if (connectionsJson.isEmpty()) continue;

                try {
                    List<Map<String, String>> connections =
                            mapper.readValue(connectionsJson, List.class);

                    for (Map<String, String> conn : connections) {
                        String toName = conn.get("to");
                        if (toName == null) {
                            System.out.println("⚠️ Missing 'to' field in connection for " + fromName);
                            continue;
                        }
                        Node to = nodeMap.get(toName);
                        if (to == null) {
                            System.out.println("⚠️ Skipping edge to missing node: " + toName);
                            continue;
                        }

                        double dist = haversine(from.getX(), from.getY(), to.getX(), to.getY());
                        graph.addEdge(from, to, dist);
                        graph.addEdge(to, from, dist);
                        edgeCount += 2;

                        System.out.println("↔️ Connected " + fromName + " <-> " + toName + " (dist=" + String.format("%.2f", dist) + " km)");
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error parsing connections for " + fromName + ": " + e.getMessage());
                }
            }

            System.out.println("🔚 Edge creation complete. Total edges: " + edgeCount);
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }

        System.out.println("✅ Finished loading Set 2. Total cities: " + graph.getNodes().size());
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
        double R = 6371.0; // Earth radius km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
