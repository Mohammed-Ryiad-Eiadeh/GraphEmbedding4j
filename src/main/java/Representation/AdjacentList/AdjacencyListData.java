package Representation.AdjacentList;

import java.util.List;
import java.util.Map;

/**
 * Base class for graph representations backed by an adjacency list.
 *
 * @param <T> the vertex type
 */
public abstract class AdjacencyListData<T> {
    /**
     * Returns the adjacency list of the graph.
     * Each vertex is mapped to a list of outgoing edges.
     *
     * @return a map representing the adjacency list
     */
    public abstract Map<T, List<Neighbor<T>>> getAdjacentMap();
}
