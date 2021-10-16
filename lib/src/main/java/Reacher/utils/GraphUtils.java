package Reacher.utils;

import Reacher.Graph;
import Reacher.domain.INode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import org.apache.commons.math3.util.Pair;
import org.ejml.simple.SimpleMatrix;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphUtils {

	public static Graph constructGraph(List<INode> nodes, Multimap<String, String> edges) {
		int n = nodes.size();
		Map<String, INode> nodeIdToNodeMap = buildNodeIdToNodeMap(nodes);
		Map<String, Integer> nodeIdToIntegerIds = assignIntegerIdToNodes(nodes);
		Map<Integer, INode> integerIdToNodeMap = buildIntegerIdToNodeMap(nodes, nodeIdToIntegerIds);
		SimpleMatrix adjacencyMatrix = constructAdjacencyMatrix(n, edges, nodeIdToIntegerIds);
		SimpleMatrix identityMatrix = buildIdentityMatrix(n);
		SimpleMatrix reachabilityMatrix = constructReachabilityMatrix(adjacencyMatrix, identityMatrix);

		return new Graph(n, integerIdToNodeMap, nodeIdToIntegerIds, nodeIdToNodeMap, adjacencyMatrix, reachabilityMatrix);
	}

	private static Map<Integer, INode> buildIntegerIdToNodeMap(List<INode> nodes, Map<String, Integer> nodeIdToIntegerId) {
		return nodes.stream()
				.collect(Collectors.toMap(node -> nodeIdToIntegerId.get(node.getId()), Function.identity()));
	}

	private static Map<String, INode> buildNodeIdToNodeMap(List<INode> nodes) {
		return nodes.stream()
				.collect(Collectors.toMap(INode::getId, Function.identity()));
	}

	private static SimpleMatrix constructReachabilityMatrix(SimpleMatrix adjacencyMatrix, SimpleMatrix identityMatrix) {
		return identityMatrix.mult(identityMatrix
					.minus(adjacencyMatrix)
					.invert())
				.minus(identityMatrix);
	}

	private static SimpleMatrix buildIdentityMatrix(int n) {
		double[][] data = new double[n][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (j == i) {
					data[i][j] = 1;
				} else {
					data[i][j] = 0;
				}
			}
		}

		return new SimpleMatrix(data);
	}

	private static SimpleMatrix constructAdjacencyMatrix(int n, Multimap<String, String> edges, Map<String, Integer> nodeIdToIntegerIds) {

		double[][] data = new double[n][n];
		initializeMatrixToZero(data, n);
		edges.asMap().entrySet().stream()
				.flatMap(entry -> entry.getValue().stream()
						.map(val -> Pair.create(entry.getKey(), val))
						.map(pair -> Pair.create(
								nodeIdToIntegerIds.get(pair.getKey()),
								nodeIdToIntegerIds.get(pair.getValue())
						)))
				.forEach(pair -> data[pair.getKey()][pair.getValue()] = 1);

		return new SimpleMatrix(data);
	}

	private static void initializeMatrixToZero(double[][] data, int n) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				data[i][j] = 0;
			}
		}
	}

	private static Map<String, Integer> assignIntegerIdToNodes(List<INode> nodes) {
		ImmutableMap.Builder<String, Integer> ids = ImmutableMap.builder();

		int count = 0;
		for (var node : nodes) {
			ids.put(node.getId(), count);
			count++;
		}

		return ids.build();
	}
}
