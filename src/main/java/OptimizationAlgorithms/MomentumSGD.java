package OptimizationAlgorithms;

import java.util.HashMap;

/**
 * Smooths stochastic gradient updates by carrying a decaying velocity between steps.
 */
public non-sealed class MomentumSGD implements Optimizer {
    private final double learningRate;
    private final double momentum;
    private final HashMap<double[], double[]> velocityVector;

    /**
     * Configures the step size and the fraction of prior velocity to retain.
     *
     * @param learningRate the learning rate used for parameter updates
     * @param momentum     the momentum coefficient in the range [0, 1)
     * @throws IllegalArgumentException if the learning rate or momentum
     *                                  values are outside valid ranges
     */
    public MomentumSGD(double learningRate, double momentum) {
        if (learningRate < 0 || learningRate > 1) {
            throw new IllegalArgumentException("Learning rate must be between 0 and 1");
        }
        this.learningRate = learningRate;

        if (momentum < 0 || momentum >= 1) {
            throw new IllegalArgumentException("Momentum must be in [0, 1)");
        }
        this.momentum = momentum;

        this.velocityVector = new HashMap<>();
    }

    /**
     * Accumulates the current gradient into velocity and applies the resulting step.
     *
     * @param weights  the parameter vector to be updated
     * @param gradient the computed gradient vector
     */
    @Override
    public void update(double[] weights, double[] gradient) {
        double[] velocity = velocityVector.computeIfAbsent(weights,
                V -> new double[weights.length]);

        for (int i = 0; i < weights.length; i++) {
            velocity[i] = momentum * velocity[i] + gradient[i];
            weights[i] -= learningRate * velocity[i];
        }
    }
}
