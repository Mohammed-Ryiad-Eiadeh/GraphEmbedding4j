package PositiveNegativeSampling;

import java.util.List;
import java.util.Set;

/**
 * Strategy for generating negative (target, context) samples
 * that are not true neighbors in the random walk.
 */
public sealed interface NegativeSample permits UniformNegativeSample {

    /**
     * Samples K negative (target, context) pairs for the given target,
     * excluding any forbidden nodes (e.g., target itself + window contexts).
     *
     * @param target the target node
     * @param forbidden nodes that must not be sampled as negatives
     * @param numOfNegativeSamples number of negatives to sample
     *
     * @return list of negative pairs (target, negativeContext)
     */
    List<TrainingPair> generateNegativePairs(int target, Set<Integer> forbidden, int numOfNegativeSamples);
}
