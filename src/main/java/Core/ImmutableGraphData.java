package Core;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable snapshot of a graph's structure.
 * <p>
 * Thread-safe and safe to share across algorithms and random walks.
 * </p>
 */
public record ImmutableGraphData<V>(GraphType graphType, Set<V> vertexSet,
                                    Set<Edge<V>> edgeSet) implements GraphData<V> {
    /**
     * Constructs an immutable graph data instance.
     *
     * @param graphType the graph structure type
     * @param vertexSet the set of vertices (defensively copied)
     * @param edgeSet   the set of edges (defensively copied)
     * @throws NullPointerException if any argument is null
     */
    public ImmutableGraphData(GraphType graphType, Set<V> vertexSet, Set<Edge<V>> edgeSet) {
        this.graphType = Objects.requireNonNull(graphType);
        this.vertexSet = Collections.unmodifiableSet(new LinkedHashSet<>(vertexSet));
        this.edgeSet = Collections.unmodifiableSet(new LinkedHashSet<>(edgeSet));
    }

    /**
     * Returns the name of the graph type as a string.
     *
     * @return graph type name
     */
    @Override
    public String getClassName() {
        return graphType.name();
    }

    /**
     * Returns a human-readable string representation of the graph data.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "ImmutableGraphData[" +
                "graphType=" + graphType + ", " +
                "vertexSet=" + vertexSet + ", " +
                "edgeSet=" + edgeSet + ']';
    }

    /**
     * Returns the structural type of the graph.
     *
     * @return graph type
     */
    @Override
    public GraphType graphType() {
        return graphType;
    }

    /**
     * Returns the immutable set of vertices.
     *
     * @return vertex set
     */
    @Override
    public Set<V> vertexSet() {
        return vertexSet;
    }

    /**
     * Returns the immutable set of edges.
     *
     * @return edge set
     */
    @Override
    public Set<Edge<V>> edgeSet() {
        return edgeSet;
    }

}
