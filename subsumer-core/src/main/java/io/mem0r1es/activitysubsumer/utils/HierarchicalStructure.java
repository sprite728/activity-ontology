package io.mem0r1es.activitysubsumer.utils;

import java.util.Set;

/**
 * Interface for hierarchical structures that have child-parent relations.
 *
 * @author Ivan Gavrilović
 */
public abstract class HierarchicalStructure<T extends HierarchicalStructure> {
    public void addParent(T parent){
        getParents().add(parent);
    }
    public void addChild(T child){
        getChildren().add(child);
    }
    public abstract Set<T> getParents();
    public abstract Set<T> getChildren();
}
