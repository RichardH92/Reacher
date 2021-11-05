package Reacher.utils;

import Reacher.Graph;
import Reacher.domain.INode;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import org.apache.commons.math3.util.Pair;
import org.ejml.data.DMatrixRMaj;
import org.ejml.data.DMatrixSparseCSC;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.ops.DConvertMatrixStruct;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphUtils {

	public static Graph constructGraph(List<INode> nodes, Multimap<Integer, Integer> edges) {
		int n = nodes.size();
		Map<Integer, INode> nodeIdToNodeMap = buildNodeIdToNodeMap(nodes);
		Map<Integer, Integer> nodeIdToIntegerIds = assignVertexNumToNodes(nodes);
		Map<Integer, INode> integerIdToNodeMap = buildIntegerIdToNodeMap(nodes, nodeIdToIntegerIds);
		DMatrixSparseCSC adjacencyMatrix = ((MatrixWrapper) constructAdjacencyMatrix(n, edges, nodeIdToIntegerIds)).matrix;
		DMatrixSparseCSC identityMatrix = ((MatrixWrapper) buildIdentityMatrix(n)).matrix;
		DMatrixSparseCSC reachabilityMatrix = ((MatrixWrapper) constructReachabilityMatrix(adjacencyMatrix, identityMatrix)).matrix;

		return new Graph(n, integerIdToNodeMap, nodeIdToIntegerIds, nodeIdToNodeMap, adjacencyMatrix, reachabilityMatrix);
	}

	private static Map<Integer, INode> buildIntegerIdToNodeMap(List<INode> nodes, Map<Integer, Integer> nodeIdToIntegerId) {
		return nodes.stream()
				.collect(Collectors.toMap(node -> nodeIdToIntegerId.get(node.getId()), Function.identity()));
	}

	private static Map<Integer, INode> buildNodeIdToNodeMap(List<INode> nodes) {
		return nodes.stream()
				.collect(Collectors.toMap(INode::getId, Function.identity()));
	}

	@VisibleForTesting
	static Matrix constructReachabilityMatrix(DMatrixSparseCSC adjacencyMatrix, DMatrixSparseCSC identityMatrix) {


		DMatrixRMaj a = DConvertMatrixStruct.convert(adjacencyMatrix, (DMatrixRMaj) null);
		DMatrixRMaj i = DConvertMatrixStruct.convert(identityMatrix, (DMatrixRMaj) null);

		DMatrixRMaj iMinusA = new DMatrixRMaj(a.numRows, a.numCols);
		CommonOps_DDRM.subtract(i, a, iMinusA);
		CommonOps_DDRM.invert(iMinusA);

		DMatrixRMaj result = new DMatrixRMaj(a.numRows, a.numCols);
		CommonOps_DDRM.mult(i, iMinusA, result);

		DMatrixSparseCSC ret = DConvertMatrixStruct.convert(result, (DMatrixSparseCSC) null, 0);

		return new MatrixWrapper(ret);
	}

	@VisibleForTesting
	static Matrix buildIdentityMatrix(int n) {
		var matrix = new DMatrixSparseCSC(n, n, 2 * n);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (j == i) {
					matrix.set(i, j, 1);
				} else {
					matrix.set(i, j, 0);
				}
			}
		}

		return new MatrixWrapper(matrix);
	}

	@VisibleForTesting
	static Matrix constructAdjacencyMatrix(int n, Multimap<Integer, Integer> edges, Map<Integer, Integer> nodeIdToVertexNum) {

		var matrix = new DMatrixSparseCSC(n, n, 2 * n);

		edges.asMap().entrySet().stream()
				.flatMap(entry -> entry.getValue().stream()
						.map(val -> Pair.create(entry.getKey(), val))
						.map(pair -> Pair.create(
								nodeIdToVertexNum.get(pair.getKey()),
								nodeIdToVertexNum.get(pair.getValue())
						)))
				.forEach(pair -> matrix.set(pair.getKey(), pair.getValue(), 1));

		return new MatrixWrapper(matrix);
	}

	private static Map<Integer, Integer> assignVertexNumToNodes(List<INode> nodes) {
		ImmutableMap.Builder<Integer, Integer> ids = ImmutableMap.builder();

		int count = 0;
		for (var node : nodes) {
			ids.put(node.getId(), count);
			count++;
		}

		return ids.build();
	}

	private static class MatrixWrapper implements Matrix {

		private DMatrixSparseCSC matrix;

		private MatrixWrapper(DMatrixSparseCSC matrix) {
			this.matrix = matrix;
		}

		@Override
		public int get(int row, int col) {
			return (int) matrix.get(row, col);
		}

		@Override
		public void set(int row, int col, int value) {
			matrix.set(row, col, value);
		}
	}
}
