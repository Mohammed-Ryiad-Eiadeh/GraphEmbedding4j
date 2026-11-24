package ActivationFunction;

/**
 * Sigmoid activation function that maps a real-valued score to (0, 1).
 */
public non-sealed class SigmoidFunction implements Activation {

    /**
     * Applies the sigmoid activation function.
     *
     * @param dotProduct input value
     * @return sigmoid output in the range (0, 1)
     */
    @Override
    public double applyAsDouble(double dotProduct) {
        return 1.0 / (1.0 + Math.exp(-dotProduct));
    }

    /**
     * Computes the sigmoid derivative using the activation output.
     *
     * @param dotProduct input value
     * @return sigmoid derivative
     */
    @Override
    public double derivative(double dotProduct) {
        return applyAsDouble(dotProduct) * (1 - applyAsDouble(dotProduct));
    }
}
