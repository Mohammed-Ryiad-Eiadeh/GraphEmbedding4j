package SamplingModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Provides weighted candidate selection shared by biased random-walk strategies.
 */
public sealed interface SamplingStrategy permits MB2Vec, Node2Vec {

    /**
     * Selects one candidate with probability proportional to its assigned weight.
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
