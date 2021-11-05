package Reacher;

import Reacher.domain.INode;
import Reacher.domain.exceptions.NodeNotFoundException;
import Reacher.service.IGraph;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.ejml.data.DMatrixSparseCSC;
import org.ejml.simple.SimpleMatrix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Graph implements IGraph {

	private final ReadWriteLock readWriteLock;
	private final int n;
	private final Map<Integer, INode> vertexNumToNode;
	private final Map<Integer, INode> nodeIdToNode;
	private final Map<Integer, Integer> nodeIdToVertexNum;
	private final DMatrixSparseCSC adjacencyMatrix;
	private final DMatrixSparseCSC reachabilityMatrix;
	private final Set<Integer> unusedVertexNums;

	public Graph(
			int n,
			Map<Integer, INode> vertexNumToNode,
			Map<Integer, Integer> nodeIdToVertexNum,
			Map<Integer, INode> nodeIdToNode,
			DMatrixSparseCSC adjacencyMatrix,
			DMatrixSparseCSC reachabilityMatrix) {

		this.n = n;
		this.vertexNumToNode = new HashMap<>(vertexNumToNode);
		this.nodeIdToVertexNum = new HashMap<>(nodeIdToVertexNum);
		this.adjacencyMatrix = adjacencyMatrix;
		this.reachabilityMatrix = reachabilityMatrix;
		this.nodeIdToNode = new HashMap<>(nodeIdToNode);
		this.unusedVertexNums = new HashSet<>();
		this.readWriteLock = new ReentrantReadWriteLock();
	}

	@Override
	public Optional<INode> getNode(int nodeId) {
		return Optional.ofNullable(nodeIdToNode.get(nodeId));
	}

	@Override
	public List<INode> getAncestors(int nodeId) {
		readWriteLock.readLock().lock();
		try {
			assertNodeExists(nodeId);

			int colId = nodeIdToVertexNum.get(nodeId);

			var nodeListBuilder = ImmutableList.<INode>builder();

			for (int i = 0; i < n; i++) {
				if (reachabilityMatrix.get(i, colId) > 0) {
					INode node = vertexNumToNode.get(i);
					nodeListBuilder.add(node);
				}
			}

			return nodeListBuilder.build();
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	@Override
	public List<INode> getDescendants(int nodeId) {
		readWriteLock.readLock().lock();
		try {
			assertNodeExists(nodeId);

			int rowId = nodeIdToVertexNum.get(nodeId);

			var nodeListBuilder = ImmutableList.<INode>builder();

			for (int i = 0; i < n; i++) {
				if (reachabilityMatrix.get(rowId, i) > 0) {
					INode node = vertexNumToNode.get(i);
					nodeListBuilder.add(node);
				}
			}

			return nodeListBuilder.build();
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	@Override
	public boolean doesPathExist(int fromNodeId, int toNodeId) {
		readWriteLock.readLock().lock();
		try {
			assertNodeExists(fromNodeId);
			assertNodeExists(toNodeId);

			int rowId = nodeIdToVertexNum.get(fromNodeId);
			int colId = nodeIdToVertexNum.get(toNodeId);

			var result = reachabilityMatrix.get(rowId, colId) >= 1;

			return result;
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	@Override
	public List<INode> getNodes() {
		readWriteLock.readLock().lock();
		try {
			var builder = ImmutableList.<INode>builder();

			for (int i = 0; i < n; i++) {
				if (!unusedVertexNums.contains(i)) {
					builder.add(vertexNumToNode.get(i));
				}
			}

			return builder.build();
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	@Override
	public Multimap<Integer, Integer> getEdges() {
		readWriteLock.readLock().lock();

		try {
			var builder = ImmutableMultimap.<Integer, Integer>builder();

			for (int i = 0; i < n; i++) {

				if (unusedVertexNums.contains(i)) {
					continue;
				}

				for (int j = 0; j < n; j++) {

					if (unusedVertexNums.contains(j)) {
						continue;
					}

					if (adjacencyMatrix.get(i, j) > 0) {
						var from = vertexNumToNode.get(i);
						var to = vertexNumToNode.get(j);

						builder.put(from.getId(), to.getId());
					}

				}
			}

			return builder.build();
		} finally {
			readWriteLock.readLock().unlock();
		}
	}

	private void assertNodeExists(int nodeId) {
		if (!nodeIdToVertexNum.containsKey(nodeId)) {
			throw new NodeNotFoundException(nodeId);
		}
	}

	@Override
	public void addNode(INode node) {

	}

	@Override
	public void removeNode(int nodeId) {
		readWriteLock.writeLock().lock();
		try {
			assertNodeExists(nodeId);
			assertNodeIsALeaf(nodeId);

			int rowId = nodeIdToVertexNum.get(nodeId);
			int colId = nodeIdToVertexNum.get(nodeId);

			for (int i = 0; i < reachabilityMatrix.numRows; i++) {
				reachabilityMatrix.set(i, colId, 0);
				adjacencyMatrix.set(i, colId, 0);
			}

			for (int j = 0; j < reachabilityMatrix.numCols; j++) {
				reachabilityMatrix.set(rowId, j, 0);
				adjacencyMatrix.set(rowId, j, 0);
			}

			unusedVertexNums.add(rowId);
			nodeIdToNode.remove(nodeId);
			nodeIdToVertexNum.remove(nodeId);

		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	private void assertNodeIsALeaf(int nodeId) {

	}

	private void assertEdgeDoesNotExist(int from, int to) {

	}

	private void assertPathDoesNotExist(int from, int to) {

	}

	private void assertMultiplePathsExist(int from, int to) {

	}

	@Override
	public void addEdge(int fromNodeId, int toNodeId) {
		readWriteLock.writeLock().lock();
		try {

			assertNodeExists(fromNodeId);
			assertNodeExists(toNodeId);
			assertEdgeDoesNotExist(fromNodeId, toNodeId);
			// check for cycles
			assertPathDoesNotExist(toNodeId, fromNodeId);

			int fromNodeIntegerId = nodeIdToVertexNum.get(fromNodeId);
			int toNodeIntegerId = nodeIdToVertexNum.get(toNodeId);

			for (INode ancestor : getAncestors(fromNodeId)) {
				int ancestorRowId = nodeIdToVertexNum.get(ancestor.getId());
				for (int i = 0; i < n; i++) {
					reachabilityMatrix.set(ancestorRowId, i, reachabilityMatrix.get(ancestorRowId, i) + reachabilityMatrix.get(toNodeIntegerId, i));
				}
			}

			for (INode descendant : getDescendants(toNodeId)) {
				int descendantColId = nodeIdToVertexNum.get(descendant.getId());
				for (int i = 0; i < n; i++) {
					reachabilityMatrix.set(i, descendantColId, reachabilityMatrix.get(i, descendantColId) + reachabilityMatrix.get(i, fromNodeIntegerId));
				}
			}

			reachabilityMatrix.set(fromNodeIntegerId, toNodeIntegerId, reachabilityMatrix.get(fromNodeIntegerId, toNodeIntegerId) + 1);
			adjacencyMatrix.set(fromNodeIntegerId, toNodeIntegerId, 1);

		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	@Override
	public void removeEdge(int fromNodeId, int toNodeId) {
		readWriteLock.writeLock().lock();

		try {

			assertNodeExists(fromNodeId);
			assertNodeExists(toNodeId);
			// check graph won't become disconnected
			assertMultiplePathsExist(fromNodeId, toNodeId);

			int fromNodeIntegerId = nodeIdToVertexNum.get(fromNodeId);
			int toNodeIntegerId = nodeIdToVertexNum.get(toNodeId);

			for (INode ancestor : getAncestors(fromNodeId)) {
				int ancestorRowId = nodeIdToVertexNum.get(ancestor.getId());
				for (int i = 0; i < n; i++) {
					reachabilityMatrix.set(ancestorRowId, i, reachabilityMatrix.get(ancestorRowId, i) - reachabilityMatrix.get(toNodeIntegerId, i));
				}
			}

			for (INode descendant : getDescendants(toNodeId)) {
				int descendantColId = nodeIdToVertexNum.get(descendant.getId());
				for (int i = 0; i < n; i++) {
					reachabilityMatrix.set(i, descendantColId, reachabilityMatrix.get(i, descendantColId) - reachabilityMatrix.get(i, fromNodeIntegerId));
				}
			}

			reachabilityMatrix.set(fromNodeIntegerId, toNodeIntegerId, reachabilityMatrix.get(fromNodeIntegerId, toNodeIntegerId) - 1);

		} finally {
			readWriteLock.writeLock().unlock();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Graph graph = (Graph) o;

		return this.getNodes().equals(graph.getNodes()) && this.getEdges().equals(graph.getEdges());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getEdges(), getNodes());
	}

	public static GraphBuilder builder() {
		return new GraphBuilder();
	}

	public GraphBuilder toBuilder() {
		return new GraphBuilder(getNodes(), getEdges());
	}
}
