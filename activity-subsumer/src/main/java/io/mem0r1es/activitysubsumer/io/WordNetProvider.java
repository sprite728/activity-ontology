package io.mem0r1es.activitysubsumer.io;

import java.io.File;
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
     * @param inputFileName  path to WordNet io file
     * @param outputFileName output file location
     */
    public abstract void parseInput(String inputFileName, String outputFileName);

    /**
     * Prints the records to the file
     *
     * @param fileName path
     * @param records  map with records
     */
    void printToFile(String fileName, Map<String, Set<String>> records) {
        try {
            File outFile = new File(fileName);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }

            PrintWriter p = new PrintWriter(outFile);

            for (Map.Entry<String, Set<String>> e : records.entrySet()) {
                // it is possible to have synset as 102342345-n, we want to remove -n suffix
                String noSuffix = e.getKey();
                if (e.getKey().contains("-")) {
                    noSuffix = e.getKey().substring(0, e.getKey().lastIndexOf("-"));
                }

                if (e.getValue().isEmpty()) {
                    p.println(noSuffix);
                } else {
                    for (String v : e.getValue()) {
                        String valNoSuffix = v;
                        if (v.contains("-")) {
                            valNoSuffix = v.substring(0, v.lastIndexOf("-"));
                        }
                        p.println(noSuffix + " " + valNoSuffix);
                    }
                }
            }
            p.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
