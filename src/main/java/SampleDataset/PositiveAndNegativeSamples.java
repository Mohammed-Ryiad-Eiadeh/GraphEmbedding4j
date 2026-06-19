package SampleDataset;

import ContextModel.ContextWindow;
import NegativeSamplingModel.SampleStrategy.NegativeSample;
import WalkModel.WalkStrategy;

import java.util.*;

/**
 * Generates positive and negative training samples from random walk sequences
 * over an immutable graph structure for node embedding algorithms.
 *
 * @param <V> The vertex (node) type of the graph
 */
public class PositiveAndNegativeSamples<V> {
    private final ArrayList<ArrayList<Integer>> sequences;
    private final ContextWindow slidingWindow;
    private final NegativeSample negativeSample;
    private final int numberOfNegativeSamples;
    private final boolean allowSampleDuplicate;
    private final Random random;

    /**
     * Constructs a positive and negative sample generator using walks
     * produced by the given DeepWalk model.
     *
     * @param walkStrategy the Walk model that provides the walk sequences
     * @param slidingWindow the context window used to extract positive samples
     * @param negativeSample the strategy used to generate negative samples
     * @param numberOfNegativeSamples the number of negative samples
     * @param allowSampleDuplicate whether duplicate samples are allowed
     * @param randomSeed the seed used to initialize random sampling
     */
    public PositiveAndNegativeSamples(WalkStrategy<V> walkStrategy, ContextWindow slidingWindow, NegativeSample negativeSample, int numberOfNegativeSamples, boolean allowSampleDuplicate, long randomSeed) {
        WalkStrategy<V> walkModel = Objects.requireNonNull(walkStrategy, "walk cannot be null");

        this.slidingWindow = Objects.requireNonNull(slidingWindow, "symmetricSlidingWindow cannot be null");

        this.negativeSample = Objects.requireNonNull(negativeSample, "negativeSample cannot be null");

        if (numberOfNegativeSamples < 0) {
            throw new IllegalArgumentException("Number of negative samples must be greater than zero");
        } else if (numberOfNegativeSamples > 20) {
            throw new IllegalArgumentException("Number of negative samples must be at most 20");
        }
        this.numberOfNegativeSamples = numberOfNegativeSamples;

        this.random = new Random(randomSeed);
        this.sequences = new ArrayList<>(walkModel.getRandomWalks());
        this.allowSampleDuplicate = allowSampleDuplicate;
    }

    /**
     * Generates shuffled positive and negative samples from random walks using graph-aware negative sampling.
     *
     * @return unmodifiable list of SampleDataset
     */
    public List<Sample> generatePositiveNegativeSampleDataset() {
        List<Sample> datasets = new ArrayList<>();
        for (ArrayList<Integer> walk : sequences) {
            List<Pair> positivePairs = slidingWindow.generatePositivePairs(walk);

            for (Pair positivePair : positivePairs) {
                String label = "Positive Sample";
                Sample positiveSample = new Sample(positivePair.v1(), positivePair.v2(), label);
                datasets.add(positiveSample);
            }

            int slidingWindowSize = this.slidingWindow.getWindowSize();
            for (var target : walk) {
                List<Pair> negativePairs = negativeSample.generateNegativePairs(target,
                        new HashSet<>(forbiddingNegatives(target, walk, slidingWindowSize)),
                        numberOfNegativeSamples);

                for (Pair negativePair : negativePairs) {
                    String label = "Negative Sample";
                    Sample negativeSample = new Sample(negativePair.v1(), negativePair.v2(), label);
                    datasets.add(negativeSample);
                }
            }
        }
        if (!allowSampleDuplicate) {
            datasets = new ArrayList<>(new LinkedHashSet<>(datasets));
        }
        Collections.shuffle(datasets, random);
        return datasets;
    }

    /**
     * Computes the set of nodes that must be excluded from negative sampling
     * for a given target within a sliding window of a walk.
     *
     * @return nodes forbidden for negative sampling of a target.
     */
    private List<Integer> forbiddingNegatives(int target, List<Integer> walk, int windowSize) {
        List<Integer> forbidding = new ArrayList<>();
        for (int i = 0; i < walk.size(); i++) {
            if (Objects.equals(target, walk.get(i))) {
                int from = Math.max(0, i - windowSize);
                int to = Math.min(walk.size() - 1, i + windowSize);

                for (int j = from; j <= to; j++) {
                    if (i != j) {
                        forbidding.add(walk.get(j));
                    }
                }
            }
        }
        return forbidding;
    }
}