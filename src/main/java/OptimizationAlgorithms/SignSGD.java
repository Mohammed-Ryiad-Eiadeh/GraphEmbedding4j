package OptimizationAlgorithms;

/**
 * Moves each parameter opposite the gradient sign using a fixed step size.
 */
public non-sealed class SignSGD implements Optimizer {
    private final double learningRate;

    /**
     * Sets the step size applied to each gradient sign.
     *
     * @param learningRate the step size used during parameter updates
     */
    public SignSGD(double learningRate) {
        if (learningRate < 0 || learningRate > 1) {
            throw new IllegalArgumentException("Learning rate must be between 0 and 1");
        }
        this.learningRate = learningRate;
    }

    /**
     * Adjusts each parameter by the signed direction of its gradient.
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

