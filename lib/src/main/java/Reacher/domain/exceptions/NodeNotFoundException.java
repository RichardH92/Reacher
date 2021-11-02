package Reacher.domain.exceptions;

public class NodeNotFoundException extends RuntimeException {

	private int nodeId;

	public NodeNotFoundException(int nodeId) {
		super(String.format("Node was not found with the given id: %d", nodeId));
		this.nodeId = nodeId;
	}

	public int getNodeId() {
		return nodeId;
	}
}
