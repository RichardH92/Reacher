package Reacher;

import Reacher.domain.INode;
import Reacher.domain.Node;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphTest {

	List<Node> testNodes;
	Graph testGraph;

	@BeforeEach
	public void setup() {
		testNodes = ImmutableList.of(
				new Node("A"),
				new Node("B"),
				new Node("C"),
				new Node("D"),
				new Node("E")
		);

		var builder = new GraphBuilder();

		testNodes.forEach(builder::addNode);

		builder.addEdge("A", "B");
		builder.addEdge("B", "C");
		builder.addEdge("A", "D");
		builder.addEdge("C", "E");
		builder.addEdge("D", "E");

		testGraph = builder.build();
	}

	@Test
	public void testGetAncestorsReturnsCorrectlyHappyPath() {
		List<INode> ancestors = testGraph.getAncestors("C");

		List<INode> expected = ImmutableList.of(
				testNodes.get(0),
				testNodes.get(1)
		);

		assertEquals(expected, ancestors);
	}

	@Test
	public void testGetAncestorsDedupesWhenNodeIsAncestorViaMultiplePaths() {
		List<INode> ancestors = testGraph.getAncestors("E");

		List<INode> expected = ImmutableList.of(
				testNodes.get(0),
				testNodes.get(1),
				testNodes.get(2),
				testNodes.get(3)
		);

		assertEquals(expected, ancestors);
	}

	@Test
	public void testGetAncestorsReturnsEmptyWhenIdBelongsToRoot() {
		assertTrue(testGraph.getAncestors("A").isEmpty());
	}
}
