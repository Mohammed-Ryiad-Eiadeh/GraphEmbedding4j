package ActivationFunction;

/**
 * Converts a real-valued score to a probability-like output with the complementary log-log function.
 */
public non-sealed class CLogLogFunction implements Activation {

    /**
     * Transforms a score into its complementary log-log activation output.
     *
     * @param dotProduct target-context score
     * @return activation output in the range (0, 1)
     */
    @Override
    public double applyAsDouble(double dotProduct) {
        return 1 - Math.exp(-Math.exp(dotProduct));
    }

    /**
     * Computes the slope of the complementary log-log function at the supplied score.
     *
     * @param dotProduct target-context score
     * @return the activation derivative
     */
    @Override
    public double derivative(double dotProduct) {
        return Math.exp(dotProduct - Math.exp(dotProduct));
    }
}
