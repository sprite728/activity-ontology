package io.mem0r1es.activitysubsumer.utils;

/**
 * Configuration of the project
 * @author Ivan Gavrilović
 */
public enum SubConf {
    CONFIG;

    /**
     * Files containing the sorted synsets, parents (each line is node -> node's parent),
     * and children (each line is node -> node's child)
     */
    private static final String NOUNS_PARENTS = "subsumer-test/src/test/resources/nouns_parents_sorted";
    private static final String NOUNS_CHILDREN = "subsumer-test/src/test/resources/nouns_children_sorted";
    private static final String NOUNS_SYNSET = "subsumer-test/src/test/resources/nouns_syns_sorted";
    private static final String NOUNS_WORDS = "subsumer-test/src/test/resources/nouns_words_sorted";
    private static final String VERBS_PARENTS = "subsumer-test/src/test/resources/verbs_parents_sorted";
    private static final String VERBS_CHILDREN = "subsumer-test/src/test/resources/verbs_children_sorted";
    private static final String VERBS_SYNSET = "subsumer-test/src/test/resources/verbs_syns_sorted";
    private static final String VERBS_WORDS = "subsumer-test/src/test/resources/verbs_words_sorted";

    private static final String ACTIVITIES_FILE = "subsumer-test/src/test/resources/user.activities";
    private static final String ACTIVITIES_DEFAULT = "subsumer-test/src/test/resources/default.activities";
    private static final String TMP_FILE_IN = "subsumer-test/src/test/resources/tmp_in";

    private static final String CATEGORIES_CSV = "subsumer-test/src/test/resources/categories.csv";
    private static final String DB_NOUNS_IN = "subsumer-test/src/test/resources/data.noun";
    private static final String DB_VERB_IN = "subsumer-test/src/test/resources/data.verb";

    private static final String PREDICATE_HYPONYM = "hyponym";
    private static final String PREDICATE_SYNSET = "synset_member";

    private static final int UNIQUE_NOUNS = 116197;
    private static final int UNIQUE_VERBS = 11340;
    private static final int NOUNS_WORDS_IN_SYNS = 146547;
    private static final int NOUNS_PARENT_CHLD_RELS = 84505;
    private static final int VERBS_WORDS_IN_SYNS = 25061;
    private static final int VERBS_PARENT_CHLD_RELS = 13256;
    private static final int NUM_THREADS = 10;

    /**
     * Actual values of the config file
     */
    private String nounsParents = NOUNS_PARENTS;
    private String nounsChildren = NOUNS_CHILDREN;
    private String nounsSynset = NOUNS_SYNSET;
    private String nounsWords = NOUNS_WORDS;
    private String verbsParents = VERBS_PARENTS;
    private String verbsChildren = VERBS_CHILDREN;
    private String verbsSynset = VERBS_SYNSET;
    private String verbsWords = VERBS_WORDS;

    private String activitiesFile = ACTIVITIES_FILE;
    private String activitiesDefault = ACTIVITIES_DEFAULT;
    private String tmpFileIn = TMP_FILE_IN;

    private String categoriesCsv = CATEGORIES_CSV;
    private String dbNounsIn = DB_NOUNS_IN;
    private String dbVerbsIn = DB_VERB_IN;

    private String predicateHyponym = PREDICATE_HYPONYM;
    private String predicateSynset = PREDICATE_SYNSET;

    private int uniqueNouns = UNIQUE_NOUNS;
    private int uniqueVerbs = UNIQUE_VERBS;
    private int nounsWordsInSyns = NOUNS_WORDS_IN_SYNS;
    private int nounsParentChldRels = NOUNS_PARENT_CHLD_RELS;
    private int verbsWordsInSyns = VERBS_WORDS_IN_SYNS;
    private int verbsParentChldRels = VERBS_PARENT_CHLD_RELS;
    private int numThreads = NUM_THREADS;

    public String getNounsParents() {
        return nounsParents;
    }

    public void setNounsParents(String nounsParents) {
        this.nounsParents = nounsParents;
    }

