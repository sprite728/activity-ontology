package io.mem0r1es.activitysubsumer.io;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.imp.CSVImporter;
import org.jgrapht.imp.StringVertexParser;
import org.jgrapht.util.VertexPair;

import java.net.URLDecoder;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public class FoursquareCategoriesCSV implements FoursquareCategoriesParser {

    private String path;
    public FoursquareCategoriesCSV(String path){
        this.path = path;
    }
    @Override
    public DirectedAcyclicGraph<String, DefaultEdge> readGraph() {
        CSVImporter<String> importer = new CSVImporter<String>(" ", new StringVertexParser(), path);
        Set<String> vertices = importer.getVertices();
        Set<VertexPair<String>> edges = importer.getEdges();
        System.out.println("Parser v size e size "+ vertices.size() + " - "+edges.size());

        DirectedAcyclicGraph<String, DefaultEdge> dag = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
        try {
            for (String v : vertices) dag.addVertex(URLDecoder.decode(v, "UTF-8"));

            for (VertexPair<String> edg : edges) dag.addEdge(URLDecoder.decode(edg.getFirst(), "UTF-8"), URLDecoder.decode(edg.getSecond(), "UTF-8"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return dag;
    }
}
