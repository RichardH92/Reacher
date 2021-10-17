package Reacher;

import Reacher.domain.INode;
import Reacher.domain.exceptions.NodeNotFoundException;
import Reacher.service.IGraph;
import com.google.common.collect.*;
import org.ejml.simple.SimpleMatrix;

import java.util.*;

public class Graph implements IGraph {

	private final int n;
	private final Map<Integer, INode> integerIdToNodes;
	private final Map<String, INode> nodeIdToNodes;
	private final Map<String, Integer> nodeIdToIntegerIds;
	private final SimpleMatrix adjacencyMatrix;
	private final SimpleMatrix reachabilityMatrix;
	private Set<Integer> unusedNodeIds;

	public Graph(
			int n,
			Map<Integer, INode> integerIdToNodes,
			Map<String, Integer> nodeIdToIntegerIds,
			Map<String, INode> nodeIdToNodes,
			SimpleMatrix adjacencyMatrix,
			SimpleMatrix reachabilityMatrix) {

		this.n = n;
		this.integerIdToNodes = new HashMap<>(integerIdToNodes);
		this.nodeIdToIntegerIds = new HashMap<>(nodeIdToIntegerIds);
		this.adjacencyMatrix = adjacencyMatrix;
		this.reachabilityMatrix = reachabilityMatrix;
		this.nodeIdToNodes = new HashMap<>(nodeIdToNodes);
		this.unusedNodeIds = new HashSet<>();
	}

	@Override
	public Optional<INode> getNode(String nodeId) {
		return Optional.ofNullable(nodeIdToNodes.get(nodeId));
	}

	@Override
	public List<INode> getAncestors(String nodeId) {
		assertNodeExists(nodeId);

		int colId = nodeIdToIntegerIds.get(nodeId);

		var nodeListBuilder = ImmutableList.<INode>builder();

		for (int i = 0; i < n; i++) {
			if (reachabilityMatrix.get(i, colId) > 0) {
				INode node = integerIdToNodes.get(i);
				nodeListBuilder.add(node);
			}
		}

		return nodeListBuilder.build();
	}

	@Override
	public List<INode> getDescendants(String nodeId) {
		assertNodeExists(nodeId);

		int rowId = nodeIdToIntegerIds.get(nodeId);

		var nodeListBuilder = ImmutableList.<INode>builder();

		for (int i = 0; i < n; i++) {
			if (reachabilityMatrix.get(rowId, i) > 0) {
				INode node = integerIdToNodes.get(i);
				nodeListBuilder.add(node);
			}
		}

		return nodeListBuilder.build();
	}

	@Override
	public boolean doesPathExist(String fromNodeId, String toNodeId) {
		assertNodeExists(fromNodeId);
		assertNodeExists(toNodeId);

		int rowId = nodeIdToIntegerIds.get(fromNodeId);
		int colId = nodeIdToIntegerIds.get(toNodeId);

		return reachabilityMatrix.get(rowId, colId) >= 1;
	}

	@Override
	public List<INode> getNodes() {
		var builder = ImmutableList.<INode>builder();

		for (int i = 0; i < n; i++) {
			if (!unusedNodeIds.contains(i)) {
				builder.add(integerIdToNodes.get(i));
			}
		}

		return builder.build();
	}

	@Override
	public Multimap<String, String> getEdges() {

		var builder = ImmutableMultimap.<String, String>builder();

		for (int i = 0; i < n; i++) {

			if (unusedNodeIds.contains(i)) {
				continue;
			}

			for (int j = 0; j < n; j++) {

				if (unusedNodeIds.contains(j)) {
					continue;
				}

				if (adjacencyMatrix.get(i, j) > 0) {
					var from = integerIdToNodes.get(i);
					var to = integerIdToNodes.get(j);

					builder.put(from.getId(), to.getId());
				}

			}
		}

		return builder.build();
	}

