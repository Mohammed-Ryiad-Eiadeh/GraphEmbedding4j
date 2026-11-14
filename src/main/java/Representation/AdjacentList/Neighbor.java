package Representation.AdjacentList;

/**
 * A weighted neighbor in an adjacency list.
 *
 * @param destination the destination vertex
 * @param weight the edge weight
 * @param <V> the vertex type
 */
public record Neighbor<V>(V destination, float weight) { }
