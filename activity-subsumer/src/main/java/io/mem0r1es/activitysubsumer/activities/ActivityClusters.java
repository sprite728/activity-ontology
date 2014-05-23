package io.mem0r1es.activitysubsumer.activities;

import io.mem0r1es.activitysubsumer.activities.AbstractActivity;
import io.mem0r1es.activitysubsumer.graphs.NounsSynsetGraph;
import io.mem0r1es.activitysubsumer.graphs.VerbsSynsetForest;
import io.mem0r1es.activitysubsumer.io.ActivityIO;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public class ActivityClusters {
    private Map<SynsetNode, DirectedAcyclicGraph<AbstractActivity, DefaultEdge>> clusters;
    private String graphPath;
    private String mappingPath;

    private VerbsSynsetForest verbs;
    private NounsSynsetGraph nouns;

    public ActivityClusters(VerbsSynsetForest verbs, NounsSynsetGraph nouns, String graphPath, String mappingPath) {
        this.verbs = verbs;
        this.nouns = nouns;
        this.graphPath = graphPath;
        this.mappingPath = mappingPath;

        initClusters();
    }

    public void initClusters() {
        ActivityIO parser = new ActivityIO(graphPath, mappingPath);
        clusters = parser.readGraph();
    }

    public void saveClusters() {
        ActivityIO parser = new ActivityIO(graphPath, mappingPath);
        parser.write(clusters);
    }

    public void addActivity(AbstractActivity activity){
        Set<SynsetNode> subgraphs = verbs.find(activity.getVerb());
        for(SynsetNode s: subgraphs){
            DirectedAcyclicGraph<AbstractActivity, DefaultEdge> dag = clusters.get(s);
            if (dag == null){
                dag =  new DirectedAcyclicGraph<AbstractActivity, DefaultEdge>(DefaultEdge.class);
            }

            dag.addVertex(activity);
            clusters.put(s, dag);
        }
    }
}
