package Reacher;

import Reacher.domain.INode;
import Reacher.domain.Node;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphTest {

	@Test
	public void testGetAncestorsReturnsCorrectly() {
		List<Node> testData = ImmutableList.of(
				new Node("A"),
				new Node("B"),
				new Node("C"),
				new Node("D")
		);

		var builder = new GraphBuilder();

		testData.forEach(builder::addNode);

		builder.addEdge("A", "B");
		builder.addEdge("B", "C");
		builder.addEdge("A", "D");

		var graph = builder.build();

		List<INode> ancestors = graph.getAncestors("C");

		List<INode> expected = ImmutableList.of(
				testData.get(0),
				testData.get(1)
		);

		assertEquals(expected, ancestors);
	}
}
