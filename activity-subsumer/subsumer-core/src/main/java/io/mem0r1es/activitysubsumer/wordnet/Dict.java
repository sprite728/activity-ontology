package io.mem0r1es.activitysubsumer.wordnet;

/**
 * @author Ivan Gavrilović
 */
public abstract class Dict {
    protected int nextId = -1;

    protected char[][] wordsKeys;

    public static Dict getDict(int code){
        if (Integer.toString(code).startsWith("1")){
            return NounDict.getInstance();
        }
        else if (Integer.toString(code).startsWith("2")){
            return VerbDict.getInstance();
        }
        else {
            throw new RuntimeException("Unsupported synset code! Nouns and verbs start with 1 or 2");
        }
    }

    public int put(String s){
        char[] sChar = s.toCharArray();
        int pos = binarySearch(wordsKeys, sChar, 0, nextId);
        if (pos != -1) return pos;
        nextId++;

        wordsKeys[nextId] = sChar;
        return nextId;
    }

    public int get(String s){
        return binarySearch(wordsKeys, s.toCharArray(), 0, wordsKeys.length - 1);
    }

    public String get(int code){
        return new String(wordsKeys[code]);
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

    public static int binarySearch(char[][] a, char[] val, int low, int high){
        int start = low, end = high;
        while(start <= end){
            int mid = (start + end) >>> 1;

            int cmp = compareTo(a[mid], val);
            if (cmp == 0){
                return mid;
            }
            else if (cmp < 0) start = mid + 1;
            else end = mid - 1;
        }
        return -1;
    }
}
