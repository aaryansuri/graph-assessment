package org.assessment.graph;
import java.util.concurrent.ExecutionException;
import org.assessment.graph.pojos.Road;
import org.assessment.graph.service.GenerateGraph;
import org.assessment.graph.service.MapGenerator;
import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.algorithm.TarjanStronglyConnectedComponents;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.view.Viewer;

public class Runner {


    /**
     * "With more lanes, more preference is given to generating in the order:
     *
     * 4 lanes: National Highways
     * 3 lanes: Inter-State Highways
     * 2 lanes: Highways
     * 1 lane: Main Roads"
     * MapGenerator uses backtrack internally and multiple tries are made till a graph is not generated
     * wait for few minutes
     * if fails then re-run the program again
     * @param args
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        MapGenerator generator = new MapGenerator(100, 200, 4);
        generator.generateGraph();
        System.out.println("hello");

        System.setProperty("org.graphstream.ui", "swing");


        Graph graph = new MultiGraph("Bazinga!");
        GenerateGraph generateGraph = new GenerateGraph();
        generateGraph.addCities(generator.getCityList(), graph);
        generateGraph.addRoads(generator.getRoads(), graph);
        generateGraph.addAttributes(graph);

        ConnectedComponents cc = new ConnectedComponents();
        cc.init(graph);
        TarjanStronglyConnectedComponents tscc = new TarjanStronglyConnectedComponents();
        tscc.init(graph);
        tscc.compute();

        System.out.printf("%d connected component(s) in this graph, so far.%n",
            cc.getConnectedComponentsCount());

        Viewer viewer = graph.display();
        viewer.enableAutoLayout();
    }
}
