package Reacher;

import Reacher.domain.INode;
import Reacher.domain.exceptions.NodeNotFoundException;
import Reacher.service.IGraph;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.ejml.simple.SimpleMatrix;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Graph implements IGraph {

	private final int n;
	private final Map<Integer, INode> integerIdToNodes;
	private final Map<String, INode> nodeIdToNodes;
	private final Map<String, Integer> nodeIdToIntegerIds;
	private final SimpleMatrix adjacencyMatrix;
	private final SimpleMatrix reachabilityMatrix;
	private List<INode> nodes;
	private Multimap<String, String> edges;

	public Graph(
			int n,
			Map<Integer, INode> integerIdToNodes,
			Map<String, Integer> nodeIdToIntegerIds,
			Map<String, INode> nodeIdToNodes,
			SimpleMatrix adjacencyMatrix,
			SimpleMatrix reachabilityMatrix,
			List<INode> nodes,
			Multimap<String, String> edges) {

		this.n = n;
		this.integerIdToNodes = integerIdToNodes;
		this.nodeIdToIntegerIds = nodeIdToIntegerIds;
		this.adjacencyMatrix = adjacencyMatrix;
		this.reachabilityMatrix = reachabilityMatrix;
		this.nodeIdToNodes = nodeIdToNodes;
		this.nodes = nodes;
		this.edges = edges;
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
		return new GraphBuilder(nodes, edges);
	}
}
