package PositiveNegativeSampling;

import Core.VertexIndexMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public non-sealed class UniformNegativeSampler<V> implements NegativeSampler {
    private final int graphSize;

    /**
     * Creates a generator for negative samples using a uniform distribution
     * over the graph vertices.
     *
     * @param mapper mapping between vertices and integer indices
     * @param seed seed used to initialize the random generator for negative sampling
     *
     * @throws NullPointerException if {@code mapper} is {@code null}
     */
    public UniformNegativeSampler(VertexIndexMapping<V> mapper) {
        VertexIndexMapping<V> mapping = Objects.requireNonNull(mapper, "mapper cannot be null");
        this.graphSize = mapping.getVertexToIndex().size();
    }

    /**
     * Samples K negative (target, context) pairs for the given target based on candidate based sampling,
     * excluding any forbidden nodes (e.g., target itself + window contexts).
     *
     * @param target               the target node
     * @param forbidden            nodes that must not be sampled as negatives
     * @param numOfNegativeSamples number of negatives to sample
     * @return list of negative pairs (target, negativeContext)
     */
    @Override
    public List<TrainingPair> generateNegativePairs(int target, Set<Integer> forbidden, int numOfNegativeSamples) {
        Objects.requireNonNull(forbidden, "forbidden cannot be null");

        List<Integer> negativeSamplesCandidate = new ArrayList<>();

        for (int node = 0; node < graphSize; node++) {
            if (node != target && !forbidden.contains(node))
                negativeSamplesCandidate.add(node);
        }

        Collections.shuffle(negativeSamplesCandidate, new Random(12345L));

        int limit = Math.min(negativeSamplesCandidate.size(), numOfNegativeSamples);

        List<TrainingPair> negativeTrainingPairs = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            negativeTrainingPairs.add(new TrainingPair(target, negativeSamplesCandidate.get(i)));
        }

        return negativeTrainingPairs;
    }
}
