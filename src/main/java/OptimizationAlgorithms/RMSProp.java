package OptimizationAlgorithms;

import java.util.HashMap;

/**
 * RMSProp optimizer that adapts the learning rate per parameter
 * using an exponentially decaying average of squared gradients.
 */
public non-sealed class RMSProp implements Optimizer{
    private final double learningRate;
    private final double decayRate;
    private final double epsilon;
    private final HashMap<double[], double[]> cacheVector;

    /**
     * Constructs an RMSProp optimizer.
     *
     * @param learningRate the learning rate used for parameter updates
     * @param decayRate the exponential decay rate for squared gradients, in the range [0, 1)
     * @param epsilon the numerical stability constant, in the range [1e-12, 1e-6]
     * @throws IllegalArgumentException if learningRate, decayRate, or epsilon are outside valid ranges
     */
    public RMSProp(double learningRate, double decayRate, double epsilon) {
        if (learningRate < 0 || learningRate > 1) {
            throw new IllegalArgumentException("LearningModel rate must be between 0 and 1");
        }
        this.learningRate = learningRate;

        if (decayRate < 0 || decayRate >= 1) {
            throw new IllegalArgumentException("Decay rate must be in [0, 1)");
        }
        this.decayRate = decayRate;

        if (epsilon < 1e-12 || epsilon > 1e-6) {
            throw new IllegalArgumentException("Epsilon must be in [10^-6, 10^-12]");
        }
        this.epsilon = epsilon;

        this.cacheVector = new HashMap<>();
    }

    /**
     * Updates the model parameters using the computed gradient.
     *
     * @param weights  the parameter vector to be updated
     * @param gradient the computed gradient vector
     */
    @Override
    public void update(double[] weights, double[] gradient) {
        double[] cache = cacheVector.computeIfAbsent(weights,
                V -> new double[weights.length]);

        for (int i = 0; i < weights.length; i++) {
            cache[i] = decayRate * (cache[i]) + (1 - decayRate) * Math.pow(gradient[i], 2) ;
            weights[i] -= learningRate / Math.sqrt(cache[i] + epsilon) * gradient[i];  // ie-8 is to a void division by 0
        }
    }
}
