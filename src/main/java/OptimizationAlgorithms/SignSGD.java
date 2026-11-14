package OptimizationAlgorithms;

/**
 * Sign SGD optimizer.
 * <p>
 * Updates each parameter by moving it in the opposite direction of the
 * gradient's sign, scaled by a fixed learning rate.
 */
public non-sealed class SignSGD implements Optimizer {
    private final double learningRate;

    /**
     * Constructs a SignSGD optimizer with the given learning rate.
     *
     * @param learningRate the step size used during parameter updates
     */
    public SignSGD(double learningRate) {
        if (learningRate < 0 || learningRate > 1) {
            throw new IllegalArgumentException("LearningModel rate must be between 0 and 1");
        }
        this.learningRate = learningRate;
    }

    /**
     * Updates the model parameters using the computed gradient.
     *
     * @param weights  the parameter vector to be updated
     * @param gradient the computed gradient vector
     */
    @Override
    public void update(double[] weights, double[] gradient) {
        for (int i = 0; i < weights.length; i++) {
            weights[i] -= learningRate * Math.signum(gradient[i]);
        }
    }
}

