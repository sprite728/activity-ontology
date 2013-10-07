package io.mem0r1es.activitysubsumer.useractivitytree.algs;

import io.mem0r1es.activitysubsumer.useractivitytree.utils.GraphUtils;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

/**
 * Static methods for online lowest common ancestor search.
 * 
 * @author Sebastian Claici
 */
public final class LowestCommonAncestor<V, E> {
	private final DirectedGraph<V, E> reversedDAG;

	public LowestCommonAncestor(DirectedGraph<V, E> dag) {
		this.reversedDAG = GraphUtils.reverseGraph(dag);
	}

	/**
	 * <p>
	 * Online lowest common ancestor for directed acyclic graphs.
	 * </p>
	 * <p>
	 * Complexity:<b>O(n)</b> where <i>n</i> is the number of nodes in the graph
	 * </p>
	 */
	public Set<V> onlineLCA(V fst, V scd) {
		BreadthFirstIterator<V, E> iter = new BreadthFirstIterator<V, E>(reversedDAG, fst);

		Set<V> ancestorsFst = new HashSet<V>();
		while (iter.hasNext())
			ancestorsFst.add(iter.next());

		iter = new BreadthFirstIterator<V, E>(reversedDAG, scd);
		Set<V> result = new HashSet<V>();
		while (iter.hasNext()) {
			V next = iter.next();
			if (ancestorsFst.contains(next))
				result.add(next);
		}

		return result;
	}
}
