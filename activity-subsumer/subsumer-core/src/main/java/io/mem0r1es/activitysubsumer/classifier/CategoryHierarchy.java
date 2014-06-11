package io.mem0r1es.activitysubsumer.classifier;

import io.mem0r1es.activitysubsumer.io.CategoriesProvider;
import io.mem0r1es.activitysubsumer.utils.HierarchicalStructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public class CategoryHierarchy {
    private Map<String, Category> categories = null;
    private CategoriesProvider parser;

    public CategoryHierarchy(CategoriesProvider parser) {
        this.parser = parser;
    }

    /**
     * Get all categories that are more general than the specified one
     * @param s category for which we are looking more general ones
     * @return all parent categories, including the specified one
     */
    public Set<String> getHierarchy(String s) {
        if (categories == null) {
            Set<Category> readCategories = parser.read();
            categories = new HashMap<String, Category>();
            for(Category c:readCategories) categories.put(c.name, c);
        }

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

    public static class Category extends HierarchicalStructure<Category>{
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
