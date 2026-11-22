package ActivationFunction;

/**
 * Mish activation function that maps a real-valued score to (−0.3088, ∞).
 */
public non-sealed class MishFunction implements Activation {

    /**
     * Applies the activation function to the input value.
     *
     * @param operand input value
     * @return activated output
     */
    @Override
    public double applyAsDouble(double operand) {
        return operand * Math.tanh(Math.log(1 + Math.exp(operand)));
    }
}
