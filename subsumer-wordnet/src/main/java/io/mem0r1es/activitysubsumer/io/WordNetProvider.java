package io.mem0r1es.activitysubsumer.io;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public abstract class WordNetProvider {
    /**
     * Parses the WordNet file and leaves only nouns and verbs with hyponym
     * and synset_member predicates
     * <p>Tested against WordNet 3.1</p>
     *
     * @param input  input to WordNet io file
     */
    public abstract void parseInput(InputStream input);

    /**
     * Prints the records to the file
     *
     * @param writer writer
     * @param records  map with records
     */
    void printToFile(PrintWriter writer, Map<String, Set<String>> records) {
        try {
            for (Map.Entry<String, Set<String>> e : records.entrySet()) {
                // it is possible to have synset as 102342345-n, we want to remove -n suffix
                String noSuffix = e.getKey();
                if (e.getKey().contains("-")) {
                    noSuffix = e.getKey().substring(0, e.getKey().lastIndexOf("-"));
                }

                if (e.getValue().isEmpty()) {
                    writer.println(noSuffix);
                } else {
                    for (String v : e.getValue()) {
                        String valNoSuffix = v;
                        if (v.contains("-")) {
                            valNoSuffix = v.substring(0, v.lastIndexOf("-"));
                        }
                        writer.println(noSuffix + " " + valNoSuffix);
                    }
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
