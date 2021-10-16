package Reacher.utils;

import com.google.common.collect.Multimap;
import org.apache.commons.math3.util.Pair;
import org.ejml.simple.SimpleMatrix;

import java.util.List;

public class GraphUtils {

	public static SimpleMatrix constructAdjacencyMatrix(List<Integer> nodes, Multimap<Integer, Integer> edges) {

		double[][] data = new double[nodes.size()][nodes.size()];

		edges.asMap().entrySet().stream()
				.flatMap(entry -> entry.getValue().stream()
						.map(val -> Pair.create(entry.getKey(), val)))
				.forEach(pair -> data[pair.getKey()][pair.getValue()] = 1);

		return new SimpleMatrix(data);

	}
}
