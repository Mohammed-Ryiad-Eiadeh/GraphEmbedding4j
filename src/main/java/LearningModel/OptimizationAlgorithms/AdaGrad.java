package LearningModel.OptimizationAlgorithms;

import java.util.HashMap;

/**
 * AdaGrad optimizer.
 * <p>
 * This optimizer maintains a moving accumulative of squared gradients
 * and uses it to adapt the learning rate for each parameter.
 */
public non-sealed class AdaGrad implements Optimizer {
    private final double learningRate;
    private final double epsilon;
    private final HashMap<double[], double[]> accumulatorVector;

    /**
     * Constructs an AdaGrad optimizer.
     *
     * @param learningRate the learning rate used for parameter updates
     * @param epsilon      the epsilon coefficient in the range
     * @throws IllegalArgumentException if the learning rate or momentum
     *                                  values are outside valid ranges
     */
    public AdaGrad(double learningRate, double epsilon) {
        if (learningRate < 0 || learningRate > 1) {
            throw new IllegalArgumentException("Learning rate must be between 0 and 1");
        }
        this.learningRate = learningRate;

        if (epsilon < 1e-12 || epsilon > 1e-6) {
            throw new IllegalArgumentException("Epsilon must be in [10^-6, 10^-12]");
        }
        this.epsilon = epsilon;

        this.accumulatorVector = new HashMap<>();
    }

    /**
     * Updates the model parameters using the computed gradient.
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
