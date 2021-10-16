package Reacher;

import Reacher.domain.INode;
import Reacher.service.IGraph;

import java.util.List;

public class Graph implements IGraph {

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
