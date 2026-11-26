package ContextModel;

import PositiveNegativeSampling.TrainingPair;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts positive target-context pairs from a walk using a configurable direction and span.
 */
public non-sealed class SlidingWindow implements ContextWindow {
    private final int windowSize;
    private final WindowMode windowMode;

    /**
     * Configures which neighboring walk positions are treated as context.
     *
     * @param windowMode the window direction strategy (symmetric, left, or right)
     * @param windowSize the number of neighbors to include around the center element
     * @throws IllegalArgumentException if {@code windowSize <= 0} or {@code windowMode} is null
     */
    public SlidingWindow(WindowMode windowMode, int windowSize) {
        if  (windowSize <= 0) {
            throw new IllegalArgumentException("windowSize must be greater than 0");
        }

        if (windowMode == null) {
            throw new IllegalArgumentException("windowMode must not be null");
        }

        this.windowSize = windowSize;
        this.windowMode = windowMode;
    }

    /**
     * Reports the maximum distance from a target considered for context.
     *
     * @return the number of neighbors considered around the center element
     */
    @Override
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * Builds positive training pairs by treating each walk node as a target in turn.
     *
     * @param walk       the generated random walk
     * @return list of (target, context) index pairs
     */
    @Override
    public List<TrainingPair> generatePositivePairs(List<Integer> walk) {
        List<TrainingPair> positiveSamples = new ArrayList<>();

        for (int i = 0; i < walk.size(); i++) {
            int left = 0;
            int right = 0;

            switch (windowMode) {
                case Symmetric -> {
                    left = Math.max(0, i - windowSize);
                    right = Math.min(walk.size() - 1, i + windowSize);
                }
                case Left -> {
                    left = Math.max(0, i - windowSize);
                    right = i - 1;
                }
                case Right -> {
                    left = i + 1;
                    right = Math.min(walk.size() - 1, i + windowSize);
                }
            }

            for (int j = left; j <= right; j++) {
                positiveSamples.add(new TrainingPair(walk.get(i), walk.get(j)));
            }
        }

        return positiveSamples;
    }
}
