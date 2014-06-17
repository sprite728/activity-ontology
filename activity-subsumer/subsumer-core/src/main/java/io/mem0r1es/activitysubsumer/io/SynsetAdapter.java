package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.utils.Pair;
import io.mem0r1es.activitysubsumer.utils.SubsumerConfig;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Interface that should be implemented by classes that retrieve synsets (from DB, file, network etc.)
 *
 * @author Ivan Gavrilović
 */
public abstract class SynsetAdapter {
    public static SynsetAdapter defaultVerbs() throws IOException{
        return new SynsetCSVAdapter(new FileInputStream(SubsumerConfig.VERBS_SYNSET), new FileInputStream(SubsumerConfig.VERBS_WORDS),
                new FileInputStream(SubsumerConfig.VERBS_CHILDREN), new FileInputStream(SubsumerConfig.VERBS_PARENTS));
    }

    public static SynsetAdapter defaultNouns() throws IOException{
        return new SynsetCSVAdapter(new FileInputStream(SubsumerConfig.NOUNS_SYNSET), new FileInputStream(SubsumerConfig.NOUNS_WORDS),
                new FileInputStream(SubsumerConfig.NOUNS_CHILDREN), new FileInputStream(SubsumerConfig.NOUNS_PARENTS));
    }

    /**
     * Returns single node - node's child pair. Will be invoked repeatedly, until {@link this#hasChild()}
     * returns {@code true}
     *
     * @return node - child pair
     * @throws IOException
     */
    public abstract Pair<Integer, Integer> child() throws IOException;

    /**
     * If there are more elements to read should return {@code true}, {@code false} otherwise
     *
     * @return the status of the child data source
     */
    public abstract boolean hasChild();

    /**
     * Closes this stream. If this is not suitable for the implementation, just leave this method empty.
     */
    public abstract void closeChild();

    /**
     * NOTICE:
     * For all other methods, see {@link this#child}, {@link this#hasChild()} and {@link this#closeChild()}.
     * The meaning of all other is the same like for these three.
     */


    public abstract Pair<Integer, Integer> parent() throws IOException;

    public abstract boolean hasParent();

    public abstract void closeParent();

    public abstract Pair<String, Integer> word() throws IOException;

    public abstract boolean hasWord();

    public abstract void closeWord();

    public abstract Pair<Integer, String> synset() throws IOException;

    public abstract boolean hasSynset();

    public abstract void closeSynset();
}
