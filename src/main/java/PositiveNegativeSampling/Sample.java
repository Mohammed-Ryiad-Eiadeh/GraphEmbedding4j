package PositiveNegativeSampling;

/**
 * Carries one target-context pair and its positive or negative training label.
 *
 * @param targetNode the target vertex
 * @param contextNode the context vertex
 * @param label the label of this sample
 */
public record Sample(int targetNode, int contextNode, String label) { }
