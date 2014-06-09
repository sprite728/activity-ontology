package io.mem0r1es.activitysubsumer.io;

import io.mem0r1es.activitysubsumer.classifier.CategoryHierarchy;

import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public interface CategoriesProvider {
    Set<CategoryHierarchy.Category> read();
}
