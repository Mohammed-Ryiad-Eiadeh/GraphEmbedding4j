package SamplingModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Shared sampling utilities for biased random-walk strategies.
 * <p>
 * Provides roulette-wheel (weighted random) sampling used by MB2Vec and Node2Vec.
 * </p>
 */
public sealed interface SamplingStrategy permits MB2Vec, Node2Vec {

    /**
     * Performs roulette‑wheel (weighted random) sampling over a set of candidate nodes.
     * <p>
     * Each candidate node has an associated bias weight. The probability of selecting
     * a node is proportional to its weight:
     *
     * <pre>
     *     P(node_i) = weight_i / Σ(weight_j)
     * </pre>
     *
     * This method:
     * <ol>
     *   <li>Computes the total weight.</li>
     *   <li>Draws a random value in [0, total).</li>
     *   <li>Iterates through candidates, accumulating weights until the
     *       cumulative sum exceeds the random value.</li>
     * </ol>
     *
     * @param neighborsToBiasRatio mapping from candidate node IDs to their bias weights
     * @param randSample random generator used for sampling
     * @return the selected node ID
     *
     * @throws IllegalStateException if no candidate can be selected
     *         (e.g., empty map or all weights are zero)
     */
    default int rouletteWheelSample(HashMap<Integer, Double> neighborsToBiasRatio, Random randSample) {
        double total = neighborsToBiasRatio.values().stream().mapToDouble(Double::doubleValue).sum();

        double rand = randSample.nextDouble() * total;

        double cumulative = 0.0;

        for (Map.Entry<Integer, Double> entry : neighborsToBiasRatio.entrySet()) {
            cumulative += entry.getValue();

            if (rand <= cumulative) {
                return entry.getKey();
            }
        }

        throw  new IllegalStateException("no more roulette wheel sample");
    }
}
