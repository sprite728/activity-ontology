package io.mem0r1es.activitysubsumer.utils;

/**
 * @author Ivan Gavrilović
 */
public class Cons {
    public static final String ENTRY_SEPARATOR = "|";
    public static final String ENTRY_SEPARATOR_REG = "[|]";
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String CLUSTER_SEPARATOR = "--------";
    public static final String CATEGORY_SEPARATOR = "********";

    /**
     * Files containing the sorted synsets, parents (each line is node -> node's parent),
     * and children (each line is node -> node's child)
     */
    public static final String NOUNS_PARENTS = "subsumer-test/src/test/resources/nouns_parents_sorted";
    public static final String NOUNS_CHILDREN = "subsumer-test/src/test/resources/nouns_children_sorted";
    public static final String NOUNS_SYNSET = "subsumer-test/src/test/resources/nouns_syns_sorted";
    public static final String NOUNS_WORDS = "subsumer-test/src/test/resources/nouns_words_sorted";
    public static final String VERBS_PARENTS = "subsumer-test/src/test/resources/verbs_parents_sorted";
    public static final String VERBS_CHILDREN = "subsumer-test/src/test/resources/verbs_children_sorted";
    public static final String VERBS_SYNSET = "subsumer-test/src/test/resources/verbs_syns_sorted";
    public static final String VERBS_WORDS = "subsumer-test/src/test/resources/verbs_words_sorted";

    public static final String ACTIVITIES_FILE = "subsumer-test/src/test/resources/user.activities";
    public static final String ACTIVITIES_DEFAULT = "subsumer-test/src/test/resources/default.activities";

    public static final String CATEGORIES_CSV = "subsumer-test/src/test/resources/categories.csv";

    public static final String DB_NOUN_IN = "subsumer-test/src/test/resources/data.noun";
    public static final String DB_VERB_IN = "subsumer-test/src/test/resources/data.verb";

    public static final String TMP_FILE_IN = "subsumer-test/src/test/resources/tmp_in";

    public static final String PREDICATE_HYPONYM = "hyponym";
    public static final String PREDICATE_SYNSET = "synset_member";

    public static final int UNIQUE_NOUNS = 116197;
    public static final int UNIQUE_VERBS = 11340;
    public static final int NOUNS_WORDS_IN_SYNS = 146547;
    public static final int NOUNS_PARENT_CHLD_RELS = 84505;
    public static final int VERBS_WORDS_IN_SYNS = 25061;
    public static final int VERBS_PARENT_CHLD_RELS = 13256;


    public static final int NUM_THREADS = 10;
}