    public String getNounsChildren() {
        return nounsChildren;
    }

    public void setNounsChildren(String nounsChildren) {
        this.nounsChildren = nounsChildren;
    }

    public String getNounsSynset() {
        return nounsSynset;
    }

    public void setNounsSynset(String nounsSynset) {
        this.nounsSynset = nounsSynset;
    }

    public String getNounsWords() {
        return nounsWords;
    }

    public void setNounsWords(String nounsWords) {
        this.nounsWords = nounsWords;
    }

    public String getVerbsParents() {
        return verbsParents;
    }

    public void setVerbsParents(String verbsParents) {
        this.verbsParents = verbsParents;
    }

    public String getVerbsChildren() {
        return verbsChildren;
    }

    public void setVerbsChildren(String verbsChildren) {
        this.verbsChildren = verbsChildren;
    }

    public String getVerbsSynset() {
        return verbsSynset;
    }

    public void setVerbsSynset(String verbsSynset) {
        this.verbsSynset = verbsSynset;
    }

    public String getVerbsWords() {
        return verbsWords;
    }

    public void setVerbsWords(String verbsWords) {
        this.verbsWords = verbsWords;
    }

    public String getActivitiesFile() {
        return activitiesFile;
    }

    public void setActivitiesFile(String activitiesFile) {
        this.activitiesFile = activitiesFile;
    }

    public String getActivitiesDefault() {
        return activitiesDefault;
    }

    public void setActivitiesDefault(String activitiesDefault) {
        this.activitiesDefault = activitiesDefault;
    }

    public String getTmpFileIn() {
        return tmpFileIn;
    }

    public void setTmpFileIn(String tmpFileIn) {
        this.tmpFileIn = tmpFileIn;
    }

    public String getPredicateHyponym() {
        return predicateHyponym;
    }

    public void setPredicateHyponym(String predicateHyponym) {
        this.predicateHyponym = predicateHyponym;
    }

    public String getPredicateSynset() {
        return predicateSynset;
    }

    public void setPredicateSynset(String predicateSynset) {
        this.predicateSynset = predicateSynset;
    }

    public int getUniqueNouns() {
        return uniqueNouns;
    }

    public void setUniqueNouns(int uniqueNouns) {
        this.uniqueNouns = uniqueNouns;
    }

    public int getUniqueVerbs() {
        return uniqueVerbs;
    }

    public void setUniqueVerbs(int uniqueVerbs) {
        this.uniqueVerbs = uniqueVerbs;
    }

    public int getNounsWordsInSyns() {
        return nounsWordsInSyns;
    }

    public void setNounsWordsInSyns(int nounsWordsInSyns) {
        this.nounsWordsInSyns = nounsWordsInSyns;
    }

    public int getNounsParentChldRels() {
        return nounsParentChldRels;
    }

    public void setNounsParentChldRels(int nounsParentChldRels) {
        this.nounsParentChldRels = nounsParentChldRels;
    }

    public int getVerbsWordsInSyns() {
        return verbsWordsInSyns;
    }

    public void setVerbsWordsInSyns(int verbsWordsInSyns) {
        this.verbsWordsInSyns = verbsWordsInSyns;
    }

    public int getVerbsParentChldRels() {
        return verbsParentChldRels;
    }

    public void setVerbsParentChldRels(int verbsParentChldRels) {
        this.verbsParentChldRels = verbsParentChldRels;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public String getCategoriesCsv() {
        return categoriesCsv;
    }

    public void setCategoriesCsv(String categoriesCsv) {
        this.categoriesCsv = categoriesCsv;
    }

    public String getDbNounsIn() {
        return dbNounsIn;
    }

    public void setDbNounsIn(String dbNounsIn) {
        this.dbNounsIn = dbNounsIn;
    }

    public String getDbVerbsIn() {
        return dbVerbsIn;
    }

    public void setDbVerbsIn(String dbVerbsIn) {
        this.dbVerbsIn = dbVerbsIn;
    }
}
