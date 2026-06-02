package LearningModel.ActivationFunction;

/**
 * Sigmoid activation function used to map a real-valued score
 * into the probability range (0, 1).
 */
public class SigmoidFunction implements Activation {

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
