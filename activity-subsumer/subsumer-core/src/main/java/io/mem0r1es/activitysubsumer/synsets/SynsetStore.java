package io.mem0r1es.activitysubsumer.synsets;

import io.mem0r1es.activitysubsumer.utils.SubConf;

import java.util.HashSet;
import java.util.Set;

/**
 * Representing the relations between the synsets, child and parent relationship. Also, keeps track of which
 * synset code maps to which words, and which words map to synset codes.
 *
 * All data is sorted, and all queries are done using binary search.
 *
 * @author Ivan Gavrilović
 */
public enum SynsetStore {
    NOUNS(SubConf.CONFIG.getNounsWordsInSyns(), SubConf.CONFIG.getNounsParentChldRels(), Dict.NOUNS),
    VERBS(SubConf.CONFIG.getVerbsWordsInSyns(), SubConf.CONFIG.getVerbsParentChldRels(), Dict.VERBS);

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

    /**
     * Dictionary associated with this store
     */
    private Dict dict;


    SynsetStore(int numWords, int parChldRels, Dict dict) {
        synsetKeys = new int[numWords];
        synsetVals = new int[numWords];

        wordsKeys = new int[numWords];
        wordsVals = new int[numWords];

        parentsKeys = new int[parChldRels];
        parentVals = new int[parChldRels];

        childrenKeys = new int[parChldRels];
        childrenVals = new int[parChldRels];

        this.dict = dict;
    }

    /**
     * Get all words from the synset
     * @param code synset code
     * @return set of ids of words from the dictionary
     */
    public Set<Integer> getSynset(int code) {
        int pos = binSearch(synsetKeys, code);

        Set<Integer> synsetMembers = new HashSet<Integer>();
        if (pos == -1) return synsetMembers;
        while (pos < synsetKeys.length && synsetKeys[pos] == code) {
            synsetMembers.add(synsetVals[pos++]);
        }
        return synsetMembers;
    }

    /**
     * Get all synsets that contain the word
     * @param word word from the dictionary
     * @return set of synset codes containing the word
     */
    public Set<Integer> getSynsetsWithWord(String word){
        int w = dict.get(word);
        int pos = binSearch(wordsKeys, w);

        Set<Integer> synsets = new HashSet<Integer>();
        if (pos == -1) return synsets;
        while (pos < wordsKeys.length && wordsKeys[pos] == w) {
            int v = wordsVals[pos++];
            if (v != -1) synsets.add(v);
        }
        return synsets;
    }

    /**
     * Get all parent synsets of the node
     * @param code synset code
     * @return set of all parents of the specified node
     */
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

    /**
     * Get all children of the specified node
     * @param code synset code
     * @return set of all children of the specifed node
     */
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

    /**
     * For the word with the id from the corresponding dictionary, add the synset code in which it is contained.
     * @param w iword from the dictionary
     * @param code code of the synset that contains the word
     */
    public void addWordCode(String w, int code){
        int word = dict.put(w);

        if (wordsCnt > 0){
            if (word < wordsKeys[wordsCnt-1]){
                throw new RuntimeException("Add in ascending synset words order.");
            }
        }

        wordsKeys[wordsCnt] = word;
        wordsVals[wordsCnt++] = code;
    }

    /**
     * For the synset with the specified code, add the id of the word from the dictionary that the synset contains.
     * @param code synset code
     * @param word id of the word from the dictionary
     */
    public void addCodeWord(int code, int word) {
        if (synsetCnt > 0) {
            if (synsetKeys[synsetCnt-1] > code)
                throw new RuntimeException("Add in ascending synset codes order.");
        }

        synsetKeys[synsetCnt] = code;
        synsetVals[synsetCnt++] = word;
    }

    /**
     * For the synset code add parent synset node with the specifed code.
     * @param code synset code
     * @param parent node that is parent
     */
    public void addParent(int code, int parent) {
        if (parentCnt > 0) {
            if (parentsKeys[parentCnt-1] > code)
                throw new RuntimeException("Add in ascending synset codes order.");
        }

        parentsKeys[parentCnt] = code;
        parentVals[parentCnt++] = parent;
    }

    /**
     * For the synset with code add child synset node with the specified code.
     * @param code synset code
     * @param child node that is child
     */
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

    public String idToString(int wordId){
        return dict.get(wordId);
    }

    public int stringToId(String word){
        return dict.get(word);
    }
}
