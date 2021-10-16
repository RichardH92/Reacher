package Reacher.service;

import Reacher.domain.INode;

import java.util.List;
import java.util.Optional;

public interface IGraph extends GraphMutationService {
	Optional<INode> getNode(String nodeId);
	List<INode> getAncestors(String nodeId);
	List<INode> getDescendants(String nodeId);
	boolean doesPathExist(String nodeAId, String nodeBId);
}
