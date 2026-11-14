package ActivationFunction;

import java.util.function.DoubleUnaryOperator;

/**
 * Activation function applied to the dot product of target and context embeddings.
 */
public sealed interface Activation extends DoubleUnaryOperator
        permits SigmoidFunction, TanhFunction { }
