package ActivationFunction;

/**
 * Sigmoid activation function that maps a real-valued score to (0, 1).
 */
public non-sealed class SigmoidFunction implements Activation {

    /**
     * Applies the activation function to the input value.
     *
     * @param operand input value
     * @return activated output
     */
    @Override
    public double applyAsDouble(double operand) {
        return 1.0 / (1.0 + Math.exp(-operand));
    }
}
