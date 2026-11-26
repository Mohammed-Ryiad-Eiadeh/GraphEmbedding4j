package ContextModel;

import PositiveNegativeSampling.TrainingPair;

import java.util.List;

/**
 * Defines how nearby nodes in a random walk become positive training pairs.
 */
public sealed interface ContextWindow permits SlidingWindow {

    /**
     * Extracts the target-context relationships represented by one walk.
     *
     * @param walk the generated random walk
     * @return list of (target, context) index pairs
     */
    List<TrainingPair> generatePositivePairs(List<Integer> walk);

    /**
     * Reports how many walk positions may contribute context around each target.
     *
     * @return the number of neighbors considered around the center element
     */
    int getWindowSize();
}
