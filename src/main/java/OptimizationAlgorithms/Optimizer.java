package OptimizationAlgorithms;

/**
 * Optimization algorithm that updates embedding vectors given computed gradients.
 */
public sealed interface Optimizer permits AdaDelta, AdaGrad, AdaMax, AdamW, MomentumSGD, RMSProp, SGD, SignSGD, NAG {

    /**
     * Updates the model parameters using the computed gradient.
     *
     * @param weights the parameter vector to be updated
     * @param gradient the computed gradient vector
     */
    void update(double[] weights, double[] gradient);
}
