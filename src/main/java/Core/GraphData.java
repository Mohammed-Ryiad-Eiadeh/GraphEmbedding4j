package Core;

import java.util.Objects;
import java.util.Set;

/**
 * Exposes a graph's vertices, edges, and directionality without prescribing storage.
 *
 * @param <V> the vertex type
 */
public sealed interface GraphData<V> permits ImmutableGraphData {

    /**
     * Identifies whether graph connections are directed or bidirectional.
     *
     * @return the graph type
     */
    GraphType graphType();

    /**
     * Provides the graph type name used to identify its representation.
     *
     * @return the representation type name
     */
    String getClassName();

    /**
     * Exposes the vertices that belong to this graph.
     *
     * @return the set of vertices
     */
    Set<V> vertexSet();

    /**
     * Exposes the connections that belong to this graph.
     *
     * @return the set of edges
     */
    Set<Edge<V>> edgeSet();

    /**
     * Counts the vertices exposed by this graph view.
     *
     * @return the vertex count
     */
    default int vertexCount() {
        return vertexSet().size();
    }

    /**
     * Counts the edges exposed by this graph view.
     *
     * @return the edge count
     */
    default int edgeCount() {
        return edgeSet().size();
    }

    /**
     * Tests whether the graph contains the directed connection from {@code v1} to {@code v2}.
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



