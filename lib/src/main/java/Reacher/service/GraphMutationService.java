package Reacher.service;

import Reacher.domain.INode;

public interface GraphMutationService {
	void addNode(INode node);
	void removeNode(int nodeId);
	void addEdge(int fromNodeId, int toNodeId);
	void removeEdge(int fromNodeId, int toNodeId);
}
