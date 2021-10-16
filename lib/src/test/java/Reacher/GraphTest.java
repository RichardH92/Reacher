package Reacher;

import Reacher.domain.INode;
import Reacher.domain.Node;
import Reacher.domain.exceptions.NodeNotFoundException;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
	public void testGetDescendantsHappyPath() {
		List<INode> descendants = testGraph.getDescendants("B");
		List<INode> expected = ImmutableList.of(
				testNodes.get(2),
				testNodes.get(4)
		);

		assertEquals(expected, descendants);
	}

	@Test
	public void testGetDescendantsDedupesNodesWhenDescendantViaMultiplePaths() {
		List<INode> descendants = testGraph.getDescendants("A");
		List<INode> expected = ImmutableList.of(
				testNodes.get(1),
				testNodes.get(2),
				testNodes.get(3),
				testNodes.get(4)
		);

		assertEquals(expected, descendants);
	}

	@Test
	public void testGetDescendantsReturnsEmptyWhenNodeIsALeaf() {
		assertTrue(testGraph.getDescendants("E").isEmpty());
	}

	@Test
	public void testGetDescendantsThrowsNotFoundExceptionWhenNodeWithIdDNE() {
		NodeNotFoundException exception = assertThrows(NodeNotFoundException.class, () -> testGraph.getAncestors("DNE"));
		assertEquals("Node was not found with the given id: DNE", exception.getMessage());
		assertEquals("DNE", exception.getNodeId());
	}

	@Test
	public void testDoesPathExistHappyPath() {
		assertTrue(testGraph.doesPathExist("A", "E"));
		assertTrue(testGraph.doesPathExist("A", "B"));
		assertTrue(testGraph.doesPathExist("A", "D"));

		assertFalse(testGraph.doesPathExist("E", "A"));

		assertFalse(testGraph.doesPathExist("D", "C"));
		assertFalse(testGraph.doesPathExist("C", "D"));
	}

	@Test
	public void testDoesPathExistThrowsNotFoundExceptionWhenNodeWithIdDNE() {
		NodeNotFoundException exception = assertThrows(NodeNotFoundException.class, () -> testGraph.doesPathExist("DNE", "DNE"));
		assertEquals("Node was not found with the given id: DNE", exception.getMessage());
		assertEquals("DNE", exception.getNodeId());
	}

	@Test
	public void testGetAncestorsThrowsNotFoundExceptionWhenNodeWithIdDNE() {
		NodeNotFoundException exception = assertThrows(NodeNotFoundException.class, () -> testGraph.getAncestors("DNE"));
		assertEquals("Node was not found with the given id: DNE", exception.getMessage());
		assertEquals("DNE", exception.getNodeId());
	}

	@Test
	public void testGetAncestorsThrowsNotFoundExceptionWhenNodePreviouslyExisted() {
		testGraph.removeNode("E");

		NodeNotFoundException exception = assertThrows(NodeNotFoundException.class, () -> testGraph.getAncestors("E"));
		assertEquals("Node was not found with the given id: E", exception.getMessage());
		assertEquals("E", exception.getNodeId());
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

	@Test
	public void testRemoveNodeRemovesNodeAndEdgesFromGraphCorrectly() {
		assertFalse(testGraph.getNode("E").isEmpty());
		testGraph.removeNode("E");
		assertTrue(testGraph.getNode("E").isEmpty());
	}

	@Test
	public void testRemoveNodeThrowsNotFoundExceptionWhenNodeWithIdDNE() {
		NodeNotFoundException exception = assertThrows(NodeNotFoundException.class, () -> testGraph.removeNode("DNE"));
		assertEquals("Node was not found with the given id: DNE", exception.getMessage());
		assertEquals("DNE", exception.getNodeId());
	}
}
