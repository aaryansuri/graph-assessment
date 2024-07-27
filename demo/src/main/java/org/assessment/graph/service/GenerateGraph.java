package org.assessment.graph.service;

import java.util.List;

import org.assessment.graph.pojos.City;
import org.assessment.graph.pojos.Road;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

public class GenerateGraph {

	public void addCities(List<City> cities, Graph graph) {
		for (City city : cities) {
			Node node = graph.addNode(city.getName());
			node.setAttribute("ui.label", city.getName());
		}
	}

	public void addRoads(List<Road> roads, Graph graph) {
		for (Road road : roads) {
			String edgeId = road.getCity1().getName() + "-" + road.getCity2().getName();
			String reverseEdgeId = road.getCity2().getName() + "-" + road.getCity1().getName();

			if (graph.getEdge(edgeId) == null && graph.getEdge(reverseEdgeId) == null) {
				Edge edge = graph.addEdge(edgeId, road.getCity1().getName(), road.getCity2().getName());
				edge.setAttribute("ui.label", road.getLaneType().toString());
				edge.setAttribute("ui.class", road.getLaneType().getStyle());
				edge.setAttribute("layout.weight", 20.0);
			}
		}
	}

	public void addAttributes(Graph graph) {
		String styleSheet =
			"node { fill-color: red; size: 10px; text-size: 8px; z-index: 0; }"
				+ "edge.nh { size: 3px; fill-color: navy; }"
				+ "edge.ish { size: 1.2px; fill-color: purple; }"
				+ "edge.h { size: 0.8px; fill-color: coral; }"
				+ "edge.mr { size: 0.2px; fill-color:black; }";

		graph.setAttribute("ui.stylesheet", styleSheet);
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
	}
}
