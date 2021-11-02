package Reacher;

import Reacher.domain.INode;
import Reacher.utils.GraphUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

public class GraphBuilder {

	ImmutableList.Builder<INode> vertices;
	ImmutableMultimap.Builder<Integer, Integer> edges;

	public GraphBuilder() {
		vertices = ImmutableList.builder();
		edges = ImmutableMultimap.builder();
	}

	public GraphBuilder(List<INode> vertices, Multimap<Integer, Integer> edges) {
		this.vertices = ImmutableList.builder();
		this.edges = ImmutableMultimap.builder();

		for (var node : vertices) {
			this.vertices.add(node);
		}

		for (var edge : edges.entries()) {
			this.edges.put(edge.getKey(), edge.getValue());
		}
	}

	public GraphBuilder addNode(INode node) {
		vertices.add(node);
		return this;
	}

	public GraphBuilder addEdge(int fromNodeId, int toNodeId) {
		edges.put(fromNodeId, toNodeId);
		return this;
	}

	public Graph build() {
		return GraphUtils.constructGraph(vertices.build(), edges.build());
	}
}
