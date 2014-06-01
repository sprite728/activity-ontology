package io.mem0r1es.activitysubsumer.activities;

/**
 * Represents the subsumed activity
 * @author Ivan Gavrilović
 */
public class SubsumedActivity extends AbstractActivity {
    public SubsumedActivity(String id, String verb, String noun) {
        super(id, verb, noun);
    }

    public SubsumedActivity(String serializedInput){
        super(serializedInput);
    }

    @Override
    public void deSerialize(String input) {
        String parts[] = decodeParts(input);

        if (!parts[0].equals(SubsumedActivity.class.getSimpleName())) {
            System.err.println("Deserializing to SubsumedActivity ERROR for: " + input);
        }

        id = parts[1];
        verb = parts[2];
        noun = parts[3];
    }
}
