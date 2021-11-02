package Reacher.domain;

public class Node implements INode {

	private int id;

	public Node(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}
}
