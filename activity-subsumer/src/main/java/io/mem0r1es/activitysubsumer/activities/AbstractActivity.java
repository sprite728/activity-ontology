package io.mem0r1es.activitysubsumer.activities;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ivan Gavrilović
 */
public abstract class AbstractActivity {
    protected String id;
    protected String verb;
    protected String noun;

    protected AbstractActivity(String serializedInput) {
        deSerialize(serializedInput);
    }

    protected AbstractActivity(String id, String verb, String noun) {
        this.id = id;
        this.verb = verb;
        this.noun = noun;
    }

    public String serialize() {
        List<String> parts = new LinkedList<String>();

        parts.add(this.getClass().getCanonicalName());
        parts.add(id);
        parts.add(verb);
        parts.add(noun);

        return encodeParts(parts);
    }

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
     * Used to export the graph
     * @return
     */
    @Override
    public String toString() {
        return id;
    }
}
