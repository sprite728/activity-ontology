package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.activities.AbstractActivity;
import io.mem0r1es.activitysubsumer.activities.ActivityFactory;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.ext.CSVExporter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.util.VertexPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * Reads and writes the activity clusters to the storage
 * @author Ivan Gavrilović
 */
public class ActivityIO {
    private Map<String, AbstractActivity> mappings = null;
    private String graphPath;
    private String mappingsPath;

    /**
     * Creates new object.
     * @param graphPath path to the graph file
     * @param mappingsPath path to the file mapping the code to the objects
     */
    public ActivityIO(String graphPath, String mappingsPath) {
        this.graphPath = graphPath;
        this.mappingsPath = mappingsPath;
    }

    /**
     * Writes the activity graph and mappings to the files. Graph file contains the only the ids denoting the relationships
     * between the activities. In the current implementation, these codes are retrieved from {@link io.mem0r1es.activitysubsumer.activities.AbstractActivity#toString()}
     * which is the {@code id} of the activity.
     *
     * @param clusters activity clusters
     * @return {code true} if the write is success, {@code false} otherwise
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

    /**
     * Reads the graph from the file, and generates the clusters
     * @return activity clusters
     */
    public Map<SynsetNode, DirectedAcyclicGraph<AbstractActivity, DefaultEdge>> readGraph() {
        Map<SynsetNode, DirectedAcyclicGraph<AbstractActivity, DefaultEdge>> clusters =
                new HashMap<SynsetNode, DirectedAcyclicGraph<AbstractActivity, DefaultEdge>>();
        try {
            File graphFile = new File(graphPath);
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
                    DirectedAcyclicGraph<AbstractActivity, DefaultEdge> dag =
                            new DirectedAcyclicGraph<AbstractActivity, DefaultEdge>(DefaultEdge.class);
                    for (String v : vertices) {
                        dag.addVertex(codeToActivity(v));
                    }
                    for (VertexPair<String> e : edges) {
                        dag.addEdge(codeToActivity(e.getFirst()), codeToActivity(e.getSecond()));
                    }

                    clusters.put(synsetNode, dag);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clusters;
    }

    /**
     * Maps the codes found in the graphs file to the objects. Currently, it maps the activity ids to objects
     * @param code activity code i.e. activity id
     * @return activity with that code
     */
    private AbstractActivity codeToActivity(String code) {
        if (mappings == null) {
            readMappings();
        }
        return mappings.get(code);
    }

    /**
     * Initializes the mappings from activity codes (activity ids) to activity objects
     */
    private void readMappings() {
        mappings = new HashMap<String, AbstractActivity>();
        try {
            Scanner scanner = new Scanner(new FileInputStream(mappingsPath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // these are the activities
                try {
                    AbstractActivity activity = ActivityFactory.getActivity(line);
                    mappings.put(activity.getId(), activity);
                } catch (Exception e) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
