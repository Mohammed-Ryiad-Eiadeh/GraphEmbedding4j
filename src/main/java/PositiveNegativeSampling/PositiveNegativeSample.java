package PositiveNegativeSampling;

import ContextModel.ContextWindow;
import SamplingModel.AbstractWalkingStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Produces labeled positive and negative (target, context) training samples
 * from random walk sequences for Skip-Gram embedding models.
 *
 * @param <V> the vertex type used in the input graph
 */
public class PositiveNegativeSample<V> {
    private static final String POSITIVE_LABEL = "Positive PositiveNegativeSampling";
    private static final String NEGATIVE_LABEL = "Negative PositiveNegativeSampling";
    private final ArrayList<ArrayList<Integer>> sequences;
    private final ContextWindow slidingWindow;
    private final NegativeSample negativeSample;
    private final int numberOfNegativeSamples;
    private final boolean allowSampleDuplicate;
    private final Random random;

    /**
     * Constructs a positive and negative sample generator.
     *
     * @param abstractWalkStrategy random walk generation strategy
     * @param slidingWindow context window used for positive pairs
     * @param negativeSample negative sampling strategy
     * @param numberOfNegativeSamples negatives per positive pair
     * @param allowSampleDuplicate whether repeated pairs are retained
     * @param randomSeed random seed for reproducibility
     */
    public PositiveNegativeSample(AbstractWalkingStrategy<V> abstractWalkStrategy, ContextWindow slidingWindow, NegativeSample negativeSample, int numberOfNegativeSamples, boolean allowSampleDuplicate, long randomSeed) {
        AbstractWalkingStrategy<V> walkModel = Objects.requireNonNull(abstractWalkStrategy, "abstractWalkStrategy cannot be null");

        this.slidingWindow = Objects.requireNonNull(slidingWindow, "slidingWindow cannot be null");

        this.negativeSample = Objects.requireNonNull(negativeSample, "negativeSample cannot be null");

        if (numberOfNegativeSamples < 0 || numberOfNegativeSamples > 20) {
            throw new IllegalArgumentException("Number of negative samples must be between 0 and 20");
        }

        this.numberOfNegativeSamples = numberOfNegativeSamples;
        this.allowSampleDuplicate = allowSampleDuplicate;
        this.random = new Random(randomSeed);
        this.sequences = new ArrayList<>(walkModel.getRandomWalks());
    }

    /**
     * Generates positive and negative samples.
     *
     * For each positive pair:
     * 1. Add the positive pair.
     * 2. Exclude the target and known positive contexts.
     * 3. Generate negative samples.
     *
     * @return shuffled training samples
     */
    public List<Sample> generatePositiveNegativeSampleDataset() {
        List<Sample> datasets = sequences.parallelStream().map(walk -> {
            List<Sample> localDataset = new ArrayList<>();

            List<TrainingPair> positiveTrainingPairs = slidingWindow.generatePositivePairs(walk);

            // Collect positive contexts for each target node.
            Map<Integer, Set<Integer>> positiveContexts = new HashMap<>();

            for (TrainingPair pair : positiveTrainingPairs) {
                positiveContexts.computeIfAbsent(pair.v1(), key -> new HashSet<>()).add(pair.v2());
            }

            // Include the target node in its forbidden set.
            for (Map.Entry<Integer, Set<Integer>> entry : positiveContexts.entrySet()) {
                entry.getValue().add(entry.getKey());
            }

            // Generate samples for each positive pair.
            for (TrainingPair positivePair : positiveTrainingPairs) {

                int target = positivePair.v1();
                int context = positivePair.v2();

                // Add positive sample.
                localDataset.add(new Sample(target, context, POSITIVE_LABEL));

                if (numberOfNegativeSamples == 0) {
                    continue;
                }

                // Reuse the forbidden set for this target.
                Set<Integer> forbidden = positiveContexts.get(target);

                // Generate negative samples.
                List<TrainingPair> negativeTrainingPairs = negativeSample.generateNegativePairs(target, forbidden, numberOfNegativeSamples);

                for (TrainingPair negativePair : negativeTrainingPairs) {
                    localDataset.add(new Sample(negativePair.v1(), negativePair.v2(), NEGATIVE_LABEL));
                }
            }

            // Remove duplicates only when explicitly requested.
            if (!allowSampleDuplicate) {
                localDataset = new ArrayList<>(new LinkedHashSet<>(localDataset));
            }

            // Shuffle training samples.
            Collections.shuffle(localDataset, random);

            return localDataset;

        }).flatMap(List::stream).collect(Collectors.toList());

        return datasets;
    }
}