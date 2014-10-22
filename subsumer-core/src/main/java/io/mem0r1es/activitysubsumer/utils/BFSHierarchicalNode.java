package io.mem0r1es.activitysubsumer.utils;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Breadth first search implementation.
 *
 * @author Ivan Gavrilović
 */
public class BFSHierarchicalNode<T extends HierarchicalStructure<T>> implements Iterator<HierarchicalStructure<T>> {

    private Deque<T> queue;

    public BFSHierarchicalNode(T startNode) {
        queue = new LinkedList<T>();
        queue.addFirst(startNode);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public T next() {
        T node = queue.pollFirst();
        for (T sn : node.getChildren()) queue.addLast(sn);

        return node;
    }

    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
