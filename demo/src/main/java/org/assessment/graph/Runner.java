package org.assessment.graph;
import java.util.concurrent.ExecutionException;
import org.assessment.graph.pojos.Road;
import org.assessment.graph.service.GenerateGraph;
import org.assessment.graph.service.MapGenerator;
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
     * wait atleast 1 minute
     * if fails then re-run the program again
     * @param args
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        MapGenerator generator = new MapGenerator(100, 200, 3);
        generator.generateGraph();
        System.out.println(generator.getRoads().stream().map(Road::toString).filter(r -> r.contains("NATIONAL")).count());

        System.setProperty("org.graphstream.ui", "swing");


        Graph graph = new MultiGraph("Bazinga!");
        GenerateGraph generateGraph = new GenerateGraph();
        generateGraph.addCities(generator.getCityList(), graph);
        generateGraph.addRoads(generator.getRoads(), graph);
        generateGraph.addAttributes(graph);

        Viewer viewer = graph.display();
        viewer.enableAutoLayout();
    }
}
