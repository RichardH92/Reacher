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
	private Set<Integer> unusuedNodeIds;

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
		this.unusuedNodeIds = new HashSet<>();
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

		unusuedNodeIds.add(rowId);
		nodeIdToNodes.remove(nodeId);
		nodeIdToIntegerIds.remove(nodeId);
	}

	private void assertNodeIsALeaf(String nodeId) {

	}

	@Override
	public void addEdge(String fromNodeId, String toNodeId) {

	}

	@Override
	public void removeEdge(String fromNodeId, String toNodeId) {

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
