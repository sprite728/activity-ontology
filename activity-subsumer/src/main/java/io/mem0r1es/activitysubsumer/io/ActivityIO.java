package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.activities.BasicActivity;
import io.mem0r1es.activitysubsumer.activities.UserActivity;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.utils.TimeOfDay;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.util.*;

/**
 * Reads and writes the activity clusters to the storage
 *
 * @author Ivan Gavrilović
 */
public class ActivityIO {

    private String defaultActivitiesPath;

    private ActivityGraphParser graphParser;
    private ActivityMappingParser mappingParser;

    /**
     * Creates new object.
     *
     * @param graphParser           used to read/write the activities graph
     * @param mappingParser         used to read/write the mapping from codes used to denote the graph nodes, to activities
     * @param defaultActivitiesPath path to the file containing the default activities
     */
    public ActivityIO(ActivityGraphParser graphParser, ActivityMappingParser mappingParser, String defaultActivitiesPath) {
        this.graphParser = graphParser;
        this.mappingParser = mappingParser;

        this.defaultActivitiesPath = defaultActivitiesPath;
    }

    /**
     * Writes the activity graph and mappings to the files. Graph file contains the only the ids denoting the relationships
     * between the activities. In the current implementation, these codes are retrieved from {@link io.mem0r1es.activitysubsumer.activities.BasicActivity#toString()}
     * which is the {@code id} of the activity.
     *
     * @param clusters activity clusters
     * @return {code true} if the write is success, {@code false} otherwise
     */
    public boolean write(Map<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>> clusters) {
        return graphParser.write(clusters) && mappingParser.writeMappings(clusters);
    }

    /**
     * Reads the graph from the file, and generates the clusters
     *
     * @return activity clusters
     */
    public Map<SynsetNode, DirectedAcyclicGraph<BasicActivity, DefaultEdge>> read() {
        return graphParser.readGraph();
    }

    /**
     * Get all of the default activities that tie together activity and contextual parameters
     *
     * @return set of UserActivities
     */
    public Set<UserActivity> getDefaultActivities() {
        Set<UserActivity> defaultActivities = new HashSet<UserActivity>();
        try {

            Scanner s = new Scanner(new File(defaultActivitiesPath));
            while (s.hasNextLine()) {
                // verb, noun, locations, times, duration
                String infos[] = (s.nextLine()).split(",");

                Set<String> locations = new HashSet<String>();
                Collections.addAll(locations, infos[2].split(Cons.ENTRY_SEPARATOR));

                Set<TimeOfDay> times = new HashSet<TimeOfDay>();
                for (String tm : infos[3].split(Cons.ENTRY_SEPARATOR)) {
                    times.add(TimeOfDay.valueOf(tm));
                }

                defaultActivities.add(new UserActivity("0", infos[0], infos[1], locations, times, infos[4]));
            }

            return defaultActivities;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultActivities;
    }
}
