package Core;

import java.util.*;

/**
 * Provides a bidirectional mapping between graph vertices and
 * contiguous integer indices [0 ... n-1].
 *
 * <p>The mapping preserves the iteration order of the underlying
 * graph vertex set and is immutable after construction.</p>
 *
 * @param <V> the vertex type
 */
public class VertexIndexMapping<V> {
    private final Map<V, Integer> vertexToIndex;
    private final List<V> indexToVertex;

    /**
     * Constructs an index mapping from immutable graph data.
     *
     * @param immutableGraphData the graph snapshot containing ordered vertices
     */
    public VertexIndexMapping(ImmutableGraphData<V> immutableGraphData) {
        indexToVertex = List.copyOf(Objects.requireNonNull(immutableGraphData).vertexSet());
        Map<V, Integer> mapper = new HashMap<>();
        for (int index = 0; index < indexToVertex.size(); index++) {
            mapper.putIfAbsent(indexToVertex.get(index), index);
        }
        vertexToIndex = Collections.unmodifiableMap(mapper);
    }

    /**
     * Returns the immutable vertex to index mapping.
     *
     * @return vertex to index map
     */
    public Map<V, Integer> getVertexToIndex() {
        return vertexToIndex;
    }

    /**
     * Returns the index associated with a given vertex.
     *
     * @param vertex the vertex to look up
     * @return vertex index
     * @throws IllegalArgumentException if the vertex does not exist
     */
    public int indexForVertex(V vertex) {
        if (!vertexToIndex.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex " + vertex + " does not exist");
        }
        return vertexToIndex.get(vertex);
    }

    /**
     * Returns the vertex associated with a given index.
     *
     * @param index the vertex index
     * @return vertex at the given index
     * @throws IllegalArgumentException if the index is out of bounds
     */
    public V getVertex(int index) {
        if (index < 0 || index >= indexToVertex.size()) {
            throw new IllegalArgumentException("Index " + index + " is out of bounds");
        }
        return indexToVertex.get(index);
    }
}
