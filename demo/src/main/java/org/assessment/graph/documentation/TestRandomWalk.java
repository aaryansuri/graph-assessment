package org.assessment.graph.documentation;
import java.util.concurrent.atomic.AtomicReference;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.randomWalk.RandomWalk;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

public class TestRandomWalk {

	public static void main(String[] args) {
		TestRandomWalk();
	}
        public static void TestRandomWalk() {
    	    Graph graph = new MultiGraph("random walk");
    	    Generator gen   = new DorogovtsevMendesGenerator();
    	    RandomWalk rwalk = new RandomWalk();
    	
            // We generate a 400 nodes Dorogovstev-Mendes graph.
    	    gen.addSink(graph);
    	    gen.begin();
    	    for(int i=0; i<100; i++) {
    		gen.nextEvents();
    	    }
    	    gen.end();
    	
            // We display the graph.
    	    graph.setAttribute("ui.stylesheet", styleSheet);
    	    graph.setAttribute("ui.quality");
    	    graph.setAttribute("ui.antialias");
    	    graph.display();
    	
            // We configure the random walk to use twice as
            // much entities as nodes in the graph. To use
            // a small evaporation on the number of passes
            // per element and a last visited edge list of 
            // 40 elements.
    	    rwalk.setEntityCount(graph.getNodeCount()*2);
    	    rwalk.setEvaporation(0.97);
    	    rwalk.setEntityMemory(40);
    	    rwalk.init(graph);

            // Compute the walks for 3000 steps only as an
            // example, but the test could run forever with
            // a dynamic graph if needed.
    	    for(int i=0; i<3000; i++) {
    	        rwalk.compute();
    	    }
    	    rwalk.terminate();

            // Only when finished we change the edges colors
            // according to the number of passes. This call could
            // be made inside the loop above to show the evolution
            // of the entities passes.
       	    updateGraph(graph, rwalk);

            // We take a small screen-shot of the result.
    	    graph.setAttribute("ui.screenshot", "randomWalk.png");
        }
    
        /**
         * Update the edges with colors corresponding to entities passes.
         */
        public static void updateGraph(Graph graph, RandomWalk rwalk) {
            AtomicReference<Double> mine = new AtomicReference<>(Double.MAX_VALUE);
    	    AtomicReference<Double> maxe = new AtomicReference<>(Double.MIN_VALUE);
    	
            // Obtain the maximum and minimum passes values.
            graph.getEdgeSet().forEach(edge -> {
			    double passes = rwalk.getPasses(edge);
                    if (passes > maxe.get()) {
                        maxe.set(passes);
                    }
                    if (passes < mine.get()) {
                        mine.set(passes);
                    }
    	    });
    	
            // Set the colors.
			graph.getEdgeSet().forEach(edge -> {
                double passes = rwalk.getPasses(edge);
    	        double color  = ((passes- mine.get())/(maxe.get() - mine.get()));
    			edge.setAttribute("ui.color", color);
    	    });
        }
    
        protected static String styleSheet =
    		"edge {"+
    		"	size: 2px;"+
    		"	fill-color: red, yellow, green, #444;"+
    		"	fill-mode: dyn-plain;"+
    		"}"+
    		"node {"+
    		"	size: 6px;"+
    		"	fill-color: #444;"+
    		"}";
    }