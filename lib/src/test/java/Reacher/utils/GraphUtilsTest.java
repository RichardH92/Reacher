package Reacher.utils;

import org.junit.jupiter.api.Test;

import static Reacher.utils.GraphUtils.buildIdentityMatrix;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphUtilsTest {

	@Test
	public void testConstructIdentityMatrixHappyPath() {

		int n = 10;
		var matrix = buildIdentityMatrix(n);

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j) {
					assertEquals(1, matrix.get(i, j));
				} else {
					assertEquals(0, matrix.get(i, j));
				}
			}
		}
	}
}
