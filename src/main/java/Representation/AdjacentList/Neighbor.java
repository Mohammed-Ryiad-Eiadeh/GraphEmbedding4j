package Representation.AdjacentList;

/**
 * Stores an adjacent vertex index together with the connecting edge's weight.
 *
 * @param destination the destination vertex
 * @param weight the edge weight
 * @param <V> the vertex type
 */
public record Neighbor<V>(V destination, float weight) { }
