package Reacher;

import Reacher.domain.INode;
import Reacher.utils.GraphUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.util.List;

public class GraphBuilder {

	ImmutableList.Builder<INode> vertices;
	ImmutableMultimap.Builder<String, String> edges;

	public GraphBuilder() {
		vertices = ImmutableList.builder();
		edges = ImmutableMultimap.builder();
	}

	public GraphBuilder(List<INode> vertices, Multimap<String, String> edges) {
		super();

		vertices.addAll(vertices);
		edges.forEach(edges::put);
	}

	public GraphBuilder addNode(INode node) {
		vertices.add(node);
		return this;
	}

	public GraphBuilder addEdge(String fromNodeId, String toNodeId) {
		edges.put(fromNodeId, toNodeId);
		return this;
	}

	public Graph build() {
		return GraphUtils.constructGraph(vertices.build(), edges.build());
	}
}
