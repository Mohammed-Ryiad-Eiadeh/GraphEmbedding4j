package SampleDataset;

/**
 * Immutable representation of a positive negative samples triple.
 *
 * @param targetNode the target vertex
 * @param contextNode the context vertex
 * @param label the label of this sample
 */
public record Sample(int targetNode, int contextNode, String label) { }
