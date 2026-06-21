package OptimizationAlgorithms;

import java.util.HashMap;

/**
 * Momentum-based Stochastic Gradient Descent (Momentum SGD) optimizer.
 * <p>
 * This optimizer improves standard SGD by accumulating a velocity vector
 * that stores the weighted moving average of previous updates.
 * The momentum term helps accelerate convergence and reduces oscillations
 * during optimization.
 */
public non-sealed class MomentumSGD implements Optimizer {
    private final double learningRate;
    private final double momentum;
    private final HashMap<double[], double[]> velocityVector;

    /**
     * Constructs a Momentum SGD optimizer.
     *
     * @param learningRate the learning rate used for parameter updates
     * @param momentum     the momentum coefficient in the range [0, 1)
     * @throws IllegalArgumentException if the learning rate or momentum
     *                                  values are outside valid ranges
     */
    public MomentumSGD(double learningRate, double momentum) {
        if (learningRate < 0 || learningRate > 1) {
            throw new IllegalArgumentException("LearningModel rate must be between 0 and 1");
        }
        this.learningRate = learningRate;

        if (momentum < 0 || momentum >= 1) {
            throw new IllegalArgumentException("Momentum must be in [0, 1)");
        }
        this.momentum = momentum;

        this.velocityVector = new HashMap<>();
    }

    /**
     * Updates the model parameters using the computed gradient.
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
