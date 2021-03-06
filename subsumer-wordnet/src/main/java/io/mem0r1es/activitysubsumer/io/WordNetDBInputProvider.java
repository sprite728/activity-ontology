package io.mem0r1es.activitysubsumer.io;


import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.*;

/**
 * Used to parse word net data.noun and data.verb files, downloaded from
 * <a href ="http://wordnetcode.princeton.edu/wn3.1.dict.tar.gz">http://wordnetcode.princeton.edu/wn3.1.dict.tar.gz</a>
 * Remove the copyright info from the file header when parsing.
 *
 * @author Ivan Gavrilović
 */
public class WordNetDBInputProvider extends WordNetProvider {
    // the symbol for hyponym relation in the WordNet file
    private static final String HYPONYM = "~";
    private static final String INSTANCE_HYPONYM = "~i";

    private PrintWriter writerGraph;
    private PrintWriter writerSynset;

    public WordNetDBInputProvider(PrintWriter writerGraph, PrintWriter writerSynset) {
        this.writerGraph = writerGraph;
        this.writerSynset = writerSynset;
    }

    @Override
    public void parseInput(InputStream input) {
        try {
            Map<String, Set<String>> synsets = new HashMap<String, Set<String>>();
            Map<String, Set<String>> hyponyms = new HashMap<String, Set<String>>();

            Scanner s = new Scanner(input);

            while (s.hasNextLine()) {
                String line = s.nextLine();
                StringTokenizer tokenizer = new StringTokenizer(line);

                // first symbol is the synset code
                String synsetCode = tokenizer.nextToken();

                // skip second
                tokenizer.nextToken();

                // this one is either n or v; if noun we prepend 1 to the code, if verb 2 (like in the RDF version)
                // this is a nice way create namespaces for different types of words
                String prefix = tokenizer.nextToken().toLowerCase().equals("n") ? "1" : "2";
                int cntSynsetMembers = Integer.parseInt(tokenizer.nextToken(), 16);

                // get synset members
                Set<String> members = new HashSet<String>();
                for (int i = 0; i < cntSynsetMembers; i++) {
                    members.add(URLEncoder.encode(tokenizer.nextToken().replace("_", " "), "UTF-8"));
                    tokenizer.nextToken();
                }

                // find all hyponym and instance hyponym relationships
                int cntRelationships = Integer.parseInt(tokenizer.nextToken());
                Set<String> rels = new HashSet<String>();
                for (int i = 0; i < cntRelationships; i++) {
                    String type = tokenizer.nextToken();
                    String code = tokenizer.nextToken();
                    tokenizer.nextToken();
                    tokenizer.nextToken();

                    if (type.equals(HYPONYM) || type.equals(INSTANCE_HYPONYM)) {
                        rels.add(prefix + code);
                    }
                }

                synsets.put(prefix + synsetCode, members);
                hyponyms.put(prefix + synsetCode, rels);
            }
            printToFile(writerSynset, synsets);
            printToFile(writerGraph, hyponyms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
