package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.utils.SubsumerLogger;
import io.mem0r1es.activitysubsumer.wordnet.Dict;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;
import io.mem0r1es.activitysubsumer.wordnet.SynsetNodeProxy;
import io.mem0r1es.activitysubsumer.wordnet.SynsetStore;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
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
    private static final Logger logger = SubsumerLogger.get(SynsetCSVProvider.class.getCanonicalName());

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
     * Creates new provider. All input streams should be sorted.
     *
     * @param synsetStream stream to synset code - word pairs
     * @param wordStream   stream to word - synset code pairs
     * @param childStream  stream to node - child node pairs
     * @param parentStream stream to node - parent node pairs
     * @param store        store to be populated
     * @param dict         dictionary to be populated
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
    private void readHyponyms(boolean readingChildren) throws IOException {
        LineByLineSplit lbls = null;
        try {
            lbls = new LineByLineSplit(readingChildren ? childStream : parentStream);
            while (lbls.hasNext()) {
                String[] parts = lbls.next();
                if (parts.length == 1) {
                    if (readingChildren) store.addChild(Integer.parseInt(parts[0]), -1);
                    else store.addParent(Integer.parseInt(parts[0]), -1);

                } else if (parts.length == 2) {
                    int fstCode = Integer.parseInt(parts[0]);
                    int sndCode = Integer.parseInt(parts[1]);
                    if (readingChildren) store.addChild(fstCode, sndCode);
                    else store.addParent(fstCode, sndCode);

                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new IOException("Malformed input.");
        } finally {
            if (lbls != null) lbls.close();
        }
    }

    private Set<SynsetNode> readAll() {
        try {
            // this populates the dict
            readWords();
            readHyponyms(false);
            readHyponyms(true);
            return readSynsets();
        } catch (IOException ioe) {

            return null;
        }
    }

    /**
     * Parse the synsets file and make mapping from synset codes to words
     */
    private Set<SynsetNode> readSynsets() throws IOException {
        Set<SynsetNode> proxies = new HashSet<SynsetNode>();
        LineByLineSplit lbls = null;
        try {
            lbls = new LineByLineSplit(synsetStream);
            while (lbls.hasNext()) {
                String[] parts = lbls.next();
                if (parts.length == 2) {
                    Integer synsetCode = Integer.parseInt(parts[0]);

                    proxies.add(new SynsetNodeProxy(synsetCode));

                    String newWord = URLDecoder.decode(parts[1], "UTF-8");
                    int newWordId = dict.get(newWord);

                    store.addCodeWord(synsetCode, newWordId);
                } else {
                    throw new IOException("Unexpected file format. Each line should have code - word syntax");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new IOException("Malformed input.");
        } finally {
            if (lbls != null) lbls.close();
        }
        return proxies;
    }

    private void readWords() throws IOException {
        LineByLineSplit lbls = null;
        try {
            lbls = new LineByLineSplit(wordStream);
            while (lbls.hasNext()) {
                String[] parts = lbls.next();
                if (parts.length == 2) {
                    String newWord = URLDecoder.decode(parts[0], "UTF-8");

                    int newWordId = dict.put(newWord);
                    Integer synsetCode = Integer.parseInt(parts[1]);

                    store.addWordCode(newWordId, synsetCode);
                } else {
                    throw new IOException("Unexpected file format. Each line should have code - word syntax");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new IOException("Malformed input.");
        } finally {
            if (lbls != null) lbls.close();
        }
    }

    @Override
    public Set<SynsetNode> rootSynsets() {
        Set<SynsetNode> roots = new HashSet<SynsetNode>();
        for (SynsetNode sn : readAll()) {
            if (sn.getParents().isEmpty()) {
                roots.add(sn);
            }
        }
        return roots;
    }

    private static class LineByLineSplit {
        private BufferedReader reader;
        private String line;

        private LineByLineSplit(InputStream stream) {
            this.reader = new BufferedReader(new InputStreamReader(stream));
            try {
                line = reader.readLine();
            } catch (Exception e) {
                line = null;
            }
        }

        boolean hasNext() {
            return line != null;
        }

        String[] next() {
            String[] parts = line.split(" ");
            try {
                line = reader.readLine();
            } catch (Exception e) {
                logger.error(e.getMessage());
                line = null;
            }
            return parts;
        }

        void close() {
            try {
                reader.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}