package com.jaiPatel.aisearch.graph;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class GraphLoaderSet1 {

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
            int x = (int) Double.parseDouble(parts[1]);
            int y = (int) Double.parseDouble(parts[2]);

            Node node = new Node(name, x, y);
            graph.addNode(node);
            nodeMap.put(name, node);

            // 👇 Debug print
            System.out.println(" - Added city: " + name + " (x=" + x + ", y=" + y + ")");
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

                // 👇 Debug print
                System.out.println(" - Connected " + city1 + " <-> " + city2 + " (" + dist + ")");
            } else {
                System.err.println(" ⚠️ Road skipped: " + city1 + " <-> " + city2 +
                        " (one of them not found in coordinates.csv)");
            }
        }

        System.out.println("\n✅ Finished loading graph. Total cities: " + graph.getNodes().size());
        return graph;
    }



    // Replace Euclidean with Haversine
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

}



