package io.mem0r1es.activitysubsumer.activities;

/**
 * Basic activity with verb and noun.
 *
 * @author Ivan Gavrilović
 */
public class BasicActivity {
    protected String verb;
    protected String noun;

    public BasicActivity(String verb, String noun) {
        this.verb = verb;
        this.noun = noun;
    }

    protected BasicActivity(){}

    public String getVerb() {
        return verb;
    }

    public String getNoun() {
        return noun;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicActivity activity = (BasicActivity) o;

        if (!noun.equals(activity.noun)) return false;
        if (!verb.equals(activity.verb)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = verb.hashCode();
        result = 31 * result + noun.hashCode();
        return result;
    }
}
