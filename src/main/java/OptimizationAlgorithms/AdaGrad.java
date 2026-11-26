package OptimizationAlgorithms;

import java.util.HashMap;

/**
 * Shrinks each parameter's step according to its accumulated squared gradients.
 */
public non-sealed class AdaGrad implements Optimizer {
    private final double learningRate;
    private final double epsilon;
    private final HashMap<double[], double[]> accumulatorVector;

    /**
     * Sets the global step size and numerical stability value for AdaGrad updates.
     *
     * @param learningRate the learning rate used for parameter updates
     * @param epsilon the numerical stability constant, in the range [1e-12, 1e-6]
     * @throws IllegalArgumentException if learningRate or epsilon are outside valid ranges
     */
    public AdaGrad(double learningRate, double epsilon) {
        if (learningRate < 0 || learningRate > 1) {
            throw new IllegalArgumentException("Learning rate must be between 0 and 1");
        }
        this.learningRate = learningRate;

        if (epsilon < 1e-12 || epsilon > 1e-6) {
            throw new IllegalArgumentException("Epsilon must be in [1e-12, 1e-6]");
        }
        this.epsilon = epsilon;

        this.accumulatorVector = new HashMap<>();
    }

    /**
     * Accumulates squared gradients and scales each parameter update by its history.
     *
     * @param weights  the parameter vector to be updated
     * @param gradient the computed gradient vector
     */
    @Override
    public void update(double[] weights, double[] gradient) {
        double[] accumulator = accumulatorVector.computeIfAbsent(weights,
                V -> new double[gradient.length]);

        for (int i = 0; i < weights.length; i++) {
            accumulator[i] += Math.pow(gradient[i], 2);
            weights[i] -= learningRate / Math.sqrt(accumulator[i] + epsilon) * gradient[i];
        }
    }
}
