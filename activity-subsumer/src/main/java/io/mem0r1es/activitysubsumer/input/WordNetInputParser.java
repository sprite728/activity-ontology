package io.mem0r1es.activitysubsumer.input;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.sun.deploy.util.StringUtils;
import io.mem0r1es.activitysubsumer.utils.Cons;
import io.mem0r1es.activitysubsumer.utils.Utils;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Methods for parsing WordNet N-Triple input file
 *
 * @author Ivan Gavrilović
 */
public class WordNetInputParser {
    class Tuple3 {
        String subject;
        String predicate;
        String object;
        String type;

        Tuple3(String subject, String predicate, String object, String type) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
            this.type = type;
        }
    }

    /**
     * Parses the N-Triple WordNet file and leaves only nouns and verbs with hyponym
     * and synset_member predicates
     * <p>Tested against WordNet 3.1</p>
     *
     * @param inputFileName  path to N-Triple WordNet file
     * @param outputFileName output file location
     */
    public void reduceInput(String inputFileName, String outputFileName) {
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // use the FileManager to find the input file
        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + inputFileName + " not found");
        }

        model.read(in, null, "N-TRIPLE");
        StmtIterator iter = model.listStatements();
        Map<String, List<String>> nounSyns = new HashMap<>();
        Map<String, List<String>> nounHypo = new HashMap<>();
        Map<String, List<String>> verbsSyns = new HashMap<>();
        Map<String, List<String>> verbsHypo = new HashMap<>();

        while (iter.hasNext()) {
            Tuple3 t = parseStatement(iter.nextStatement());
            if (t != null) {
                if (t.predicate.equals(Cons.PRED_SYNSET)) {
                    if (t.type.equals("n")) {
                        Utils.addToMap(nounSyns, t.subject, t.object);
                    } else {
                        Utils.addToMap(verbsSyns, t.subject, t.object);
                    }
                } else {
                    if (t.type.equals("n")) {
                        Utils.addToMap(nounHypo, t.subject, t.object);
                    } else {
                        Utils.addToMap(verbsHypo, t.subject, t.object);
                    }
                }
            }
        }

        printToFile(outputFileName + "_nouns_synset", nounSyns);
        printToFile(outputFileName + "_nouns_hyponym", nounHypo);
        printToFile(outputFileName + "_verbs_synset", verbsSyns);
        printToFile(outputFileName + "_verbs_hyponym", verbsHypo);
        model.close();
    }

    /**
     * Prints the records to the file
     *
     * @param fileName path
     * @param records  map with records
     */
    private void printToFile(String fileName, Map<String, List<String>> records) {
        try {
            PrintWriter p = new PrintWriter(new File(fileName));

            for (Map.Entry<String, List<String>> e : records.entrySet()) {
                p.println(e.getKey() + " " + StringUtils.join(e.getValue(), " "));
            }
            p.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse single statement, return null if we are not interested in this type of statement
     *
     * @param stmt statement
     * @return {@link io.mem0r1es.activitysubsumer.input.WordNetInputParser.Tuple3} with values or {@code null}
     */
    private Tuple3 parseStatement(Statement stmt) {
        String predicate = stmt.getPredicate().getLocalName();

        // get only hyponyms and members of synsets
        if (predicate.equals(Cons.PRED_HYPONYM) || predicate.equals(Cons.PRED_SYNSET)) {
            String subjectURI = stmt.getSubject().getURI();
            // get only nouns and verbs
            if (subjectURI.endsWith("-N") || subjectURI.endsWith("-n") || subjectURI.endsWith("-v") || subjectURI.endsWith("-V")) {
                // it is always resource in this case, so this will always succeed
                String objectURI = stmt.getObject().asResource().getURI();
                String subject = subjectURI.substring(subjectURI.lastIndexOf("/") + 1);
                String object = objectURI.substring(subjectURI.lastIndexOf("/") + 1);
                String type = (subjectURI.endsWith("-N") || subjectURI.endsWith("-n")) ? "n" : "v";
                return new Tuple3(subject, predicate, object, type);
            }
        }
        return null;
    }
}
