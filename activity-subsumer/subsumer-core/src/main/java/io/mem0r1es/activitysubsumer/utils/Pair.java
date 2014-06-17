package io.mem0r1es.activitysubsumer.utils;

/**
 * @author Ivan Gavrilović
 */
public class Pair<K, V> {
    private K fst;
    private V snd;

    private Pair(K fst, V snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public static <K,V> Pair<K,V> get(K fst, V snd){
        return new Pair<K, V>(fst, snd);
    }

    public K getFst() {
        return fst;
    }

    public V getSnd() {
        return snd;
    }
}
