package Reacher.service;

import Reacher.domain.INode;

public interface GraphMutationService {
	void addNode(INode node);
	void removeNode(String nodeId);
	void addEdge(String fromNodeId, String toNodeId);
	void removeEdge(String fromNodeId, String toNodeId);
}