	private void assertNodeExists(String nodeId) {
		if (!nodeIdToNodes.containsKey(nodeId)) {
			throw new NodeNotFoundException(nodeId);
		}
	}

	@Override
	public void addNode(INode node) {

	}

	@Override
	public void removeNode(String nodeId) {

		assertNodeExists(nodeId);
		assertNodeIsALeaf(nodeId);

		int rowId = nodeIdToIntegerIds.get(nodeId);
		int colId = nodeIdToIntegerIds.get(nodeId);

		reachabilityMatrix.setColumn(colId, 0, 0);
		reachabilityMatrix.setRow(rowId,0, 0);

		// TODO: update adjacency matrix
		unusedNodeIds.add(rowId);
		nodeIdToNodes.remove(nodeId);
		nodeIdToIntegerIds.remove(nodeId);
	}

	private void assertNodeIsALeaf(String nodeId) {

	}

	private void assertEdgeDoesNotExist(String from, String to) {

	}

	private void assertPathDoesNotExist(String from, String to) {

	}

	private void assertMultiplePathsExist(String from, String to) {

	}

	@Override
	public void addEdge(String fromNodeId, String toNodeId) {

		assertNodeExists(fromNodeId);
		assertNodeExists(toNodeId);
		assertEdgeDoesNotExist(fromNodeId, toNodeId);
		// check for cycles
		assertPathDoesNotExist(toNodeId, fromNodeId);

		int fromNodeIntegerId = nodeIdToIntegerIds.get(fromNodeId);
		int toNodeIntegerId = nodeIdToIntegerIds.get(toNodeId);

		for (INode ancestor : getAncestors(fromNodeId)) {
			int ancestorRowId = nodeIdToIntegerIds.get(ancestor.getId());
			for (int i = 0; i < n; i++) {
				reachabilityMatrix.set(ancestorRowId, i, reachabilityMatrix.get(ancestorRowId, i) + reachabilityMatrix.get(toNodeIntegerId, i));
			}
		}

		for (INode descendant : getDescendants(toNodeId)) {
			int descendantColId = nodeIdToIntegerIds.get(descendant.getId());
			for (int i = 0; i < n; i++) {
				reachabilityMatrix.set(i, descendantColId, reachabilityMatrix.get(i, descendantColId) + reachabilityMatrix.get(i, fromNodeIntegerId));
			}
		}

		reachabilityMatrix.set(fromNodeIntegerId, toNodeIntegerId, reachabilityMatrix.get(fromNodeIntegerId, toNodeIntegerId) + 1);
	}

	@Override
	public void removeEdge(String fromNodeId, String toNodeId) {

		assertNodeExists(fromNodeId);
		assertNodeExists(toNodeId);
		// check graph won't become disconnected
		assertMultiplePathsExist(fromNodeId, toNodeId);

		int fromNodeIntegerId = nodeIdToIntegerIds.get(fromNodeId);
		int toNodeIntegerId = nodeIdToIntegerIds.get(toNodeId);

		for (INode ancestor : getAncestors(fromNodeId)) {
			int ancestorRowId = nodeIdToIntegerIds.get(ancestor.getId());
			for (int i = 0; i < n; i++) {
				reachabilityMatrix.set(ancestorRowId, i, reachabilityMatrix.get(ancestorRowId, i) - reachabilityMatrix.get(toNodeIntegerId, i));
			}
		}

		for (INode descendant : getDescendants(toNodeId)) {
			int descendantColId = nodeIdToIntegerIds.get(descendant.getId());
			for (int i = 0; i < n; i++) {
				reachabilityMatrix.set(i, descendantColId, reachabilityMatrix.get(i, descendantColId) - reachabilityMatrix.get(i, fromNodeIntegerId));
			}
		}

		reachabilityMatrix.set(fromNodeIntegerId, toNodeIntegerId, reachabilityMatrix.get(fromNodeIntegerId, toNodeIntegerId) - 1);
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
