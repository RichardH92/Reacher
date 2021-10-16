package Reacher.domain;

public class Node implements INode {

	private String id;

	public Node(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}
}
