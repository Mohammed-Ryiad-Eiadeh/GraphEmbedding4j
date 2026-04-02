package ContextModel;

import ContextModel.ContextStrategy.ContextWindow;
import ContextModel.ContextStrategy.WindowMode;
import SampleDataset.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates symmetric context pairs from a random walk using a sliding window,
 * where either or both forward and backward neighbors are treated as context.
 */
public class SlidingWindow implements ContextWindow {
    private final int windowSize;
    private final WindowMode windowMode;;

    /**
     * Constructs a symmetric sliding window.
     *
     * @param windowSize the number of neighbors to include on both the left
     *                   and right sides of the center element
     * @throws IllegalArgumentException if {@code windowSize <= 0}
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
    public List<Pair> generatePositivePairs(List<Integer> walk) {
        List<Pair> positiveSamples = new ArrayList<>();

        if (windowMode == WindowMode.Symmetric) {
            for (int i = 0; i < walk.size(); i++) {
                int left = Math.max(0, i - windowSize);
                int right = Math.min(walk.size() - 1, i + windowSize);

                for (int j = left; j <= right; j++) {
                    if (i != j) {
                        positiveSamples.add(new Pair(walk.get(i), walk.get(j)));
                    }
                }
            }
        }
        else if (windowMode == WindowMode.Left) {
            for (int i = 0; i < walk.size(); i++) {
                int left = Math.max(0, i - windowSize);
                int right = i - 1;

                for (int j = left; j <= right; j++) {
                        positiveSamples.add(new Pair(walk.get(i), walk.get(j)));
                }
            }
        }
        else if (windowMode == WindowMode.Right) {
            for (int i = 0; i < walk.size(); i++) {
                int left = i + 1;
                int right = Math.min(walk.size() - 1, i + windowSize);

                for (int j = left; j <= right; j++) {
                        positiveSamples.add(new Pair(walk.get(i), walk.get(j)));
                }
            }
        }
        return positiveSamples;
    }
}
