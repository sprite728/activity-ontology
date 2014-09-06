package io.mem0r1es.activitysubsumer.synsets;

import io.mem0r1es.activitysubsumer.io.DictAdapter;
import io.mem0r1es.activitysubsumer.io.SynsetAdapter;
import io.mem0r1es.activitysubsumer.utils.Pair;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class Synsets {

    SynsetAdapter adapter;

    /**
     * Holding all synset data
     */
    private SynsetStore store;

    /**
     * Creates new provider. All input streams should be sorted.
     * </p>
     * This also initializes noun/verb dictionary, that can be searched.
     *
     * @param adapter synset adapter reading synset data
     * @param store   store to be populated
     */
    public Synsets(SynsetAdapter adapter, SynsetStore store) {
        this.store = store;
        this.adapter = adapter;
    }

    public Set<SynsetNode> rootSynsets() {
        Set<SynsetNode> roots = new HashSet<SynsetNode>();
        for (SynsetNode sn : readAll()) {
            if (sn.getParents().isEmpty()) {
                roots.add(sn);
            }
        }
        return roots;
    }

    private Set<SynsetNode> readAll() {
        try {
            // this populates the dict
            //readWords();
            readChildren();
            readParents();
            return readSynsets();
        } catch (IOException ioe) {

            return null;
        }
    }

    private Set<SynsetNode> readSynsets() throws IOException {
        Set<SynsetNode> proxies = new HashSet<SynsetNode>();
        while (adapter.hasSynset()) {
            Pair<Integer, String> data = adapter.synset();

            proxies.add(new SynsetNodeProxy(data.getFst()));

            int newWordId = store.stringToId(data.getSnd());
            store.addCodeWord(data.getFst(), newWordId);
        }
        adapter.closeSynset();
        return proxies;
    }

    private void readParents() throws IOException {
        while (adapter.hasParent()) {
            Pair<Integer, Integer> data = adapter.parent();
            store.addParent(data.getFst(), data.getSnd());
        }
        adapter.closeParent();
    }

    private void readChildren() throws IOException {
        while (adapter.hasChild()) {
            Pair<Integer, Integer> data = adapter.child();
            store.addChild(data.getFst(), data.getSnd());
        }
        adapter.closeChild();
    }

    /**
     * Read all words and save them to the dictionary.
     *
     * @param dictAdapter dictionary adapter to read from
     * @param dictStore store to save the words (either noun or verb store)
     * @throws IOException
     */
    public static void readWords(DictAdapter dictAdapter, SynsetStore dictStore) throws IOException {
        while (dictAdapter.hasWord()) {
            Pair<String, Integer> data = dictAdapter.word();

            dictStore.addWordCode(data.getFst(), data.getSnd());
        }
        dictAdapter.closeWord();
    }
}