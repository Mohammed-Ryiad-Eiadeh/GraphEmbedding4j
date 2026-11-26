package Core;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Holds an immutable, shareable snapshot of a graph's vertices, edges, and type.
 */
public record ImmutableGraphData<V>(GraphType graphType, Set<V> vertexSet, Set<Edge<V>> edgeSet) implements GraphData<V> {
    /**
     * Copies the supplied graph components into an immutable snapshot.
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
     * Supplies a string identifier for the graph's directionality.
     *
     * @return graph type name
     */
    @Override
    public String getClassName() {
        return graphType.name();
    }

    /**
     * Formats the graph type, vertices, and edges for diagnostics.
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
     * Exposes the directionality captured by this snapshot.
     *
     * @return graph type
     */
    @Override
    public GraphType graphType() {
        return graphType;
    }

    /**
     * Exposes the snapshot's unmodifiable vertex set.
     *
     * @return vertex set
     */
    @Override
    public Set<V> vertexSet() {
        return vertexSet;
    }

    /**
     * Exposes the snapshot's unmodifiable edge set.
     *
     * @return edge set
     */
    @Override
    public Set<Edge<V>> edgeSet() {
        return edgeSet;
    }

}
