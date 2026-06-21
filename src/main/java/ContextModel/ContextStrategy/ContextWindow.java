package ContextModel.ContextStrategy;

import SampleDataset.Pair;

import java.util.List;

/**
 * Interface for generating positive samples from a random walk
 * using a sliding window mechanism.
 */
public interface ContextWindow {
    /**
     * Generate positive (target, context) pairs from a walk.
     *
     * @param walk the generated random walk
     * @return list of (target, context) index pairs
     */
    List<Pair> generatePositivePairs(List<Integer> walk);

    /**
     * Returns the window size.
     *
     * @return the number of neighbors considered around the center element
     */
    int getWindowSize();
}
