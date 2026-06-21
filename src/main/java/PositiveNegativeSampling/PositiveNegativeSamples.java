package PositiveNegativeSampling;

import ContextModel.ContextWindow;
import WalkModel.WalkStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Generates positive and negative training samples from random walk sequences
 * over an immutable graph structure for node embedding algorithms.
 *
 * @param <V> The vertex (node) type of the graph
 */
public class PositiveNegativeSamples<V> {
    private final ArrayList<ArrayList<Integer>> sequences;
    private final ContextWindow slidingWindow;
    private final NegativeSampler negativeSampler;
    private final int numberOfNegativeSamples;
    private final boolean allowSampleDuplicate;
    private final Random random;

    /**
     * Constructs a positive and negative sample generator using walks
     * produced by the given DeepWalk model.
     *
     * @param walkStrategy the Walk model that provides the walk sequences
     * @param slidingWindow the context window used to extract positive samples
     * @param negativeSampler the strategy used to generate negative samples
     * @param numberOfNegativeSamples the number of negative samples
     * @param allowSampleDuplicate whether duplicate samples are allowed
     * @param randomSeed the seed used to initialize random sampling
     */
    public PositiveNegativeSamples(WalkStrategy<V> walkStrategy, ContextWindow slidingWindow, NegativeSampler negativeSampler, int numberOfNegativeSamples, boolean allowSampleDuplicate, long randomSeed) {
        WalkStrategy<V> walkModel = Objects.requireNonNull(walkStrategy, "walk cannot be null");

        this.slidingWindow = Objects.requireNonNull(slidingWindow, "symmetricSlidingWindow cannot be null");

        this.negativeSampler = Objects.requireNonNull(negativeSampler, "negativeSampler cannot be null");

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
    public List<Sampler> generatePositiveNegativeSampleDataset() {
        List<Sampler> datasets = new ArrayList<>();
        for (ArrayList<Integer> walk : sequences) {
            List<TrainingPair> positiveTrainingPairs = slidingWindow.generatePositivePairs(walk);

            for (TrainingPair positiveTrainingPair : positiveTrainingPairs) {
                String label = "Positive PositiveNegativeSampling";
                Sampler positiveSampler = new Sampler(positiveTrainingPair.v1(), positiveTrainingPair.v2(), label);
                datasets.add(positiveSampler);
            }

            int slidingWindowSize = this.slidingWindow.getWindowSize();
            for (var target : walk) {
                List<TrainingPair> negativeTrainingPairs = negativeSampler.generateNegativePairs(target,
                        new HashSet<>(forbiddingNegatives(target, walk, slidingWindowSize)),
                        numberOfNegativeSamples);

                for (TrainingPair negativeTrainingPair : negativeTrainingPairs) {
                    String label = "Negative PositiveNegativeSampling";
                    Sampler negativeSampler = new Sampler(negativeTrainingPair.v1(), negativeTrainingPair.v2(), label);
                    datasets.add(negativeSampler);
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