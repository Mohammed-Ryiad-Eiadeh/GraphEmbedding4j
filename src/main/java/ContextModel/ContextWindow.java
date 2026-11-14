package ContextModel;

import PositiveNegativeSampling.TrainingPair;

import java.util.List;

/**
 * Generates positive (target, context) pairs from random walks
 * using a sliding window around each node.
 */
public sealed interface ContextWindow permits SlidingWindow {

    /**
     * Generate positive (target, context) pairs from a walk.
     *
     * @param walk the generated random walk
     * @return list of (target, context) index pairs
     */
    List<TrainingPair> generatePositivePairs(List<Integer> walk);

    /**
     * Returns the window size.
     *
     * @return the number of neighbors considered around the center element
     */
    int getWindowSize();
}
