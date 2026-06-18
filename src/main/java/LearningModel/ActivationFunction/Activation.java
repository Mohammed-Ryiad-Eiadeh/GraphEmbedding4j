package LearningModel.ActivationFunction;

import java.util.function.DoubleUnaryOperator;

/**
 * Defines an activation function used in embedding learning models.
 */
public sealed interface Activation extends DoubleUnaryOperator
        permits SigmoidFunction, TanhFunction { }
