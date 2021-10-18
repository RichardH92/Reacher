package Reacher.utils;

import Reacher.Graph;
import Reacher.domain.INode;
import Reacher.domain.Node;
import org.apache.commons.math3.util.Pair;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class BenchmarkUtils {

	/**
	 * Returns a random simple DAG containing {@code V} vertices and {@code E} edges.
	 * Note: it is not uniformly selected at random among all such DAGs.
	 * @param V the number of vertices
	 * @param E the number of vertices
	 * @return a random simple DAG on {@code V} vertices, containing a total
	 *     of {@code E} edges
	 * @throws IllegalArgumentException if no such simple DAG exists
	 */
	public static Graph generateRandomGraph(int V, int E) {
		if (E > (long) V * (V - 1) / 2) {
			throw new IllegalArgumentException("Too many edges");
		}
		if (E < 0) {
			throw new IllegalArgumentException("Too few edges");
		}

		var graphBuilder = Graph.builder();
		Set<String> set = new HashSet<>();

		INode[] vertices = new INode[V];
		for (int i = 0; i < V; i++) {
			vertices[i] = new Node(Integer.toString(i));
			graphBuilder.addNode(vertices[i]);
		}

		shuffleArray(vertices);

		Random random = new Random(System.currentTimeMillis());

		while (set.size() < E) {



			int v = random.nextInt(V);
			int w = random.nextInt(V);

			INode from = vertices[v];
			INode to = vertices[w];

			String e = from.getId() + to.getId();
			if ((v < w) && !set.contains(e)) {
				set.add(e);

				graphBuilder.addEdge(from.getId(), to.getId());
			}
		}
		return graphBuilder.build();
	}

	// Implementing Fisher–Yates shuffle
	private static void shuffleArray(INode[] array) {
		// If running on Java 6 or older, use `new Random()` on RHS here
		Random rnd = ThreadLocalRandom.current();
		for (int i = array.length - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			INode a = array[index];
			array[index] = array[i];
			array[i] = a;
		}
	}
}
