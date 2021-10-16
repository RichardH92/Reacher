package Reacher.domain.exceptions;

public class NodeNotFoundException extends RuntimeException {

	private String nodeId;

	public NodeNotFoundException(String nodeId) {
		super(String.format("Node was not found with the given id: %s", nodeId));
		this.nodeId = nodeId;
	}

	public String getNodeId() {
		return nodeId;
	}
}
