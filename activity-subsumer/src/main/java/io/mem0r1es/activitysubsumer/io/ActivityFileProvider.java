package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.activities.ActivityFactory;
import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.classifier.ActivityCluster;
import io.mem0r1es.activitysubsumer.graphs.SynsetForest;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author Ivan Gavrilović
 */
public class ActivityFileProvider implements ActivityProvider {
    private InputStream input;
    private PrintWriter output;

    public ActivityFileProvider(InputStream input, PrintWriter output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public Map<String, ActivityCluster> read(SynsetForest verbs, SynsetForest nouns) {
        Map<String, ActivityCluster> cluster = new HashMap<String, ActivityCluster>();
        try {
            Scanner scanner = new Scanner(input);

            while (scanner.hasNextLine()) {
                String category = scanner.nextLine();

                Map<SynsetNode, Set<ContextualActivity>> synsetActivities = new HashMap<SynsetNode, Set<ContextualActivity>>();
                String line = scanner.nextLine();
                while (line != null && !line.equals(Cons.CATEGORY_SEPARATOR)) {
                    SynsetNode sn = SynsetNode.deSerialize(line);

                    Set<ContextualActivity> acts = new HashSet<ContextualActivity>();
                    line = scanner.nextLine();
                    while (line != null && !line.equals(Cons.CLUSTER_SEPARATOR)) {
                        acts.add(ActivityFactory.deserialize(line));
                        line = scanner.nextLine();
                    }
                    synsetActivities.put(sn, acts);

                    if (scanner.hasNextLine()) line = scanner.nextLine();
                }
                cluster.put(category, new ActivityCluster(verbs, nouns, synsetActivities));
            }
        } catch (Exception e) {
            System.err.println("File format exception!");
            e.printStackTrace();
        }
        return cluster;
    }

    @Override
    public boolean write(Map<String, ActivityCluster> activities) {
        try {
            for (String cat : activities.keySet()) {
                output.println(cat);

                Map<SynsetNode, Set<ContextualActivity>> categoryActivities = activities.get(cat).getActivities();
                for (SynsetNode sn : categoryActivities.keySet()) {
                    output.println(sn);

                    for (ContextualActivity ca : categoryActivities.get(sn))
                        output.println(ActivityFactory.serialize(ca));

                    output.println(Cons.CLUSTER_SEPARATOR);
                }

                output.println(Cons.CATEGORY_SEPARATOR);
            }

            output.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
