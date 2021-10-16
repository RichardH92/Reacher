package Reacher;

import Reacher.domain.INode;
import Reacher.service.IGraph;
import org.ejml.simple.SimpleMatrix;

import java.util.List;
import java.util.Map;

public class Graph implements IGraph {

	private Map<String, INode> nodeIdToNodes;
	private Map<String, Integer> nodeIdToIntegerIds;
	private SimpleMatrix adjacencyMatrix;
	private SimpleMatrix reachabilityMatrix;

	public Graph(
			Map<String, Integer> nodeIdToIntegerIds,
			Map<String, INode> nodeIdToNodes,
			SimpleMatrix adjacencyMatrix,
			SimpleMatrix reachabilityMatrix) {

		this.nodeIdToIntegerIds = nodeIdToIntegerIds;
		this.adjacencyMatrix = adjacencyMatrix;
		this.reachabilityMatrix = reachabilityMatrix;
		this.nodeIdToNodes = nodeIdToNodes;
	}

	@Override
	public List<INode> getAncestors(String nodeId) {
		return null;
	}

	@Override
	public List<INode> getDescendants(String nodeId) {
		return null;
	}

	@Override
	public boolean doesPathExist(String nodeAId, String nodeBId) {
		return false;
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
