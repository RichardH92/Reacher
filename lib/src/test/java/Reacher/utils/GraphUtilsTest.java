package Reacher.utils;

import Reacher.GraphBuilder;
import Reacher.domain.Node;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static Reacher.utils.GraphUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphUtilsTest {

	@Test
	public void testBuildIdentityMatrixHappyPath() {

		int n = 10;
		var matrix = buildIdentityMatrix(n);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j) {
					assertEquals(1, matrix.get(i, j));
				} else {
					assertEquals(0, matrix.get(i, j));
				}
			}
		}
	}

	@Test
	public void testConstructAdjacencyMatrixHappyPath() {
		var nodeIdToVertexNum = ImmutableMap.of(
				1, 0,
				2, 1,
				3, 2,
				4, 3,
				5, 4
		);

		var edges = ImmutableMultimap.<Integer, Integer>builder()
				.put(1, 2)
				.put(2, 3)
				.put(1, 4)
				.put(3, 5)
				.put(4, 5)
				.build();

		var matrix = constructAdjacencyMatrix(5, edges, nodeIdToVertexNum);

		edges.forEach((fromNodeId, toNodeId) -> {
			assertEquals(1, matrix.get(fromNodeId - 1, toNodeId - 1));
			assertEquals(0, matrix.get(toNodeId - 1, fromNodeId - 1));
		});

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				var fromNodeId = i + 1;
				var toNodeId = j + 1;

				if (Optional.ofNullable(edges.get(fromNodeId))
						.map(toNodes -> toNodes.contains(toNodeId))
								.orElse(false)) {

					assertEquals(1, matrix.get(i, j));
				} else {
					assertEquals(0, matrix.get(i, j));
				}
			}
		}
	}

	/*@Test
	public void testConstructReachabilityMatrixHappyPath() {
		var nodeIdToVertexNum = ImmutableMap.of(
				1, 0,
				2, 1,
				3, 2,
				4, 3,
				5, 4
		);

		var edges = ImmutableMultimap.<Integer, Integer>builder()
				.put(1, 2)
				.put(2, 3)
				.put(1, 4)
				.put(3, 5)
				.put(4, 5)
				.build();

		var expectedResult = ImmutableMap.<Pair<Integer, Integer>, Integer>builder()
				.put(Pair.create(1, 2), 1)
				.put(Pair.create(2, 1), 0)
				.put(Pair.create(1, 3), 1)
				.put(Pair.create(3, 1), 0)
				.put(Pair.create(1, 4), 1)
				.put(Pair.create(4, 1), 0)
				.put(Pair.create(0, 0), 0)

				// TODO
				.put(Pair.create(3, 5), 1)
				.build();


		var i = buildIdentityMatrix(5);
		var a = constructAdjacencyMatrix(5, edges, nodeIdToVertexNum);

		var matrix = constructReachabilityMatrix(((MatrixWrapper) a).matrix, ((MatrixWrapper) i).matrix);

		expectedResult.forEach((key, val) -> assertEquals(val, matrix.get(key.getKey(), key.getValue())));

	}*/
}
