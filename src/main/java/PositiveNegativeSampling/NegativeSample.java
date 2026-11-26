package PositiveNegativeSampling;

import java.util.List;
import java.util.Set;

/**
 * Defines how non-context nodes are selected as negative training examples.
 */
public sealed interface NegativeSample permits UniformNegativeSample {

    /**
     * Selects negative contexts for one target while excluding known positive or forbidden nodes.
     *
     * @param target the target node
     * @param forbidden nodes that must not be sampled as negatives
     * @param numOfNegativeSamples number of negatives to sample
     *
     * @return list of negative pairs (target, negativeContext)
     */
    List<TrainingPair> generateNegativePairs(int target, Set<Integer> forbidden, int numOfNegativeSamples);
}
