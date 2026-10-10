
package PositiveNegativeSampling;

import ContextModel.ContextWindow;
import WalkModel.WalkStrategy;

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

/**
 * Generates positive and negative training samples from random
 * walk sequences for Skip-Gram node embedding models.
 *
 * @param <V> the vertex type used in the input graph
 */
public class PositiveNegativeSamples<V> {

    private static final String POSITIVE_LABEL = "Positive PositiveNegativeSampling";
    private static final String NEGATIVE_LABEL = "Negative PositiveNegativeSampling";
    private final ArrayList<ArrayList<Integer>> sequences;
    private final ContextWindow slidingWindow;
    private final NegativeSampler negativeSampler;
    private final int numberOfNegativeSamples;
    private final boolean allowSampleDuplicate;
    private final Random random;

    /**
     * Constructs a positive and negative sample generator.
     *
     * @param walkStrategy random walk generation strategy
     * @param slidingWindow context window used for positive pairs
     * @param negativeSampler negative sampling strategy
     * @param numberOfNegativeSamples negatives per positive pair
     * @param allowSampleDuplicate whether repeated pairs are retained
     * @param randomSeed random seed for reproducibility
     */
    public PositiveNegativeSamples(WalkStrategy<V> walkStrategy, ContextWindow slidingWindow, NegativeSampler negativeSampler, int numberOfNegativeSamples, boolean allowSampleDuplicate, long randomSeed) {
        WalkStrategy<V> walkModel = Objects.requireNonNull(walkStrategy, "walkStrategy cannot be null");

        this.slidingWindow = Objects.requireNonNull(slidingWindow, "slidingWindow cannot be null");

        this.negativeSampler = Objects.requireNonNull(negativeSampler, "negativeSampler cannot be null");

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
    public List<Sampler> generatePositiveNegativeSampleDataset() {
        List<Sampler> datasets = new ArrayList<>();

        for (ArrayList<Integer> walk : sequences) {
            List<TrainingPair> positiveTrainingPairs = slidingWindow.generatePositivePairs(walk);

            // Collect positive contexts for each target node.
            Map<Integer, Set<Integer>> positiveContexts = new HashMap<>();

            for (TrainingPair pair : positiveTrainingPairs) {
                positiveContexts
                        .computeIfAbsent(
                                pair.v1(),
                                key -> new HashSet<>())
                        .add(pair.v2());
            }

            // Generate samples for each positive pair.
            for (TrainingPair positivePair : positiveTrainingPairs) {
                int target = positivePair.v1();
                int context = positivePair.v2();

                // Add positive sample.
                datasets.add(new Sampler(target, context, POSITIVE_LABEL));

                if (numberOfNegativeSamples == 0) {
                    continue;
                }

                // Exclude positive contexts and target node.
                Set<Integer> forbidden = new HashSet<>(
                        positiveContexts.getOrDefault(
                                target,
                                Collections.emptySet()));
                forbidden.add(target);

                // Generate negatives per positive pair.
                List<TrainingPair> negativeTrainingPairs =
                        negativeSampler.generateNegativePairs(
                                target,
                                forbidden,
                                numberOfNegativeSamples);

                for (TrainingPair negativePair : negativeTrainingPairs) {
                    datasets.add(new Sampler(
                            negativePair.v1(),
                            negativePair.v2(),
                            NEGATIVE_LABEL));
                }
            }
        }

        // Remove duplicates only when explicitly requested.
        if (!allowSampleDuplicate) {
            datasets = new ArrayList<>(
                    new LinkedHashSet<>(datasets));
        }

        // Shuffle training samples.
        Collections.shuffle(datasets, random);

        return datasets;
    }
}
