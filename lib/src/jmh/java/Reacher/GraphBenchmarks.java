package Reacher;

import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static Reacher.utils.BenchmarkUtils.generateRandomGraph;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(1)
public class GraphBenchmarks {

	@State(Scope.Thread)
	public static class BenchmarkState
	{
		Graph g;
		Random random;

		@Param({"1000", "5000", "10000"})
		int numVertices;

		@Setup(Level.Trial)
		public void initialize() {
			random = new Random();
			g = generateRandomGraph(numVertices, numVertices);
		}
	}

	/*@Benchmark
	public void benchmarkDoesPathExist(BenchmarkState state) {
		String from = Integer.toString(state.random.nextInt(3000));
		String to = Integer.toString(state.random.nextInt(3000));

		state.g.doesPathExist(from, to);
	}*/

	@Benchmark
	public void benchmarkGetAncestors(BenchmarkState state) {
		state.g.getAncestors(Integer.toString(state.random.nextInt(state.numVertices)));
	}
}