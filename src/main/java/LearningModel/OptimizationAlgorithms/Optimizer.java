package LearningModel.OptimizationAlgorithms;

/**
 * Defines the contract for optimization algorithms used to update model
 * parameters during embedding training.
 */
public interface Optimizer {

    /**
     * Updates the model parameters using the computed gradient.
     *
     * @param nodeID the identity of the given node
     * @param weights the parameter vector to be updated
     * @param gradient the computed gradient vector
     */
    void update(int nodeID, double[] weights, double[] gradient);
}
