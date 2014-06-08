package io.mem0r1es.activitysubsumer.utils;

/**
 * @author Ivan Gavrilović
 */
public class Cons {
    public static final String NOUN_ROOTS = "src/test/resources/noun_roots";

    public static final String VERB_ROOTS = "src/test/resources/verb_roots";

    public static final String ENTRY_SEPARATOR = "|";
    public static final String ENTRY_SEPARATOR_REG = "[|]";
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String CLUSTER_SEPARATOR = "--------";
    public static final String CATEGORY_SEPARATOR = "********";

    public static final String NOUNS_HYPONYM = "src/test/resources/word_net_nouns_hyponym";
    public static final String NOUNS_SYNSET = "src/test/resources/word_net_nouns_synset";
    public static final String NOUNS = "src/test/resources/word_net_nouns";
    public static final String VERBS_HYPONYM = "src/test/resources/word_net_verbs_hyponym";
    public static final String VERBS_SYNSET = "src/test/resources/word_net_verbs_synset";
    public static final String VERBS = "src/test/resources/word_net_verbs";

    public static final String ACTIVITIES_FILE = "src/test/resources/user.activities";
    public static final String ACTIVITIES_DEFAULT = "src/test/resources/default.activities";

    public static final String CATEGORIES_CSV = "src/test/resources/categories.csv";

    public static final String DB_NOUN_IN = "src/test/resources/data.noun";
    public static final String DB_VERB_IN = "src/test/resources/data.verb";

    public static final String TMP_FILE_IN = "src/test/resources/tmp_in";

    public static final String PREDICATE_HYPONYM = "hyponym";
    public static final String PREDICATE_SYNSET = "synset_member";

    public static final int NUM_THREADS = 10;
}
