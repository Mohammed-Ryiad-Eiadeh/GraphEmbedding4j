package Core;

/**
 * Immutable representation of a weighted edge.
 *
 * @param source the source vertex
 * @param destination the destination vertex
 * @param weight the edge weight
 * @param <V> the vertex type
 */
public record Edge<V> (V source, V destination, float weight) { }
