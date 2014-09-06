package io.mem0r1es.activitysubsumer.synsets;

import io.mem0r1es.activitysubsumer.utils.SubConf;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Class holding the dictionary for nouns and dictionary for verbs. All nouns are unique, and all verbs are unique,
 * but there could be a word that is in both noun dictionary, and in verbs dictionary.
 *
 * @author Ivan Gavrilović
 */
public enum Dict {
    NOUNS(SubConf.CONFIG.getUniqueNouns()), VERBS(SubConf.CONFIG.getUniqueVerbs());

    protected int nextId = -1;

    protected char[][] wordValues;

    Dict(int size) {
        wordValues = new char[size][];
    }

    public static Dict getDict(int code) {
        if (Integer.toString(code).startsWith("1")) {
            return NOUNS;
        } else if (Integer.toString(code).startsWith("2")) {
            return VERBS;
        } else {
            throw new IllegalArgumentException("Unsupported synset code! Nouns and verbs start with 1 or 2");
        }
    }

    public int put(String s) {
        char[] sChar = s.toCharArray();
        int pos = binarySearch(wordValues, sChar, 0, nextId);
        if (pos >= 0) return pos;
        nextId++;

        wordValues[nextId] = sChar;
        return nextId;
    }

    public int get(String s) {
        return binarySearch(wordValues, s.toCharArray(), 0, wordValues.length - 1);
    }

    public String get(int code) {
        return new String(wordValues[code]);
    }

    public static int compareTo(char[] fst, char[] snd) {
        int len1 = fst.length;
        int len2 = snd.length;
        int lim = Math.min(len1, len2);

        int k = 0;
        while (k < lim) {
            char c1 = fst[k];
            char c2 = snd[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return len1 - len2;
    }

    public Collection<String> search(String prefix) {
        int pos = binarySearch(wordValues, prefix.toCharArray(), 0, wordValues.length - 1);
        pos = pos < 0 ? ((-1) * pos + 1) : pos;
        char[] p = prefix.toCharArray();
        Collection<String> matches = new LinkedList<String>();
        while (pos < wordValues.length && isPrefix(wordValues[pos], p)){
            matches.add(new String(wordValues[pos++]));
        }
        return matches;
    }

    private boolean isPrefix(char[] a, char[] prefix) {
        int aLen = a.length;
        int prefixLen = prefix.length;
        if (aLen < prefixLen) return false;

        for (int i = 0; i < prefix.length; i++) {
            if (a[i] != prefix[i]) return false;
        }
        return true;
    }

    public static int binarySearch(char[][] a, char[] val, int low, int high) {
        int start = low, end = high;
        while (start <= end) {
            int mid = (start + end) >>> 1;

            int cmp = compareTo(a[mid], val);
            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) start = mid + 1;
            else end = mid - 1;
        }
        return -(start + 1);
    }

    public boolean isEmpty(){
        return nextId == -1;
    }
}
