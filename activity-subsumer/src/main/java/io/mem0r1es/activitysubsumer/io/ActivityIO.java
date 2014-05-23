package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.activities.AbstractActivity;
import io.mem0r1es.activitysubsumer.activities.ActivityFactory;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.ext.CSVExporter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.util.VertexPair;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author Ivan Gavrilović
 */
public class ActivityIO {
    private Map<String, AbstractActivity> mappings = null;
    private String graphPath;
    private String mappingsPath;

    public ActivityIO(String graphPath, String mappingsPath) {
        this.graphPath = graphPath;
        this.mappingsPath = mappingsPath;
    }

    /**
     * Writes the activity graph and mappings to the files
     *
     * @param clusters
     * @return
     */
    public boolean write(Map<SynsetNode, DirectedAcyclicGraph<AbstractActivity, DefaultEdge>> clusters) {
        try {
            // write graph
            CSVExporter<AbstractActivity, DefaultEdge> exporter = new CSVExporter<AbstractActivity, DefaultEdge>();
            exporter.setDelimiter(" ");
            PrintWriter writerGraph = new PrintWriter(graphPath);
            for (Map.Entry<SynsetNode, DirectedAcyclicGraph<AbstractActivity, DefaultEdge>> entry : clusters.entrySet()) {
                writerGraph.println(entry.getKey().toString());
                exporter.export(entry.getValue(), writerGraph);
                writerGraph.println(Cons.NEW_LINE + Cons.CLUSTER_SEPARATOR);
            }
            writerGraph.close();

            // now write mappings
            Set<AbstractActivity> activities = new HashSet<AbstractActivity>();
            for (Map.Entry<SynsetNode, DirectedAcyclicGraph<AbstractActivity, DefaultEdge>> entry : clusters.entrySet()) {
                activities.addAll(entry.getValue().vertexSet());
            }
            PrintWriter writerMapping = new PrintWriter(mappingsPath);
            for (AbstractActivity a : activities) {
                writerMapping.println(a.serialize());
            }
            writerMapping.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<SynsetNode, DirectedAcyclicGraph<AbstractActivity, DefaultEdge>> readGraph() {
        Map<SynsetNode, DirectedAcyclicGraph<AbstractActivity, DefaultEdge>> clusters =
                new HashMap<SynsetNode, DirectedAcyclicGraph<AbstractActivity, DefaultEdge>>();
        try {
            Scanner scanner = new Scanner(new FileInputStream(graphPath));
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
                DirectedAcyclicGraph<AbstractActivity, DefaultEdge> dag =
                        new DirectedAcyclicGraph<AbstractActivity, DefaultEdge>(DefaultEdge.class);
                for(String v:vertices){
                    dag.addVertex(codeToActivity(v));
                }
                for(VertexPair<String> e: edges){
                    dag.addEdge(codeToActivity(e.getFirst()), codeToActivity(e.getSecond()));
                }

                clusters.put(synsetNode, dag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clusters;
    }

    private AbstractActivity codeToActivity(String code) {
        if (mappings == null) {
            readMappings();
        }
        return mappings.get(code);
    }

    private void readMappings() {
        mappings = new HashMap<String, AbstractActivity>();
        try {
            Scanner scanner = new Scanner(new FileInputStream(mappingsPath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // these are the activities
                AbstractActivity activity = ActivityFactory.getActivity(line);
                mappings.put(activity.getId(), activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
