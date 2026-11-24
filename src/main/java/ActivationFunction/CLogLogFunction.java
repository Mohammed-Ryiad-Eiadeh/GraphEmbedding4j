package ActivationFunction;

/**
 * CLogLog activation function that maps a real-valued score to (0, 1).
 */
public non-sealed class CLogLogFunction implements Activation {

    /**
     * Applies the CLogLog activation function.
     *
     * @param  dotProduct value
     * @return sigmoid output in the range (0, 1)
     */
    @Override
    public double applyAsDouble(double dotProduct) {
        return 1 - Math.exp(-Math.exp(dotProduct));
    }

    /**
     * Computes the CLogLog derivative using the activation output.
     *
     * @param dotProduct previously computed CLogLog output
     * @return CLogLog derivative
     */
    @Override
    public double derivative(double dotProduct) {
        return Math.exp(dotProduct - Math.exp(dotProduct));
    }
}
