package Core;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Incremental builder for constructing a graph from weighted edges.
 * Supports directed and bidirectional graphs.
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
     *
     * @return object from GraphBuilder to allow chaining
     */
    public GraphBuilder<V> addConnection(V source, V destination, float weight) {
        Objects.requireNonNull(source, "Source vertex cannot be null");
        Objects.requireNonNull(destination, "Destination vertex cannot be null");

        vertexSet.add(source);
        vertexSet.add(destination);

        switch (graphType) {
            case Directed -> {
                edgesSet.add(new Edge<>(source, destination, weight));
            }
            case BiDirectional -> {
                edgesSet.add(new Edge<>(source, destination, weight));
                edgesSet.add(new Edge<>(destination, source, weight));
            }
        }

        return this;
    }

    /**
     * Validates that the graph contains at least one vertex.
     *
     * @return this builder if the graph is non-empty
     * @throws IllegalStateException if no vertices have been added
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
     * read-only graph Representation that cannot be modified. The returned
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
