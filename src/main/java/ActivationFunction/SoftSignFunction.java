package ActivationFunction;

/**
 * SoftSign activation function that maps a real-valued score to (-1, 1).
 */
public non-sealed class SoftSignFunction implements Activation {

    /**
     * Applies the activation function to the input value.
     *
     * @param operand input value
     * @return activated output
     */
    @Override
    public double applyAsDouble(double operand) {
        return operand / (1 + Math.abs(operand));
    }
}
