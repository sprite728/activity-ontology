package io.mem0r1es.activitysubsumer.utils;

import io.mem0r1es.activitysubsumer.wordnet.SynsetNode;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Ivan Gavrilović
 */
public class BFSHierarchicalNode implements Iterator<HierarchicalStructure> {

    private Deque<SynsetNode> queue;

    public BFSHierarchicalNode(SynsetNode startNode) {
        queue = new LinkedList<SynsetNode>();
        queue.addFirst(startNode);
    }

    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }

    @Override
    public SynsetNode next() {
        SynsetNode node = queue.pollFirst();
        for (SynsetNode sn : node.getChildren()) queue.addLast(sn);

        return node;
    }

    @Override
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
