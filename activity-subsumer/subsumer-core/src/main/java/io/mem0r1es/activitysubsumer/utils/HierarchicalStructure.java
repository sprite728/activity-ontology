package io.mem0r1es.activitysubsumer.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ivan Gavrilović
 */
public abstract class HierarchicalStructure<T> {
    protected Set<T> parents;
    protected Set<T> children;

    protected HierarchicalStructure() {
        parents = new HashSet<T>();
        children = new HashSet<T>();
    }

    public void addParent(T parent){
        parents.add(parent);
    }
    public void addChild(T child){
        children.add(child);
    }
    public Set<T> getParents(){
        return parents;
    }
    public Set<T> getChildren(){
        return children;
    }
}
