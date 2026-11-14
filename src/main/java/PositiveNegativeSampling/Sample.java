package PositiveNegativeSampling;

/**
 * A single labeled training sample: a (target, context) pair with a label.
 *
 * @param targetNode the target vertex
 * @param contextNode the context vertex
 * @param label the label of this sample
 */
public record Sample(int targetNode, int contextNode, String label) { }
