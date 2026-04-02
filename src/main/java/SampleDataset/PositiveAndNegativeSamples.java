package SampleDataset;

import ContextModel.ContextStrategy.ContextWindow;
import NegativeSamplingModel.SampleStrategy.NegativeSample;
import WalkModel.DeepWalk;

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
    private final boolean allowSampleDuplicate;
    private final Random random;

    /**
     * Constructs a positive and negative sample generator using walks
     * produced by the given DeepWalk model.
     *
     * @param deepWalk the DeepWalk model that provides the walk sequences
     * @param slidingWindow the context window used to extract positive samples
     * @param negativeSample the strategy used to generate negative samples
     * @param allowSampleDuplicate whether duplicate samples are allowed
     * @param randomSeed the seed used to initialize random sampling
     */
    public PositiveAndNegativeSamples(DeepWalk<V> deepWalk, ContextWindow slidingWindow, NegativeSample negativeSample, boolean allowSampleDuplicate, long randomSeed) {
        DeepWalk<V> deepWalks = Objects.requireNonNull(deepWalk, "walk cannot be null");
        this.slidingWindow = Objects.requireNonNull(slidingWindow, "symmetricSlidingWindow cannot be null");
        this.negativeSample = Objects.requireNonNull(negativeSample, "negativeSample cannot be null");
        this.random = new Random(randomSeed);
        this.sequences = new ArrayList<>(deepWalks.getRandomWalks());
        this.allowSampleDuplicate = allowSampleDuplicate;
    }

    /**
     * Generates shuffled positive and negative samples from random walks using graph-aware negative sampling.
     *
     * @return unmodifiable list of SampleDataset
     */
    public List<Sample> generatePositiveNegativeSampleDataset() {
        List<Sample> datasets = new ArrayList<>();
        sequences.removeIf(walk -> walk.size() < 2);
        for (ArrayList<Integer> walk : sequences) {
            List<Pair> positivePairs = slidingWindow.generatePositivePairs(walk);
            for (Pair positivePair : positivePairs) {
                String label = "1";
                Sample positiveSample = new Sample(positivePair.v1(), positivePair.v2(), label);
                datasets.add(positiveSample);
            }
            int slidingWindowSize = this.slidingWindow.getWindowSize();
            for (var target : walk) {
                List<Pair> negativePairs = negativeSample
                        .generatePositivePairs(target,
                        new HashSet<>(forbiddingNegatives(target, walk, slidingWindowSize)),
                        slidingWindowSize);
                for (Pair negativePair : negativePairs) {
                    String label = "0";
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
        int i = walk.indexOf(target);
        forbidding.add(target);
        for (int j = Math.max(0, i - windowSize); j <= Math.min(walk.size() - 1, i + windowSize); j++) {
            if (i != j) {
                forbidding.add(walk.get(j));
            }
        }
        return forbidding;
    }
}
