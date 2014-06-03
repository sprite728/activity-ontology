package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.ext.CSVExporter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.util.VertexPair;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Read/write graph activities to file
 * @author Ivan Gavrilović
 */
public class ActivityGraphFileParser implements ActivityGraphParser {
    private ActivityMappingParser mappingParser;
    private String path;
    private String mappingPath;

    public ActivityGraphFileParser(ActivityMappingParser mappingParser, String path, String mappingPath) {
        this.mappingParser = mappingParser;
        this.path = path;
        this.mappingPath = mappingPath;
    }

    @Override
    public boolean write(Map<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>> clusters) {
        try {
            // write graph
            CSVExporter<BasicActivity, DefaultEdge> exporter = new CSVExporter<BasicActivity, DefaultEdge>();
            exporter.setDelimiter(" ");
            PrintWriter writerGraph = new PrintWriter(path);
            for (Map.Entry<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>> entry : clusters.entrySet()) {
                writerGraph.println(entry.getKey().toString());
                exporter.export(entry.getValue(), writerGraph);
                writerGraph.println(Cons.NEW_LINE + Cons.CLUSTER_SEPARATOR);
            }
            writerGraph.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>> readGraph() {
        Map<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>> clusters =
                new HashMap<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>>();
        try {
            File graphFile = new File(path);
            if (graphFile.exists()) {
                Scanner scanner = new Scanner(graphFile);
                while (scanner.hasNextLine()) {
                    // this one is the synset code with data
                    SynsetNode synsetNode = SynsetNode.deSerialize(scanner.nextLine());

                    Set<String> vertices = new HashSet<String>();
                    Set<VertexPair<String>> edges = new HashSet<VertexPair<String>>();
                    String line = scanner.nextLine();
                    while (!line.equals(Cons.CLUSTER_SEPARATOR) && scanner.hasNextLine()) {
                        // these are the activities
                        String[] ids = line.split("\\s");
                        Collections.addAll(vertices, ids);

                        for (int i = 1; i < ids.length; i++) edges.add(new VertexPair<String>(ids[0], ids[i]));
                        line = scanner.nextLine();
                    }

                    // build graph
                    DirectedAcyclicGraph<BasicActivity, DefaultEdge> dag =
                            new DirectedAcyclicGraph<BasicActivity, DefaultEdge>(DefaultEdge.class);
                    for (String v : vertices) {
                        dag.addVertex(mappingParser.codeToActivity(v));
                    }
                    for (VertexPair<String> e : edges) {
                        dag.addEdge(mappingParser.codeToActivity(e.getFirst()), mappingParser.codeToActivity(e.getSecond()));
                    }

                    clusters.put(synsetNode, dag);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clusters;
    }
}
