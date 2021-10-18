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

		@Setup(Level.Trial)
		public void initialize() {
			random = new Random();
			g = generateRandomGraph(1000, 1000);
		}
	}

	@Benchmark
	public void benchmarkGetAncestors(BenchmarkState state) {
		state.g.getAncestors(Integer.toString(state.random.nextInt(1000)));
	}
}