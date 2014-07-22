package io.mem0r1es.activitysubsumer.activities;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.utils.TimeOfDay;
import io.mem0r1es.activitysubsumer.utils.Utils;

/**
 * Class representing the user activity.
 * 
 * @author Sebastian Claici
 * changes: Ivan Gavrilovic
 */
public class UserActivity extends BasicActivity implements ContextualActivity {

	private String id;
	private Set<String> locCategories;
	private Set<TimeOfDay> timeOfDay;

	private String duration;
	/**
	 * Default value for the score
	 */
	private double score = 1;

	/**
	 * Creates new user activity
	 * 
	 * @param id
	 *            if of this activity, it should be unique. For instance it could be
	 *            {@code System#nanoTime()}
	 * @param verb
	 *            activity verb
	 * @param noun
	 *            activity noun
	 * @param locCategories
	 *            location categories
	 * @param timesOfDay
	 *            times of day when the activity occurred (it is possible that spreads across two or
	 *            more periods
	 * @param duration
	 *            duration of the activity in minutes
	 */
	public UserActivity(String id, String verb, String noun, Set<String> locCategories,
			Set<TimeOfDay> timesOfDay, String duration) {
		super(verb, noun);

		this.id = id;
		this.locCategories = new HashSet<String>();
		this.timeOfDay = new HashSet<TimeOfDay>();

        // all categories are lowercase
		Set<String> lowerCats = new HashSet<String>();
		for (String cat : locCategories)
			lowerCats.add(cat.toLowerCase());

		this.locCategories.addAll(lowerCats);
		this.timeOfDay.addAll(timesOfDay);
		this.duration = duration;
	}

	public UserActivity(String serializedInput) {
		deSerialize(serializedInput);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		UserActivity that = (UserActivity) o;

		if (Double.compare(that.score, score) != 0)
			return false;
		if (!duration.equals(that.duration))
			return false;
		if (!id.equals(that.id))
			return false;
		if (!locCategories.equals(that.locCategories))
			return false;
		if (!timeOfDay.equals(that.timeOfDay))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		long temp;
		result = 31 * result + id.hashCode();
		result = 31 * result + locCategories.hashCode();
		result = 31 * result + timeOfDay.hashCode();
		result = 31 * result + duration.hashCode();
		temp = Double.doubleToLongBits(score);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public void deSerialize(String input) {
		String parts[] = Utils.decodeParts(input);

		id = parts[1];
		verb = parts[2];
		noun = parts[3];

		this.locCategories = new HashSet<String>();
		Collections.addAll(locCategories, parts[4].split(Cons.ENTRY_SEPARATOR_REG));

		this.timeOfDay = new HashSet<TimeOfDay>();
		for (String s : parts[5].split(Cons.ENTRY_SEPARATOR_REG)) {
			timeOfDay.add(TimeOfDay.valueOf(s));
		}

		score = Double.parseDouble(parts[6]);
		duration = parts[7];
	}

	public String serialize() {
		List<String> parts = new LinkedList<String>();
		parts.add(this.getClass().getSimpleName());
		parts.add(id);
		parts.add(verb);
		parts.add(noun);

		// add all locCategories
		String locs = "";
		for (String s : locCategories) {
			locs += s + Cons.ENTRY_SEPARATOR;
		}
		parts.add(locs);

		// add all time periods
		String tms = "";
		for (TimeOfDay s : timeOfDay) {
			tms += s + Cons.ENTRY_SEPARATOR;
		}
		parts.add(tms);

		// add score and avg duration
		parts.add(Double.toString(score));
		parts.add(duration);

		return Utils.encodeParts(parts);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public int getMinDuration() {
		if (duration.equals("none"))
			return 0;
		return Integer.parseInt(duration);
	}

	@Override
	public int getMaxDuration() {
		if (duration.equals("none"))
			return Integer.MAX_VALUE;
		return Integer.parseInt(duration);
	}

	@Override
	public Set<String> getLocCategories() {
		return locCategories;
	}

	@Override
	public Set<TimeOfDay> getTimesOfDay() {
		return timeOfDay;
	}

	@Override
	public String toString() {
		return getVerb() + " - " + getNoun();
	}
}
