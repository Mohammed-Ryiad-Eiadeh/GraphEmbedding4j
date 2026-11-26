package Representation.AdjacentList;

import java.util.List;
import java.util.Map;

/**
 * Defines access to a graph stored as per-vertex neighbor lists.
 *
 * @param <T> the vertex type
 */
public abstract class AdjacencyListData<T> {
    /**
     * Exposes each vertex's outgoing neighbors and their edge weights.
     *
     * @return a map representing the adjacency list
     */
    public abstract Map<T, List<Neighbor<T>>> getAdjacentMap();
}
