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
		return n == graph.n && Objects.equals(integerIdToNodes, graph.integerIdToNodes) && Objects.equals(nodeIdToNodes, graph.nodeIdToNodes) && Objects.equals(nodeIdToIntegerIds, graph.nodeIdToIntegerIds) && Objects.equals(adjacencyMatrix, graph.adjacencyMatrix) && Objects.equals(reachabilityMatrix, graph.reachabilityMatrix) && Objects.equals(unusedNodeIds, graph.unusedNodeIds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(n, integerIdToNodes, nodeIdToNodes, nodeIdToIntegerIds, adjacencyMatrix, reachabilityMatrix, unusedNodeIds);
	}

	public static GraphBuilder builder() {
		return new GraphBuilder();
	}

	public GraphBuilder toBuilder() {

		var nodesBuilder = ImmutableList.<INode>builder();
		var edgesBuilder = ImmutableMultimap.<String, String>builder();

		// TODO: populate builders

		return new GraphBuilder(nodesBuilder.build(), edgesBuilder.build());
	}
}
