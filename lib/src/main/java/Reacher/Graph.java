package Reacher;

import Reacher.domain.INode;
import Reacher.service.IGraph;
import com.google.common.collect.ImmutableList;
import org.ejml.simple.SimpleMatrix;

import java.util.List;
import java.util.Map;

public class Graph implements IGraph {

	private int n;
	private Map<Integer, INode> integerIdToNodes;
	private Map<String, INode> nodeIdToNodes;
	private Map<String, Integer> nodeIdToIntegerIds;
	private SimpleMatrix adjacencyMatrix;
	private SimpleMatrix reachabilityMatrix;

	public Graph(
			int n,
			Map<Integer, INode> integerIdToNodes,
			Map<String, Integer> nodeIdToIntegerIds,
			Map<String, INode> nodeIdToNodes,
			SimpleMatrix adjacencyMatrix,
			SimpleMatrix reachabilityMatrix) {

		this.n = n;
		this.integerIdToNodes = integerIdToNodes;
		this.nodeIdToIntegerIds = nodeIdToIntegerIds;
		this.adjacencyMatrix = adjacencyMatrix;
		this.reachabilityMatrix = reachabilityMatrix;
		this.nodeIdToNodes = nodeIdToNodes;
	}

	@Override
	public List<INode> getAncestors(String nodeId) {
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
		int rowId = nodeIdToIntegerIds.get(fromNodeId);
		int colId = nodeIdToIntegerIds.get(toNodeId);

		return reachabilityMatrix.get(rowId, colId) >= 1;
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
}
