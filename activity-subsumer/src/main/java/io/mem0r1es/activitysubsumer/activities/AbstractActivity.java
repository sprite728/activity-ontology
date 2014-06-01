package io.mem0r1es.activitysubsumer.activities;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract activity. All activities should extend this class.
 * @author Ivan Gavrilović
 */
public abstract class AbstractActivity {
    /**
     * The id should be kept unique for all of the activities, because they are used to uniquely determined the activities
     * in the activity graph file saved to the disk
     */
    protected String id;
    protected String verb;
    protected String noun;

    /**
     * Creates new activity by parsing the serialized input
     * @param serializedInput input to serialize
     */
    protected AbstractActivity(String serializedInput) {
        deSerialize(serializedInput);
    }

    protected AbstractActivity(String id, String verb, String noun) {
        this.id = id;
        this.verb = verb;
        this.noun = noun;
    }

    /**
     * Serialize this activity
     * @return serialized activity
     */
    public String serialize() {
        List<String> parts = new LinkedList<String>();

        parts.add(this.getClass().getSimpleName());
        parts.add(id);
        parts.add(verb);
        parts.add(noun);

        return encodeParts(parts);
    }

    /**
     * Splits the input using space as delimiter, and decodes individual parts by using {@link java.net.URLDecoder}
     * @param input input to decode
     * @return array containing the decoded input components
     */
    public String[] decodeParts(String input) {
        String parts[] = input.split("\\s");
        for (int i = 0; i < parts.length; i++) {
            try {
                parts[i] = URLDecoder.decode(parts[i], "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parts;
    }

    /**
     * Concatenates the elements of the list with space as delimiter. It encoded the {@link java.net.URLEncoder} to individual parts before contatenation
     * @param parts parts to concatenate
     * @return string containing the concatenated, encoded string
     */
    public String encodeParts(List<String> parts) {
        String output = "";
        try {
            for (String s : parts) {
                output += URLEncoder.encode(s, "UTF-8") + " ";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }

    /**
     * All classes should implement this. It allows classes to deserialize the input and initialize object fields.
     * @param input input to deserialize
     */
    public abstract void deSerialize(String input);

    public String getId() {
        return id;
    }

    public String getVerb() {
        return verb;
    }

    public String getNoun() {
        return noun;
    }

    /**
     * Used to export the graph, as this method gets invoked when generating the activities graph
     * @return
     */
    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractActivity activity = (AbstractActivity) o;

        if (!id.equals(activity.id)) return false;
        if (!noun.equals(activity.noun)) return false;
        if (!verb.equals(activity.verb)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + verb.hashCode();
        result = 31 * result + noun.hashCode();
        return result;
    }
}
