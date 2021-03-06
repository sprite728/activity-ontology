package io.mem0r1es.activitysubsumer.io;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import io.mem0r1es.activitysubsumer.utils.SubConf;
import io.mem0r1es.activitysubsumer.utils.Utils;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Methods for parsing RDF WordNet N-Triple io file. Should be invoked on the data obtained at
 * <a href="http://wordnet-rdf.princeton.edu/">http://wordnet-rdf.princeton.edu/</> in N-Triples
 * format
 *
 * @author Ivan Gavrilović
 */
public class WordNetRDFInputProvider extends WordNetProvider {
    class Tuple4 {
        String subject;
        String predicate;
        String object;
        String type;

        Tuple4(String subject, String predicate, String object, String type) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
            this.type = type;
        }
    }

    private PrintWriter nounGraph;
    private PrintWriter nounSynset;
    private PrintWriter verbGraph;
    private PrintWriter verbSynset;

    public WordNetRDFInputProvider(PrintWriter nounGraph, PrintWriter nounSynset, PrintWriter verbGraph, PrintWriter verbSynset) {
        this.nounGraph = nounGraph;
        this.nounSynset = nounSynset;
        this.verbGraph = verbGraph;
        this.verbSynset = verbSynset;
    }

    @Override
    public void parseInput(InputStream input) {
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        model.read(input, null, "N-TRIPLE");
        StmtIterator iter = model.listStatements();
        Map<String, Set<String>> nounSyns = new HashMap<String, Set<String>>();
        Map<String, Set<String>> nounHypo = new HashMap<String, Set<String>>();
        Map<String, Set<String>> verbsSyns = new HashMap<String, Set<String>>();
        Map<String, Set<String>> verbsHypo = new HashMap<String, Set<String>>();

        while (iter.hasNext()) {
            Tuple4 t = parseStatement(iter.nextStatement());
            if (t != null) {
                if (t.predicate.equals(SubConf.CONFIG.getPredicateSynset())) {
                    if (t.type.equals("n")) {
                        Utils.addToMap(nounSyns, t.subject.toLowerCase(), t.object);
                    } else {
                        Utils.addToMap(verbsSyns, t.subject.toLowerCase(), t.object);
                    }
                } else {
                    if (t.type.equals("n")) {
                        Utils.addToMap(nounHypo, t.subject.toLowerCase(), t.object);
                    } else {
                        Utils.addToMap(verbsHypo, t.subject.toLowerCase(), t.object);
                    }
                }
            }
        }

        fixMissing(nounSyns, nounHypo);
        fixMissing(verbsSyns, verbsHypo);

        printToFile(nounSynset, nounSyns);
        printToFile(nounGraph, nounHypo);
        printToFile(verbSynset, verbsSyns);
        printToFile(verbSynset, verbsHypo);
        model.close();
    }

    /**
     * Parse single statement, return null if we are not interested in this type of statement
     *
     * @param stmt statement
     * @return {@link io.mem0r1es.activitysubsumer.io.WordNetRDFInputProvider.Tuple4} with values or {@code null}
     */
    private Tuple4 parseStatement(Statement stmt) {
        String predicate = stmt.getPredicate().getLocalName();

        // get only hyponyms and members of synsets
        if (predicate.equals(SubConf.CONFIG.getPredicateHyponym()) || predicate.equals(SubConf.CONFIG.getPredicateSynset())) {
            String subjectURI = stmt.getSubject().getURI();
            // get only nouns and verbs
            if (subjectURI.endsWith("-N") || subjectURI.endsWith("-n") || subjectURI.endsWith("-v") || subjectURI.endsWith("-V")) {
                // it is always resource in this case, so this will always succeed
                String objectURI = stmt.getObject().asResource().getURI();
                String subject = subjectURI.substring(subjectURI.lastIndexOf("/") + 1);
                String object = objectURI.substring(objectURI.lastIndexOf("/") + 1);
                String type = (subjectURI.endsWith("-N") || subjectURI.endsWith("-n")) ? "n" : "v";
                return new Tuple4(subject, predicate, object, type);
            }
        }
        return null;
    }

    /**
     * Some data is in the hyponym graph, but is missing from the synset file. Retrieve it from WordNet API
     *
     * @param synsets
     * @param graph
     */
    public void fixMissing(Map<String, Set<String>> synsets, Map<String, Set<String>> graph) {
        int i = 0;
        for (String s : graph.keySet()) {
            if (!synsets.containsKey(s)) {
                synsets.put(s, getMembers(s));
                System.out.println("FIXING CODE = " + s + ", NO FIXED =  " + i + ", NUM OF SYNSET MAPPINGS = " + synsets.size());
                i++;
            }
            for (String inner : graph.get(s)) {
                if (!synsets.containsKey(inner)) {
                    System.out.println("FIXING CODE = " + inner + ", NO FIXED =  " + i + ", NUM OF SYNSET MAPPINGS = " + synsets.size());
                    synsets.put(inner.toLowerCase(), getMembers(inner));
                    i++;
                }
            }
        }
        System.out.println("Fixed " + i + " times");
    }

    public Set<String> getMembers(String code) {
        Set<String> members = new HashSet<String>();
        try {
            URL website = new URL("http://wordnet-rdf.princeton.edu/wn31/" + code + ".rdf");
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(SubConf.CONFIG.getTmpFileIn());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            // create an empty model
            Model model = ModelFactory.createDefaultModel();

            // use the FileManager to find the io file
            InputStream in = FileManager.get().open(SubConf.CONFIG.getTmpFileIn());

            model.read(in, null, "RDF/XML");
            StmtIterator iter = model.listStatements();
            while (iter.hasNext()) {
                Tuple4 t = parseStatement(iter.nextStatement());
                if (t != null) {
                    if (t.predicate.equals(SubConf.CONFIG.getPredicateSynset())) {
                        members.add(t.object);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return members;
    }
}
