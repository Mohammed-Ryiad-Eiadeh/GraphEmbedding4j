package Core;

import java.util.Objects;
import java.util.Set;

/**
 * Read-only view of a graph's structure (vertices, edges, and type),
 * independent of its internal representation.
 *
 * @param <V> the vertex type
 */
public sealed interface GraphData<V> permits ImmutableGraphData {

    /**
     * Returns the type of the graph (directed or bidirectional).
     *
     * @return the graph type
     */
    GraphType graphType();

    /**
     * Returns the name of the graph representation type.
     *
     * @return the representation type name
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
                if (Objects.equals(edge.source(), v1) && Objects.equals(edge.destination(), v2)) {
                    return true;
                }
            }
            return false;
    }
}



