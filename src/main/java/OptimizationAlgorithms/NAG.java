package OptimizationAlgorithms;

import java.util.HashMap;

/**
 * Nesterov Accelerated Gradient (NAG) — momentum-based optimizer
 * that evaluates gradients at a look-ahead position for faster convergence.
 */
public non-sealed class NAG implements Optimizer {
    private final double learningRate;
    private final double momentum;
    private final HashMap<double[], double[]> velocityVector;

    /**
     * Creates a NAG optimizer.
     *
     * @param learningRate step size
     * @param momentum momentum coefficient
     */
    public NAG(double learningRate, double momentum) {
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
     * Updates model parameters using Nesterov Accelerated Gradient.
     *
     * @param weights model parameters
     * @param gradient gradient vector
     */
    @Override
    public void update(double[] weights, double[] gradient) {
        double[] velocity = velocityVector.computeIfAbsent(weights,
                V -> new double[weights.length]);

        double[] look_Ahead = new  double[gradient.length];

        for (int i = 0; i < weights.length; i++) {
            look_Ahead[i] = gradient[i] - momentum * velocity[i];
            velocity[i] = momentum * velocity[i] + learningRate * look_Ahead[i];
            gradient[i] = gradient[i] - velocity[i];
        }
    }
}
