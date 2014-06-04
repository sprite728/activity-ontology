package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.activities.UserActivity;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.utils.TimeOfDay;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Reads the default activities collected from crowdsourcing experiment.
 * @author Ivan Gavrilović
 */
public class DefaultActivitiesProvider {

    private String path;

    public DefaultActivitiesProvider(String path) {
        this.path = path;
    }

    /**
     * Get all of the default activities that tie together activity and contextual parameters
     *
     * @return set of UserActivities
     */
    public Set<UserActivity> read() {
        Set<UserActivity> defaultActivities = new HashSet<UserActivity>();
        try {

            Scanner s = new Scanner(new File(path));
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
