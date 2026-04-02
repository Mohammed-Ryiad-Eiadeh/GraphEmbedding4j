package Core;

import java.util.*;

/**
 * Builder class for constructing a graph using an edge-list representation.
 * Supports directed and bidirectional graphs and allows incremental
 * addition of weighted and unweighted connections.
 *
 * @param <V> the vertex type
 */
public class GraphBuilder<V> {
    private final GraphType graphType;
    private final Set<Edge<V>> edgesSet;
    private final Set<V> vertexSet;

    /**
     * Creates a GraphBuilder with the specified graph type.
     *
     * @param graphType the type of graph (directed or bidirectional)
     */
    public GraphBuilder(GraphType graphType) {
        this.graphType = Objects.requireNonNull(graphType);
        this.vertexSet = new LinkedHashSet<>();
        this.edgesSet = new LinkedHashSet<>();
    }

    /**
     * Adds a weighted connection between two vertices.
     *
     * @param source the source vertex
     * @param destination the destination vertex
     * @param weight the edge weight
     */
    public void addConnection(V source, V destination, float weight) {
        vertexSet.add(source);
        vertexSet.add(destination);

        switch (graphType) {
            case Directed -> edgesSet.add(new Edge<>(source, destination, weight));
            case BiDirectional -> {
                edgesSet.add(new Edge<>(source, destination, weight));
                edgesSet.add(new Edge<>(destination, source, weight));
            }
        }
    }

    /**
     * Check whether the graph is empty or not
     * A graph G={V, E, W} is empty if it has empty set of nodes
     * Throw an illegal state exception if the graph is empty
     *
     * @return an object of the graph builder if it is not empty
     */
    public GraphBuilder<V> ifNotEmpty() {
        if (vertexSet.isEmpty()) {
            throw new IllegalStateException("Graph has no vertices, so it is empty");
        } else {
            return this;
        }
    }

    /**
     * Builds an immutable snapshot of the graph constructed so far.
     * <p>
     * This method freezes the current state of the builder and returns a
     * read-only graph representation that cannot be modified. The returned
     * snapshot is safe to share across representers, algorithms, and other
     * consumers without risk of accidental mutation.
     * <p>
     * Subsequent modifications to this builder will not affect the returned
     * graph snapshot.
     *
     * @return an immutable GraphData snapshot of the constructed graph
     */
    public ImmutableGraphData<V> build() {
        return new ImmutableGraphData<>(graphType,
                Collections.unmodifiableSet(vertexSet),
                Collections.unmodifiableSet(edgesSet));
    }
}
