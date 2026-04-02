package Core;

import java.util.Set;

/**
 * Read-only abstraction of a graph.
 * <p>
 * This interface defines the essential structural properties of a graph
 * (vertices, edges, and graph type) independent of how the graph is
 * constructed, stored, or represented.
 * <p></p>
 * Implementations may represent immutable snapshots, lazy views,
 * or adapters over external graph sources.
 * @param <V> the vertex type
 */
public interface GraphData<V> {
    /**
     * Returns the type of the graph (directed or bidirectional).
     *
     * @return the graph type
     */
    GraphType graphType();

    /**
     * Returns the simple name of the representer implementation.
     *
     * @return the representer class name
     */
    String getClassName();

    /**
     * Returns the set of vertices in the graph.
     *
     * @return the set of vertices
     */
    Set<V> vertexSet();

    /**
     * Returns the set of edges in the graph.
     *
     * @return the set of edges
     */
    Set<Edge<V>> edgeSet();

    /**
     * Returns the number of vertices in the graph.
     *
     * @return the vertex count
     */
    default int vertexCount() {
        return vertexSet().size();
    }

    /**
     * Returns the number of edges in the graph.
     *
     * @return the edge count
     */
    default int edgeCount() {
        return edgeSet().size();
    }

    /**
     * Checks whether a directed edge exists from v1 to v2.
     *
     * @param v1 the source vertex
     * @param v2 the destination vertex
     * @return true if an edge v1 -> v2 exists, false otherwise
     */
    default boolean isNeighbor(V v1, V v2) {
            for (Edge<V> edge : edgeSet()) {
                if (edge.source() == v1 && edge.destination() == v2) {
                    return true;
                }
            }
            return false;
    }
}



