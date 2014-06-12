//package io.mem0r1es.activitysubsumer.wordnet;
//
//import io.mem0r1es.activitysubsumer.io.SynsetProvider;
//import io.mem0r1es.activitysubsumer.utils.SimpleLRUCache;
//
//import java.util.Set;
//
///**
// * @author Ivan Gavrilović
// */
//public abstract class SynsetPool {
//    protected SimpleLRUCache<Integer, SynsetNodeImpl> codeToSynset;
//
//    protected SynsetProvider provider;
//
//    public SynsetPool(int capacity, SynsetProvider provider) {
//        this.provider = provider;
//        codeToSynset = new SimpleLRUCache<Integer, SynsetNodeImpl>(capacity);
//    }
//
//    public SynsetNodeImpl get(int code) {
//        if (codeToSynset.containsKey(code)) return codeToSynset.get(code);
//
//        SynsetNodeImpl node = provider.readWithCode(code);
//        codeToSynset.put(code, node);
//
//        return node;
//    }
//
//    public Set<SynsetNode> getRoots() {
//        return provider.rootSynsets();
//    }
//}
