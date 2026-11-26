package ActivationFunction;

import java.util.function.DoubleUnaryOperator;

/**
 * Defines how a target-context score becomes an output and how its gradient is computed.
 */
public sealed interface Activation extends DoubleUnaryOperator
        permits SigmoidFunction, CLogLogFunction {

    /**
     * Returns the activation's local derivative for the supplied operand.
     *
     * @param operand value used to evaluate the derivative
     * @return the local derivative
     */
    double derivative(double operand);
}
