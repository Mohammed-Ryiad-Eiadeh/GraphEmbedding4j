package ContextModel;

import PositiveNegativeSampling.TrainingPair;

import java.util.ArrayList;
import java.util.List;

/**
 * Sliding window that extracts (target, context) pairs from a random walk,
 * supporting symmetric, left-only, and right-only window modes.
 */
public non-sealed class SlidingWindow implements ContextWindow {
    private final int windowSize;
    private final WindowMode windowMode;;

    /**
     * Constructs a sliding window with the specified mode and size.
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
     * Returns the window size.
     *
     * @return the number of neighbors considered around the center element
     */
    @Override
    public int getWindowSize() {
        return windowSize;
    }

    /**
     * Generate positive (target, context) pairs from a walk.
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
