package io.mem0r1es.activitysubsumer.wordnet;

import java.util.HashSet;
import java.util.Set;

/**
 * Queries {@link io.mem0r1es.activitysubsumer.wordnet.SynsetStore} for nouns or verbs for all of the methods.
 *
 * This is lightweight class containing not data, but the synset code. Code itself is used to query corresponding
 * {@link io.mem0r1es.activitysubsumer.wordnet.SynsetStore} and {@link io.mem0r1es.activitysubsumer.wordnet.Dict}
 * structures.
 *
 * @author Ivan Gavrilović
 */
public class SynsetNodeProxy extends SynsetNode {

    /**
     * Code of this synset
     */
    int proxyCode;

    /**
     * Synset store to query for data
     */
    SynsetStore store;

    public SynsetNodeProxy(int proxyCode) {
        this.proxyCode = proxyCode;

        if (Integer.toString(proxyCode).startsWith("1")){
            store = SynsetStore.NOUNS;
        }
        else if (Integer.toString(proxyCode).startsWith("2")){
            store = SynsetStore.VERBS;
        }
        else {
            throw new UnsupportedOperationException("Unsupported synset code! Nouns and verbs start with 1 or 2");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SynsetNodeProxy that = (SynsetNodeProxy) o;

        if (proxyCode != that.proxyCode) return false;
        if (!store.equals(that.store)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = proxyCode;
        result = 31 * result + store.hashCode();
        return result;
    }

    @Override
    public boolean contains(String word) {
        return store.getSynset(proxyCode).contains(Dict.getDict(proxyCode).get(word));
    }

    @Override
    public int getCode() {
        return proxyCode;
    }

    @Override
    public Set<String> getSynset() {
        HashSet<String> syns = new HashSet<String>();
        for(int i:store.getSynset(proxyCode)) syns.add(Dict.getDict(proxyCode).get(i));
        return syns;
    }

    @Override
    public String toString() {
        return Integer.toString(proxyCode);
    }

    @Override
    public void addParent(SynsetNode parent) {
       // nothing
    }

    @Override
    public void addChild(SynsetNode child) {
        // nothing
    }

    @Override
    public Set<SynsetNode> getParents() {
        Set<SynsetNode> parents = new HashSet<SynsetNode>();
        for (int i: store.getParents(proxyCode)){
            parents.add(new SynsetNodeProxy(i));
        }

        return parents;
    }

    @Override
    public Set<SynsetNode> getChildren() {
        Set<SynsetNode> children = new HashSet<SynsetNode>();
        for (int i: store.getChildren(proxyCode)){
            children.add(new SynsetNodeProxy(i));
        }

        return children;
    }
}
