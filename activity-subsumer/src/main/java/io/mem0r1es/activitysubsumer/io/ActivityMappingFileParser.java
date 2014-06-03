package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.ActivityFactory;
import io.mem0r1es.activitysubsumer.utils.Utils;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * Read/write mappings of activity codes (ids) from graph to activities.
 * @author Ivan Gavrilović
 */
public class ActivityMappingFileParser implements ActivityMappingParser {
    private Map<String, BasicActivity> mappings = null;

    private String path;

    public ActivityMappingFileParser(String path) {
        this.path = path;
    }

    @Override
    public boolean writeMappings(Map<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>> clusters) {
        try {
            // now write mappings
            Set<BasicActivity> activities = new HashSet<BasicActivity>();
            for (Map.Entry<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>> entry : clusters.entrySet()) {
                activities.addAll(entry.getValue().vertexSet());
            }

            PrintWriter writerMapping = new PrintWriter(path);
            for (BasicActivity a : activities) {
                writerMapping.println(ActivityFactory.serialize(a));
            }
            writerMapping.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, BasicActivity> readMappings() {
        mappings = new HashMap<String, BasicActivity>();
        try {
            Scanner scanner = new Scanner(new FileInputStream(path));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // these are the activities
                try {
                    BasicActivity activity = ActivityFactory.deserialize(line);
                    String id = Utils.decodeParts(line)[1];
                    mappings.put(id, activity);
                } catch (Exception e) {
                    System.out.println(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mappings;
    }

    /**
     * Maps the codes found in the graphs file to the objects. Currently, it maps the activity ids to objects
     *
     * @param code activity code i.e. activity id
     * @return activity with that code
     */
    public BasicActivity codeToActivity(String code) {
        if (mappings == null) {
            readMappings();
        }
        return mappings.get(code);
    }
}
