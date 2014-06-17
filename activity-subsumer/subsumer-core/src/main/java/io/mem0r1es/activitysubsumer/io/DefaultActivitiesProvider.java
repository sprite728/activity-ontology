package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.activities.ContextualActivity;
import io.mem0r1es.activitysubsumer.activities.DefaultActivity;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.utils.TimeOfDay;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Reads the default activities collected from crowdsourcing experiment.
 * @author Ivan Gavrilović
 */
public class DefaultActivitiesProvider {
    private static final Logger logger = Logger.getLogger(DefaultActivitiesProvider.class.getCanonicalName());

    private InputStream input;

    public DefaultActivitiesProvider(InputStream input) {
        this.input = input;
    }

    /**
     * Get all of the default activities that tie together activity and contextual parameters
     *
     * @return set of UserActivities
     */
    public Set<ContextualActivity> read() {
        Set<ContextualActivity> defaultActivities = new HashSet<ContextualActivity>();
        try {

            Scanner s = new Scanner(input);
            while (s.hasNextLine()) {
                // verb, noun, locations, times, duration
                String infos[] = (s.nextLine()).split(",");

                Set<String> locations = new HashSet<String>();
                Collections.addAll(locations, infos[2].split(Cons.ENTRY_SEPARATOR_REG));

                Set<TimeOfDay> times = new HashSet<TimeOfDay>();
                for (String tm : infos[3].split(Cons.ENTRY_SEPARATOR_REG)) {
                    try {
                        times.add(TimeOfDay.valueOf(tm.toUpperCase()));
                    }
                    catch (Exception e){
                        logger.error("While parsing default activities: "+infos[3]);
                    }
                }

                defaultActivities.add(new DefaultActivity(infos[0], infos[1], locations, times, infos[4]));
            }

            return defaultActivities;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultActivities;
    }
}
