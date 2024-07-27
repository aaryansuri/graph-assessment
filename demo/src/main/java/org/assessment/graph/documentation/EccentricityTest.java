package org.assessment.graph.documentation;

import java.io.IOException;
import java.io.StringReader;
import org.graphstream.algorithm.APSP;
import org.graphstream.algorithm.Centroid;
import org.graphstream.algorithm.Eccentricity;
import org.graphstream.graph.Graph;
 import org.graphstream.graph.implementations.DefaultGraph;
 import org.graphstream.stream.file.FileSourceDGS;
 
 //                     +--- E
 // A --- B --- C -- D -|--- F
 //                     +--- G
 
 public class EccentricityTest {
 	static String my_graph =
                "DGS004\n" + 
                "my 0 0\n" + 
                "an A \n" +
                "an B \n" +
                "an C \n" +
                "an D \n" +
                "an E \n" +
                "an F \n" +
                "an G \n" +
                "ae AB A B \n" +
                "ae BC B C \n" +
                "ae CD C D \n" +
                "ae DE D E \n" +
                "ae DF D F \n" +
                "ae DG D G \n";
 
 	public static void main(String[] args) throws IOException {
 		Graph graph = new DefaultGraph("Eccentricity Test");
 		StringReader reader = new StringReader(my_graph);
 
 		FileSourceDGS source = new FileSourceDGS();
 		source.addSink(graph);
 		source.readAll(reader);
 
 		APSP apsp = new APSP();
 		apsp.init(graph);
 		apsp.compute();
 
 		Eccentricity eccentricity = new Eccentricity();
 		eccentricity.init(graph);
 		eccentricity.compute();

 
		graph.getNodeSet().forEach( n -> {
 		 	Boolean in = n.getAttribute("eccentricity");
 
 			System.out.printf("%s is%s in the eccentricity.\n", n.getId(), in ? ""
 					: " not");
 		});
 	}
 }