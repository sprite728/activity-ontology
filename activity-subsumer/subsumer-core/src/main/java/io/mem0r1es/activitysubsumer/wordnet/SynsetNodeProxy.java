package io.mem0r1es.activitysubsumer.wordnet;

import java.util.Set;

/**
 * Delays the actual creation of the {@link SynsetNodeImpl} until it is needed
 *
 * @author Ivan Gavrilović
 */
public class SynsetNodeProxy extends SynsetNode {

    String proxyCode;
    SynsetNodeImpl content;

    public SynsetNodeProxy(String proxyCode) {
        this.proxyCode = proxyCode;
        content = null;
    }

    public SynsetNodeProxy(String proxyCode, SynsetNodeImpl content) {
        this.proxyCode = proxyCode;
        this.content = content;
    }

    @Override
    public void addWords(String... words) {
        init();
        content.addWords(words);
    }


    @Override
    public boolean equals(Object o) {
        init();
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SynsetNodeProxy that = (SynsetNodeProxy) o;
        that.init();
        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (proxyCode != null ? !proxyCode.equals(that.proxyCode) : that.proxyCode != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = proxyCode != null ? proxyCode.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public boolean contains(String word) {
        init();
        return content.contains(word);
    }

    @Override
    public boolean containsAny(Set<String> words) {
        init();
        return content.containsAny(words);
    }

    @Override
    public String getCode() {
        return proxyCode;
    }

    @Override
    public Set<String> getSynset() {
        init();
        return content.getSynset();
    }

    @Override
    public String toString() {
        init();
        return content.toString();
    }

    @Override
    public void addParent(SynsetNode parent) {
        init();
        content.addParent(parent);
    }

    @Override
    public void addChild(SynsetNode child) {
        init();
        content.addChild(child);
    }

    @Override
    public Set<SynsetNode> getParents() {
        init();
        return content.getParents();
    }

    @Override
    public Set<SynsetNode> getChildren() {
        init();
        return content.getChildren();
    }

    /**
     * Instantiates the actual {@link SynsetNodeImpl}
     */
    private void init() {
        if (content == null) {
            SynsetPool pool;
            if (proxyCode.startsWith("1"))
                 pool = NounSynsetPool.getInstance();
            else if (proxyCode.startsWith("2"))
                pool = VerbSynsetPool.getInstance();
            else throw new RuntimeException("Unknown synset code: "+proxyCode);

            content = pool.get(Integer.parseInt(proxyCode));
        }
    }
}
