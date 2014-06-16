package io.mem0r1es.activitysubsumer.classifier;

import io.mem0r1es.activitysubsumer.io.CategoriesProvider;
import io.mem0r1es.activitysubsumer.utils.BFSHierarchicalNode;
import io.mem0r1es.activitysubsumer.utils.HierarchicalStructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents the category hierarchy and allows the query operations on it.
 *
 * @author Ivan Gavrilović
 */
public class CategoryHierarchy {
    private Map<String, Category> categories = null;

    private static CategoryHierarchy instance = null;

    /**
     * Creates new {@link io.mem0r1es.activitysubsumer.classifier.CategoryHierarchy}
     * @param provider {@link io.mem0r1es.activitysubsumer.io.CategoriesProvider} that is used for reading the data
     */
    public CategoryHierarchy(CategoriesProvider provider){

        Set<Category> readCategories = provider.read();
        categories = new HashMap<String, Category>();
        for (Category c : readCategories) categories.put(c.name, c);

        instance = this;
    }

    /**
     * Gets the previously created instance of {@link io.mem0r1es.activitysubsumer.classifier.CategoryHierarchy}
     *
     * @return the instance
     * @throws IllegalStateException if constructor has not been invoked previously
     */
    public static CategoryHierarchy get() throws IllegalStateException {
        if (instance == null) throw new IllegalStateException("Instantiate first!");
        return instance;
    }

    public static boolean isSet(){
        return instance != null;
    }

    /**
     * Get all categories that are more general than the specified one
     *
     * @param s category for which we are looking more general ones
     * @return all parent categories, including the specified one
     */
    public Set<String> getUp(String s) {
        Set<String> resultSet = new HashSet<String>();
        resultSet.add(s);
        Set<Category> edges = categories.get(s).getParents();
        while (!edges.isEmpty()) {
            Set<Category> nextEdgs = new HashSet<Category>();
            for (Category nextCat : edges) {
                resultSet.add(nextCat.name);
                nextEdgs.addAll(categories.get(nextCat.name).getParents());
            }

            edges = new HashSet<Category>(nextEdgs);
        }
        return resultSet;
    }

    /**
     * Get all categories that are more specific than the specified one
     *
     * @param s category name
     * @return all children categories, including the specified one
     */
    public Set<String> getDown(String s) {
        Set<String> resultSet = new HashSet<String>();
        resultSet.add(s);
        BFSHierarchicalNode<Category> bfs = new BFSHierarchicalNode<Category>(categories.get(s));
        while (bfs.hasNext()) resultSet.add(bfs.next().name);

        return resultSet;
    }

    /**
     * Get all categories that are above, and that are below the specified one in the hierarchy
     * @param s category name
     * @return all parent and children categories, including the specified one
     */
    public Set<String> getRelated(String s){
        Set<String> res = getUp(s); res.addAll(getDown(s));
        return res;
    }


    /**
     * Represents the category node in the category tree.
     */
    public static class Category extends HierarchicalStructure<Category> {
        private Set<Category> children;
        private Set<Category> parents;

        private String name;

        public Category(String name) {
            this.name = name;
            children = new HashSet<Category>();
            parents = new HashSet<Category>();
        }

        public String getName() {
            return name;
        }

        public Set<Category> getChildren() {
            return children;
        }

        public Set<Category> getParents() {
            return parents;
        }
    }
}
