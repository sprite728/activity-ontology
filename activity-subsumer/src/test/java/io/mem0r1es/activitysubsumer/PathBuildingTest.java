package io.mem0r1es.activitysubsumer;

import io.mem0r1es.activitysubsumer.algs.PathBuilder;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.testng.annotations.Test;

@Test
public class PathBuildingTest {

	private static List<String> path(String... vertices) {
		List<String> result = new LinkedList<String>();
		for (String vertex : vertices) {
			result.add(vertex);
		}
		return result;
	}

	@Test
	public void chainTest() {
		DirectedAcyclicGraph<String, DefaultEdge> graph = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
		graph.addVertex("a");
		graph.addVertex("b");
		graph.addVertex("c");
		graph.addVertex("d");
		graph.addEdge("a", "b");
		graph.addEdge("b", "c");
		graph.addEdge("c", "d");

		PathBuilder<String, DefaultEdge> pathBuilder = new PathBuilder<String, DefaultEdge>(graph, "a", Collections.singleton("d"));

		Map<String, Set<List<String>>> allPaths = pathBuilder.getAllPaths();
		Assert.assertEquals(1, allPaths.size());

		Assert.assertEquals(1, allPaths.get("d").size());
		List<String> computedPath = allPaths.get("d").iterator().next();

		Assert.assertEquals(path("a", "b", "c", "d"), computedPath);
	}

	@Test
	public void treeTest() {
		DirectedAcyclicGraph<String, DefaultEdge> graph = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
		graph.addVertex("a");
		graph.addVertex("b");
		graph.addVertex("c");
		graph.addVertex("d");
		graph.addVertex("e");
		graph.addEdge("a", "b");
		graph.addEdge("a", "c");
		graph.addEdge("b", "d");
		graph.addEdge("b", "e");

		Set<String> destinations = new HashSet<String>();
		destinations.add("c");
		destinations.add("d");
		PathBuilder<String, DefaultEdge> pathBuilder = new PathBuilder<String, DefaultEdge>(graph, "a", destinations);

		Map<String, Set<List<String>>> allPaths = pathBuilder.getAllPaths();
		Assert.assertEquals(2, allPaths.size());

		Assert.assertEquals(1, allPaths.get("d").size());
		Assert.assertTrue(allPaths.get("d").contains(path("a", "b", "d")));

		Assert.assertEquals(1, allPaths.get("c").size());
		Assert.assertTrue(allPaths.get("c").contains(path("a", "c")));
	}

	@Test
	public void testDiamond() {
		DirectedAcyclicGraph<String, DefaultEdge> graph = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
		graph.addVertex("a");
		graph.addVertex("b");
		graph.addVertex("c");
		graph.addVertex("d");
		graph.addEdge("a", "b");
		graph.addEdge("a", "c");
		graph.addEdge("b", "d");
		graph.addEdge("c", "d");

		Set<String> destinations = new HashSet<String>();
		destinations.add("c");
		destinations.add("d");
		PathBuilder<String, DefaultEdge> pathBuilder = new PathBuilder<String, DefaultEdge>(graph, "a", destinations);

		Map<String, Set<List<String>>> allPaths = pathBuilder.getAllPaths();
		Assert.assertEquals(2, allPaths.size());

		Assert.assertEquals(2, allPaths.get("d").size());
		Assert.assertTrue(allPaths.get("d").contains(path("a", "b", "d")));
		Assert.assertTrue(allPaths.get("d").contains(path("a", "c", "d")));

		Assert.assertEquals(1, allPaths.get("c").size());
		Assert.assertTrue(allPaths.get("c").contains(path("a", "c")));
	}
	
	@Test
	public void testComplicatedStuff() {
		DirectedAcyclicGraph<String, DefaultEdge> graph = new DirectedAcyclicGraph<String, DefaultEdge>(DefaultEdge.class);
		graph.addVertex("a");
		graph.addVertex("b");
		graph.addVertex("c");
		graph.addVertex("d");
		graph.addVertex("e");
		graph.addVertex("f");
		graph.addEdge("a", "b");
		graph.addEdge("a", "c");
		graph.addEdge("b", "d");
		graph.addEdge("c", "d");
		graph.addEdge("d", "e");
		graph.addEdge("d", "f");

		Set<String> destinations = new HashSet<String>();
		destinations.add("b");
		destinations.add("d");
		destinations.add("f");
		PathBuilder<String, DefaultEdge> pathBuilder = new PathBuilder<String, DefaultEdge>(graph, "a", destinations);

		Map<String, Set<List<String>>> allPaths = pathBuilder.getAllPaths();
		Assert.assertEquals(3, allPaths.size());

		Assert.assertEquals(1, allPaths.get("b").size());
		Assert.assertTrue(allPaths.get("b").contains(path("a", "b")));
		
		Assert.assertEquals(2, allPaths.get("d").size());
		Assert.assertTrue(allPaths.get("d").contains(path("a", "b", "d")));
		Assert.assertTrue(allPaths.get("d").contains(path("a", "c", "d")));

		Assert.assertEquals(2, allPaths.get("f").size());
		Assert.assertTrue(allPaths.get("f").contains(path("a", "b", "d", "f")));
		Assert.assertTrue(allPaths.get("f").contains(path("a", "c", "d", "f")));
	}
}