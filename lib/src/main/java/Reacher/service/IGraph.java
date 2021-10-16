package Reacher.service;

import Reacher.domain.INode;

import java.util.List;

public interface IGraph extends GraphMutationService {
	List<INode> getAncestors(String nodeId);
	List<INode> getDescendants(String nodeId);
	boolean doesPathExist(String nodeAId, String nodeBId);
}
