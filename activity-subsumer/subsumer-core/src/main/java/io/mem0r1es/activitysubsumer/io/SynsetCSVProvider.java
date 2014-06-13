package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.wordnet.Dict;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNodeProxy;
import io.mem0r1es.activitysubsumer.wordnet.SynsetStore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;

/**
 * Reads the csv file containing {@link io.mem0r1es.activitysubsumer.wordnet.SynsetNode}
 * as vertices. Direct edge from node A->B means that B is hyponym of A. For instance:
 * (pizza, pizza pie) -- hyponym --> (pizza made with a thick crust)
 *
 * @author Ivan Gavrilović
 */
public class SynsetCSVProvider implements SynsetProvider {
    private InputStream synsetStream;
    private InputStream wordStream;
    private InputStream childStream;
    private InputStream parentStream;

    private Dict dict;

    /**
     * Holding all synset data
     */
    private SynsetStore store;

    /**
     * Creates new {@link SynsetCSVProvider}
     *
     * @param childStream  path to the file with hyponyms graph
     * @param synsetStream path that contains the mappings from synset codes to words
     */
    public SynsetCSVProvider(InputStream synsetStream, InputStream wordStream, InputStream childStream, InputStream parentStream, SynsetStore store, Dict dict) {
        this.store = store;
        this.wordStream = wordStream;
        this.synsetStream = synsetStream;
        this.childStream = childStream;
        this.parentStream = parentStream;
        this.dict = dict;
    }

    /**
     * Read parent hyponyms and child hyponyms files, depending on the specified parameter.
     *
     * @param readingChildren if {@code true} read children file, if {@code false} read parents
     */
    private void readHyponyms(boolean readingChildren) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(readingChildren ? childStream : parentStream));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(" ");
                if (parts.length == 1) {
                    if (readingChildren) store.addChild(Integer.parseInt(parts[0]), -1);
                    else store.addParent(Integer.parseInt(parts[0]), -1);

                } else if (parts.length == 2) {
                    int fstCode = Integer.parseInt(parts[0]);
                    int sndCode = Integer.parseInt(parts[1]);
                    if (readingChildren) store.addChild(fstCode, sndCode);
                    else store.addParent(fstCode, sndCode);

                }

                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Set<SynsetNode> read() {
        // this populates the dict
        readWords();
        readHyponyms(false);
        readHyponyms(true);
        return readSynsets();
    }

    /**
     * Parse the synsets file and make mapping from synset codes to words
     */
    private Set<SynsetNode> readSynsets() {
        BufferedReader reader = null;
        Set<SynsetNode> proxies = new HashSet<SynsetNode>();
        try {
            reader = new BufferedReader(new InputStreamReader(synsetStream));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    Integer synsetCode = Integer.parseInt(parts[0]);

                    proxies.add(new SynsetNodeProxy(synsetCode));

                    String newWord = URLDecoder.decode(parts[1], "UTF-8");
                    int newWordId = dict.get(newWord);

                    store.addCodeWord(synsetCode, newWordId);
                } else {
                    throw new RuntimeException("Unexpected file format. Each line should have code - word syntax");
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return proxies;
    }

    private void readWords() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(wordStream));
            String line = reader.readLine();
            while (line != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    String newWord = URLDecoder.decode(parts[0], "UTF-8");

                    int newWordId = dict.put(newWord);
                    Integer synsetCode = Integer.parseInt(parts[1]);

                    store.addWordCode(newWordId, synsetCode);
                } else {
                    throw new RuntimeException("Unexpected file format. Each line should have code - word syntax");
                }
                line = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Set<SynsetNode> rootSynsets() {
        Set<SynsetNode> roots = new HashSet<SynsetNode>();
        for (SynsetNode sn : read()) {
            if (sn.getParents().isEmpty()) {
                roots.add(sn);
            }
        }
        return roots;
    }
}