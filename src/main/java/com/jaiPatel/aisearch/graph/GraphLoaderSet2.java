package com.jaiPatel.aisearch.graph;

import com.opencsv.CSVReader;
import com.fasterxml.jackson.databind.*;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GraphLoaderSet2 {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Graph load(String filePath) throws IOException {
        Graph graph = new Graph();
        Map<String, Node> nodeMap = new HashMap<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = reader.readAll();

            // Step 1: Create nodes
            for (String[] parts : rows.subList(1, rows.size())) { // skip header
                if (parts.length < 4) continue;

                String name = parts[0].replace("\"", "").trim(); // remove quotes
                double lat = Double.parseDouble(parts[1]);
                double lon = Double.parseDouble(parts[2]);

                Node node = new Node(name, lat, lon);
                graph.addNode(node);
                nodeMap.put(name, node);
            }

            // Step 2: Create edges from connections JSON
            for (String[] parts : rows.subList(1, rows.size())) {
                if (parts.length < 4) continue;

                String fromName = parts[0].replace("\"", "").trim();
                Node from = nodeMap.get(fromName);
                if (from == null) continue;

                String connectionsJson = parts[3].trim();
                if (connectionsJson.isEmpty()) continue;

                try {
                    List<Map<String, String>> connections =
                            mapper.readValue(connectionsJson, List.class);

                    for (Map<String, String> conn : connections) {
                        String toName = conn.get("to");
                        if (toName == null) continue;
                        Node to = nodeMap.get(toName);
                        if (to == null) continue;

                        double dist = haversine(from.getX(), from.getY(), to.getX(), to.getY());
                        graph.addEdge(from, to, dist);
                        graph.addEdge(to, from, dist);
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Error parsing connections for " + fromName);
                }
            }
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }

        System.out.println("✅ Finished loading Set 2. Total cities: " + graph.getNodes().size());
        return graph;
    }

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
