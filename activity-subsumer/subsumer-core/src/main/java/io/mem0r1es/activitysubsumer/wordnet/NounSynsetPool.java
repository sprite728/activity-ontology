//package io.mem0r1es.activitysubsumer.wordnet;
//
//import io.mem0r1es.activitysubsumer.io.SynsetProvider;
//
///**
// * Noun synsets pool singleton object.
// *
// * @author Ivan Gavrilović
// */
//public class NounSynsetPool extends SynsetPool {
//    protected static NounSynsetPool ourInstance = null;
//
//    public NounSynsetPool(int capacity, SynsetProvider provider) {
//        super(capacity, provider);
//    }
//
//    public static void setInstance(NounSynsetPool pool) {
//        if (isSet()) throw new RuntimeException("SynsetPool is already initialized");
//        ourInstance = pool;
//    }
//
//    public static NounSynsetPool getInstance() {
//        if (!isSet()) throw new RuntimeException("SynsetPool is not initialized");
//        return ourInstance;
//    }
//
//    public static boolean isSet() {
//        return ourInstance != null;
//    }
//}
