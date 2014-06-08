package io.mem0r1es.activitysubsumer.wordnet;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Ivan Gavrilović
 */
public class BFSSynsetNode implements Iterator<SynsetNode> {

    private SynsetNode startNode;
    private Deque<SynsetNode> queue;

    public BFSSynsetNode(SynsetNode startNode) {
        this.startNode = startNode;

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
        for(SynsetNode sn:node.getChildren()) queue.addLast(sn);

        return node;
    }

    @Override
    public void remove() throws UnsupportedOperationException{
        throw new UnsupportedOperationException();
    }
}
