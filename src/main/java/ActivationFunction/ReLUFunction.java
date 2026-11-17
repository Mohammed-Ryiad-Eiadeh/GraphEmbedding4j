package ActivationFunction;

/**
 * ReLU activation function that maps a real-valued score to (0, x).
 */
public non-sealed class ReLUFunction implements Activation {

    /**
     * Applies the activation function to the input value.
     *
     * @param operand input value
     * @return activated output
     */
    @Override
    public double applyAsDouble(double operand) {
        return Math.max(0, operand);
    }
}
