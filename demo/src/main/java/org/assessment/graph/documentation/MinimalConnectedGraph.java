package org.assessment.graph.documentation;


import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import java.util.Random;

public class MinimalConnectedGraph {
    public static void main(String[] args) {
        // Create a new graph
        Graph graph = new SingleGraph("Connected Component");

        // Number of vertices
        int numVertices = 20;

        // Add vertices to the graph
        for (int i = 0; i < numVertices; i++) {
            graph.addNode(String.valueOf(i));
        }

        Random random = new Random();

        // Add minimal random edges to create a connected component (spanning tree)
        for (int i = 1; i < numVertices; i++) {
            int j = random.nextInt(i);
            graph.addEdge(i + "-" + j, String.valueOf(i), String.valueOf(j));
        }

        // Display the graph
        graph.display();
    }}