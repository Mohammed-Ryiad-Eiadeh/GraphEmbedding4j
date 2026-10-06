package WalkModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A sampling strategy used by random‑walk–based embedding models.
 * <p>
 * This sealed interface defines shared sampling behavior for algorithms
 * such as {@code MB2Vec} and {@code Node2Vec}.
 * Implementations provide their own walk-generation logic, but all may
 * rely on the same roulette‑wheel (weighted random) sampling mechanism.
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

        double commutative = 0.0;

        for (Map.Entry<Integer, Double> entry : neighborsToBiasRatio.entrySet()) {
            commutative += entry.getValue();

            if (rand <= commutative) {
                return entry.getKey();
            }
        }

        throw  new IllegalStateException("no more roulette wheel sample");
    }
}
