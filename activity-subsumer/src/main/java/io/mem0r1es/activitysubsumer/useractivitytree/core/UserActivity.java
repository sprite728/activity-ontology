package io.mem0r1es.activitysubsumer.useractivitytree.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper class for activities.
 * 
 * @author Sebastian Claici
 */
public final class UserActivity implements Serializable {
	private final String verb;
	private final String noun;
	private final Set<String> locations = new HashSet<String>();
	private final Set<String> timeOfDay = new HashSet<String>();

	private double score = 1;
	private String avgDuration;

	private static final long serialVersionUID = 7526471156722776147L;

	public static final UserActivity DEFAULT_NODE = new UserActivity("XXX", "XXX");

	/**
	 * <p>
	 * Creates an <b>UserActivity</b> object with all the required data.
	 * </p>
	 * <p>
	 * Note that this constructor assumes each activity is specified as in the
	 * <i>activities.graph</i> file.
	 * </p>
	 */
	public UserActivity(String verb, String noun, String location, String timeOfDay, String avgDuration) {
		this.verb = verb;
		this.noun = noun;
		this.avgDuration = avgDuration;

		String[] locations = location.split("[|]");
		for (int i = 0; i < locations.length; i++) {
			this.locations.add(locations[i]);
		}

		String[] timeOfDays = timeOfDay.split("[|]");
		for (int i = 0; i < timeOfDays.length; i++) {
			this.timeOfDay.add(timeOfDays[i]);
		}
	}

	public UserActivity(String verb, String noun, Set<String> locations, Set<String> timesOfDay, String avgDuration) {
		this.verb = verb;
		this.noun = noun;
		this.locations.addAll(locations);
		this.timeOfDay.addAll(timesOfDay);
		this.avgDuration = avgDuration;
	}

	public UserActivity(String verb, String noun) {
		this.verb = verb;
		this.noun = noun;
	}

	public String getVerb() {
		return verb;
	}

	public String getNoun() {
		return noun;
	}

	public String getAvgDuration() {
		return avgDuration;
	}

	public Set<String> getLocations() {
		return locations;
	}

	public Set<String> getTimeOfDay() {
		return timeOfDay;
	}

	public double getScore() {
		return score;
	}

	public void setAvgDuration(String avgDuration) {
		this.avgDuration = avgDuration;
	}

	public void addLocation(String location) {
		locations.add(location);
	}

	public void addTimeOfDay(String timeOfDay) {
		this.timeOfDay.add(timeOfDay);
	}

	public void augmentScore(double factor) {
		score = score * factor;
	}

	@Override
	public String toString() {
		return verb + "-" + noun;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserActivity other = (UserActivity) obj;
		if (noun == null) {
			if (other.noun != null)
				return false;
		} else if (!noun.equals(other.noun))
			return false;
		if (verb == null) {
			if (other.verb != null)
				return false;
		} else if (!verb.equals(other.verb))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((noun == null) ? 0 : noun.hashCode());
		result = prime * result + ((verb == null) ? 0 : verb.hashCode());
		return result;
	}

	public void addLocations(Set<String> locations) {
		this.locations.addAll(locations);
	}

	public void addTimesOfDay(Set<String> timeOfDay) {
		this.timeOfDay.addAll(timeOfDay);
	}
}
