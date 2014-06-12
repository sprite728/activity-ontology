//package io.mem0r1es.activitysubsumer.wordnet;
//
//import io.mem0r1es.activitysubsumer.io.SynsetProvider;
//
///**
// * Verb synsets pool singleton object.
// * @author Ivan Gavrilović
// */
//public class VerbSynsetPool extends SynsetPool {
//    protected static VerbSynsetPool ourInstance = null;
//
//    public VerbSynsetPool(int capacity, SynsetProvider provider) {
//        super(capacity, provider);
//        // TODO set instance in constructor
//    }
//
//    public static void setInstance(VerbSynsetPool pool) {
//        if (isSet()) throw new RuntimeException("SynsetPool is already initialized");
//        ourInstance = pool;
//    }
//
//    public static VerbSynsetPool getInstance() {
//        if (!isSet()) throw new RuntimeException("SynsetPool is not initialized");
//        return ourInstance;
//    }
//
//    public static boolean isSet() {
//        return ourInstance != null;
//    }
//}
