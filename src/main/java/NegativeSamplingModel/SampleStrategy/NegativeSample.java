package NegativeSamplingModel.SampleStrategy;

import SampleDataset.Pair;

import java.util.List;
import java.util.Set;

/**
 * Interface for generating negative samples from a random walk
 * using a sliding window mechanism.
 */
public interface NegativeSample {
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
    List<Pair> generatePositivePairs(int target, Set<Integer> forbidden, int numOfNegativeSamples);
}
