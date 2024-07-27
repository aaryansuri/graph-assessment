package org.assessment.graph.documentation;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.algorithm.TarjanStronglyConnectedComponents;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;

public class CCTest {
	public static void main(String[] args) {

		Graph graph = new DefaultGraph("CC Test");

		graph.addNode("A");
		graph.addNode("B");
		graph.addNode("C");
		graph.addEdge("AB", "A", "B");
		graph.addEdge("AC", "A", "C");

		graph.display();

		ConnectedComponents cc = new ConnectedComponents();
		cc.init(graph);
		TarjanStronglyConnectedComponents tscc = new TarjanStronglyConnectedComponents();
		tscc.init(graph);
		tscc.compute();

		System.out.printf("%d connected component(s) in this graph, so far.%n",
				cc.getConnectedComponentsCount());

		graph.removeEdge("AC");

		System.out.printf("Eventually, there are %d.%n", 
				cc.getConnectedComponentsCount());

	}
}