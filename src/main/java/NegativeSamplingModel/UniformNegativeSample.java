package NegativeSamplingModel;

import Core.VertexIndexMapping;
import NegativeSamplingModel.SampleStrategy.NegativeSample;
import SampleDataset.Pair;

import java.util.*;

public class UniformNegativeSample<V> implements NegativeSample {
    private final Random random;
    private final int graphSize;

    /**
     * Creates a generator for negative samples based on uniform distribution of the graph nodes.
     *
     * @param mapper Mapping between vertices and integer indices
     *
     * @throws IllegalArgumentException if sequences is empty
     */
    public UniformNegativeSample(VertexIndexMapping<V> mapper) {
        VertexIndexMapping<V> mapping = Objects.requireNonNull(mapper, "mapper cannot be null");
        this.graphSize = mapping.getVertexToIndex().size();
        this.random = new Random();
    }

    /**
     * Samples K negative (target, context) pairs for the given target,
     * excluding any forbidden nodes (e.g., target itself + window contexts).
     *
     * @param target               the target node
     * @param forbidden            nodes that must not be sampled as negatives
     * @param numOfNegativeSamples number of negatives to sample
     * @return list of negative pairs (target, negativeContext)
     */
    @Override
    public List<Pair> generatePositivePairs(int target, Set<Integer> forbidden, int numOfNegativeSamples) {
        List<Pair> negativePairs = new ArrayList<>();
        while (negativePairs.size() < numOfNegativeSamples) {
            int potentialNegativeSampleNode = random.nextInt(this.graphSize);
            if (!forbidden.contains(potentialNegativeSampleNode)) {
                negativePairs.add(new Pair(target, potentialNegativeSampleNode));
            }
        }
        return negativePairs;
    }
}
