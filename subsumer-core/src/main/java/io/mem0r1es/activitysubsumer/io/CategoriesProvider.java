package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.classifier.CategoryHierarchy;

import java.util.Set;

/**
 * Interface that should be implemented by classes that read any categories source.
 *
 * @author Ivan Gavrilović
 */
public interface CategoriesProvider {
    /**
     * Reading the data from the source.
     *
     * @return {@link java.util.Set} containing all
     * categories
     */
    Set<CategoryHierarchy.Category> read();
}
