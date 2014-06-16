package io.mem0r1es.activitysubsumer.wordnet;

import io.mem0r1es.activitysubsumer.utils.SubsumerConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public enum SynsetStore {
    NOUNS(SubsumerConfig.NOUNS_WORDS_IN_SYNS, SubsumerConfig.NOUNS_PARENT_CHLD_RELS),
    VERBS(SubsumerConfig.VERBS_WORDS_IN_SYNS, SubsumerConfig.VERBS_PARENT_CHLD_RELS);

    /**
     * Keys contain synset codes, values words
     */
    private int synsetCnt = 0;
    private int synsetKeys[];
    private int synsetVals[];

    /**
     * Keys contain synset words, and values are synset codes
     */
    private int wordsCnt = 0;
    private int wordsKeys[];
    private int wordsVals[];

    /**
     * Mapping synset code to children and parents
     */
    private int parentCnt = 0;
    private int parentsKeys[];
    private int parentVals[];

    private int childrenCnt = 0;
    private int childrenKeys[];
    private int childrenVals[];


    SynsetStore(int numWords, int parChldRels) {
        synsetKeys = new int[numWords];
        synsetVals = new int[numWords];

        wordsKeys = new int[numWords];
        wordsVals = new int[numWords];

        parentsKeys = new int[parChldRels];
        parentVals = new int[parChldRels];

        childrenKeys = new int[parChldRels];
        childrenVals = new int[parChldRels];
    }

    public Set<Integer> getSynset(int code) {
        int pos = binSearch(synsetKeys, code);

        Set<Integer> synsetMembers = new HashSet<Integer>();
        if (pos == -1) return synsetMembers;
        while (pos < synsetKeys.length && synsetKeys[pos] == code) {
            synsetMembers.add(synsetVals[pos++]);
        }
        return synsetMembers;
    }

    public Set<Integer> getSynsetsWithWord(int w){
        int pos = binSearch(wordsKeys, w);

        Set<Integer> synsets = new HashSet<Integer>();
        if (pos == -1) return synsets;
        while (pos < wordsKeys.length && wordsKeys[pos] == w) {
            int v = wordsVals[pos++];
            if (v != -1) synsets.add(v);
        }
        return synsets;
    }

    public Set<Integer> getParents(int code) {
        int pos = binSearch(parentsKeys, code);

        Set<Integer> parents = new HashSet<Integer>();
        if (pos == -1) return parents;
        while (pos < parentsKeys.length && parentsKeys[pos] == code) {
            int v = parentVals[pos++];
            if (v != -1) parents.add(v);
        }
        return parents;
    }

    public Set<Integer> getChildren(int code) {
        int pos = binSearch(childrenKeys, code);

        Set<Integer> children = new HashSet<Integer>();
        if (pos == -1) return children;
        while (pos < childrenKeys.length && childrenKeys[pos] == code) {
            int v = childrenVals[pos++];
            if (v != -1) children.add(v);
        }
        return children;
    }

    public void addWordCode(int word, int code){
        if (wordsCnt > 0){
            if (word < wordsKeys[wordsCnt-1]){
                throw new RuntimeException("Add in ascending synset words order.");
            }
        }

        wordsKeys[wordsCnt] = word;
        wordsVals[wordsCnt++] = code;
    }

    public void addCodeWord(int code, int word) {
        if (synsetCnt > 0) {
            if (synsetKeys[synsetCnt-1] > code)
                throw new RuntimeException("Add in ascending synset codes order.");
        }

        synsetKeys[synsetCnt] = code;
        synsetVals[synsetCnt++] = word;
    }

    public void addParent(int code, int parent) {
        if (parentCnt > 0) {
            if (parentsKeys[parentCnt-1] > code)
                throw new RuntimeException("Add in ascending synset codes order.");
        }

        parentsKeys[parentCnt] = code;
        parentVals[parentCnt++] = parent;
    }

    public void addChild(int code, int child) {
        if (childrenCnt > 0) {
            if (childrenKeys[childrenCnt-1] > code)
                throw new RuntimeException("Add in ascending synset codes order.");
        }

        childrenKeys[childrenCnt] = code;
        childrenVals[childrenCnt++] = child;
    }

    /**
     * Binary search of sorted array that can contain duplicate entries.
     *
     * @param a   array
     * @param val value
     * @return the first index where {@code val} occurs
     */
    public static int binSearch(int[] a, int val) {
        int start = 0, end = a.length - 1;
        while (start <= end) {
            int mid = (start + end) / 2;
            if (a[mid] == val) {
                // there can be duplicates
                while (mid > 0 && a[mid - 1] == val) mid--;
                return mid;
            } else if (a[mid] < val)
                start = mid + 1;
            else
                end = mid - 1;
        }
        return -1;
    }
}
