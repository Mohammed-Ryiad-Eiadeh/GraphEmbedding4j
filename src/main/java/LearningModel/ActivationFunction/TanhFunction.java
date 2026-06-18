package LearningModel.ActivationFunction;

/**
 * Tanh activation function used to map a real-valued score
 * into the range (-1, 1).
 */
public non-sealed class TanhFunction implements Activation {

    /**
     * Applies the activation function to the input value.
     *
     * @param operand input value
     * @return activated output
     */
    @Override
    public double applyAsDouble(double operand) {
        return Math.tanh(operand);
    }
}
