package Reacher.service;

import Reacher.domain.INode;
import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Optional;

public interface IGraph extends GraphMutationService {
	Optional<INode> getNode(int nodeId);
	List<INode> getChildren(int nodeId);
	List<INode> getParents(int nodeId);
	List<INode> getAncestors(int nodeId);
	List<INode> getDescendants(int nodeId);
	boolean doesPathExist(int nodeAId, int nodeBId);
	List<INode> getNodes();
	Multimap<Integer, Integer> getEdges();
}
