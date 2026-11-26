package OptimizationAlgorithms;

/**
 * Defines the parameter-update operation shared by embedding optimizers.
 */
public sealed interface Optimizer permits AdaDelta, AdaGrad, AdaMax, AdamW, MomentumSGD, RMSProp, SGD, SignSGD, NAG {

    /**
     * Adjusts a parameter vector using its gradient and the optimizer's update rule.
     *
     * @param weights the parameter vector to be updated
     * @param gradient the computed gradient vector
     */
    void update(double[] weights, double[] gradient);
}
