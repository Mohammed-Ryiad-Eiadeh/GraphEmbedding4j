package ActivationFunction;

/**
 * Softplus activation function that maps a real-valued score to (0, ∞).
 */
public non-sealed class SoftplusFunction implements Activation {

    /**
     * Applies the activation function to the input value.
     *
     * @param operand input value
     * @return activated output
     */
    @Override
    public double applyAsDouble(double operand) {
        return Math.log(1 + Math.exp(operand));
    }
}
