package OptimizationAlgorithms;

/**
 * Applies each gradient directly with a fixed learning rate.
 */
public non-sealed class SGD implements Optimizer {
    private final double learningRate;

    /**
     * Sets the step size used for all gradient updates.
     *
     * @param learningRate the step size used during parameter updates
     */
    public SGD(double learningRate) {
        if (learningRate < 0 || learningRate > 1) {
            throw new IllegalArgumentException("Learning rate must be between 0 and 1");
        }
        this.learningRate = learningRate;
    }

    /**
     * Subtracts the scaled gradient from each parameter.
     *
     * @param weights  the parameter vector to be updated
     * @param gradient the computed gradient vector
     */
    @Override
    public void update(double[] weights, double[] gradient) {
        for (int i = 0; i < weights.length; i++) {
            weights[i] -= learningRate * gradient[i];
        }
    }
}
