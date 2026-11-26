package Core;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Collects vertices and weighted connections, then produces an immutable graph snapshot.
 *
 * @param <V> the vertex type
 */
public class GraphBuilder<V> {
    private final GraphType graphType;
    private final Set<Edge<V>> edgesSet;
    private final Set<V> vertexSet;

    /**
     * Starts a graph builder with the requested edge directionality.
     *
     * @param graphType the type of graph (directed or bidirectional)
     */
    public GraphBuilder(GraphType graphType) {
        this.graphType = Objects.requireNonNull(graphType);
        this.vertexSet = new LinkedHashSet<>();
        this.edgesSet = new LinkedHashSet<>();
    }

    /**
     * Adds both endpoint vertices and records their weighted connection.
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
     * Ensures construction has added at least one vertex before continuing.
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
     * Creates a read-only graph snapshot from the connections collected so far.
     * Later builder changes do not alter this snapshot.
     *
     * @return an immutable GraphData snapshot of the constructed graph
     */
    public ImmutableGraphData<V> build() {
        return new ImmutableGraphData<>(graphType,
                Collections.unmodifiableSet(vertexSet),
                Collections.unmodifiableSet(edgesSet));
    }
}
