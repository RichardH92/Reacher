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
				new Node(1),
				new Node(2),
				new Node(3),
				new Node(4),
				new Node(5)
		);

		var builder = new GraphBuilder();

		testNodes.forEach(builder::addNode);

		builder.addEdge(1, 2);
		builder.addEdge(2, 3);
		builder.addEdge(1, 4);
		builder.addEdge(3, 5);
		builder.addEdge(4, 5);

		testGraph = builder.build();
	}

	@Test
	public void testGetDescendantsHappyPath() {
		List<INode> descendants = testGraph.getDescendants(2);
		List<INode> expected = ImmutableList.of(
				testNodes.get(2),
				testNodes.get(4)
		);

		assertEquals(expected, descendants);
	}

	@Test
	public void testGetDescendantsDedupesNodesWhenDescendantViaMultiplePaths() {
		List<INode> descendants = testGraph.getDescendants(1);
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
		assertTrue(testGraph.getDescendants(5).isEmpty());
	}

	@Test
	public void testGetDescendantsThrowsNotFoundExceptionWhenNodeWithIdDNE() {
		NodeNotFoundException exception = assertThrows(NodeNotFoundException.class, () -> testGraph.getAncestors(1000));
		assertEquals("Node was not found with the given id: 1000", exception.getMessage());
		assertEquals(1000, exception.getNodeId());
	}

	@Test
	public void testDoesPathExistHappyPath() {
		assertTrue(testGraph.doesPathExist(1, 5));
		assertTrue(testGraph.doesPathExist(1, 2));
		assertTrue(testGraph.doesPathExist(1, 4));

		assertFalse(testGraph.doesPathExist(5, 1));

		assertFalse(testGraph.doesPathExist(4, 3));
		assertFalse(testGraph.doesPathExist(3, 4));
	}

	@Test
	public void testDoesPathExistThrowsNotFoundExceptionWhenNodeWithIdDNE() {
		NodeNotFoundException exception = assertThrows(NodeNotFoundException.class, () -> testGraph.doesPathExist(1000, 1000));
		assertEquals("Node was not found with the given id: 1000", exception.getMessage());
		assertEquals(1000, exception.getNodeId());
	}

	@Test
	public void testGetAncestorsThrowsNotFoundExceptionWhenNodeWithIdDNE() {
		NodeNotFoundException exception = assertThrows(NodeNotFoundException.class, () -> testGraph.getAncestors(1000));
		assertEquals("Node was not found with the given id: 1000", exception.getMessage());
		assertEquals(1000, exception.getNodeId());
	}

	@Test
	public void testGetAncestorsThrowsNotFoundExceptionWhenNodePreviouslyExisted() {
		testGraph.removeNode(5);

		NodeNotFoundException exception = assertThrows(NodeNotFoundException.class, () -> testGraph.getAncestors(5));
		assertEquals("Node was not found with the given id: 5", exception.getMessage());
		assertEquals(5, exception.getNodeId());
	}

	@Test
	public void testGetAncestorsReturnsCorrectlyHappyPath() {
		List<INode> ancestors = testGraph.getAncestors(3);

		List<INode> expected = ImmutableList.of(
				testNodes.get(0),
				testNodes.get(1)
		);

		assertEquals(expected, ancestors);
	}

	@Test
	public void testGetAncestorsDedupesWhenNodeIsAncestorViaMultiplePaths() {
		List<INode> ancestors = testGraph.getAncestors(5);

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
		assertTrue(testGraph.getAncestors(1).isEmpty());
	}

	@Test
	public void testRemoveNodeRemovesNodeAndEdgesFromGraphCorrectly() {

		var builder = new GraphBuilder();

		// add every node except E
		testNodes.subList(0, testNodes.size() - 1).forEach(builder::addNode);

		builder.addEdge(1, 2);
		builder.addEdge(2, 3);
		builder.addEdge(1, 4);

		var expectedGraph = builder.build();

		testGraph.removeNode(5);
		assertEquals(expectedGraph, testGraph);
	}

	@Test
	public void testRemoveNodeThrowsNotFoundExceptionWhenNodeWithIdDNE() {
		NodeNotFoundException exception = assertThrows(NodeNotFoundException.class, () -> testGraph.removeNode(1000));
		assertEquals("Node was not found with the given id: 1000", exception.getMessage());
		assertEquals(1000, exception.getNodeId());
	}

	@Test
	public void testRemoveNodeThrowsExceptionWhenNodeIsNotALeaf() {
		// TODO
	}

	@Test
	public void testAddEdgeHappyPath() {

		var expectedGraph = testGraph.toBuilder()
				.addEdge(2, 4)
				.build();

		assertFalse(testGraph.doesPathExist(2, 4));
		// check B is not an ancestor of E
		assertFalse(testGraph.getAncestors(4).stream().map(INode::getId).anyMatch(id -> id == 2));

		testGraph.addEdge(2, 4);

		assertTrue(testGraph.doesPathExist(2, 4));
		assertTrue(testGraph.getAncestors(4).stream().map(INode::getId).anyMatch(id -> id == 2));

		assertEquals(expectedGraph, testGraph);
	}
}
