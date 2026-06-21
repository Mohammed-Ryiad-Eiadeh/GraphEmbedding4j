package representation.AdjacentList.AdjacentListModel;

/**
 * Immutable representation of a weighted edge.
 *
 * @param destination the destination vertex
 * @param weight the edge weight
 * @param <V> the vertex type
 */
public record Neighbor<V>(V destination, float weight) { }
