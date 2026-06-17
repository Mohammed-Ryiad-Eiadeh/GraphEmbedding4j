package LearningModel.OptimizationAlgorithms;

/**
 * Defines the contract for optimization algorithms used to update model
 * parameters during embedding training.
 */
public sealed interface Optimizer permits SGD, MomentumSGD, RMSProp, AdaGrad, AdaDelta {

    /**
     * Updates the model parameters using the computed gradient.
     *
     * @param weights the parameter vector to be updated
     * @param gradient the computed gradient vector
     */
    void update(double[] weights, double[] gradient);
}
