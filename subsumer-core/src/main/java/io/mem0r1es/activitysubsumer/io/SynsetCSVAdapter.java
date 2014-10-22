package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.utils.LineByLineSplit;
import io.mem0r1es.activitysubsumer.utils.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.NoSuchElementException;

/**
 * Reads the csv file containing {@link io.mem0r1es.activitysubsumer.synsets.SynsetNode}
 * as vertices. Direct edge from node A->B means that B is hyponym of A. For instance:
 * (pizza, pizza pie) -- hyponym --> (pizza made with a thick crust)
 *
 * @author Ivan Gavrilović
 */
public class SynsetCSVAdapter extends SynsetAdapter {

    private LineByLineSplit synsetReader;
    private LineByLineSplit childReader;
    private LineByLineSplit parentReader;

    /**
     * Creates new provider. All input streams should be sorted.
     *
     * @param synsetStream stream to synset code - word pairs
     * @param childStream  stream to node - child node pairs
     * @param parentStream stream to node - parent node pairs
     */
    public SynsetCSVAdapter(InputStream synsetStream,
                            InputStream childStream, InputStream parentStream) {
        this.synsetReader = new LineByLineSplit(synsetStream);
        this.childReader = new LineByLineSplit(childStream);
        this.parentReader = new LineByLineSplit(parentStream);
    }

    @Override
    public Pair<Integer, Integer> child() throws IOException {
        if (childReader.hasNext()) {
            String[] parts = childReader.next();
            if (parts.length == 2) {
                Integer synsetCode = Integer.parseInt(parts[0]);
                Integer otherSynsetCode = Integer.parseInt(parts[1]);

                return Pair.get(synsetCode, otherSynsetCode);
            } else {
                throw new IOException("Unexpected file format. Each line should have code - code syntax");
            }
        } else throw new NoSuchElementException();
    }

    @Override
    public Pair<Integer, Integer> parent() throws IOException {
        if (parentReader.hasNext()) {
            String[] parts = parentReader.next();
            if (parts.length == 2) {
                Integer synsetCode = Integer.parseInt(parts[0]);
                Integer otherSynsetCode = Integer.parseInt(parts[1]);

                return Pair.get(synsetCode, otherSynsetCode);
            } else {
                throw new IOException("Unexpected file format. Each line should have code - code syntax");
            }
        } else throw new NoSuchElementException();
    }

    @Override
    public Pair<Integer, String> synset() throws IOException {
        if (synsetReader.hasNext()) {
            String[] parts = synsetReader.next();
            if (parts.length == 2) {
                Integer synsetCode = Integer.parseInt(parts[0]);
                String newWord = URLDecoder.decode(parts[1], "UTF-8");

                return Pair.get(synsetCode, newWord);
            } else {
                throw new IOException("Unexpected file format. Each line should have code - word syntax");
            }
        } else throw new NoSuchElementException();
    }

    @Override
    public boolean hasChild() {
        return childReader.hasNext();
    }

    @Override
    public boolean hasParent() {
        return parentReader.hasNext();
    }

    @Override
    public boolean hasSynset() {
        return synsetReader.hasNext();
    }

    @Override
    public void closeChild() {
        childReader.close();
    }

    @Override
    public void closeParent() {
        parentReader.close();
    }

    @Override
    public void closeSynset() {
        synsetReader.close();
    }
}
