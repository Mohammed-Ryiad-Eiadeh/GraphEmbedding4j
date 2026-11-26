package Core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Assigns each graph vertex a stable contiguous index and supports lookups in both directions.
 *
 * @param <V> the vertex type
 */
public class VertexIndexMapping<V> {
    private final Map<V, Integer> vertexToIndex;
    private final List<V> indexToVertex;

    /**
     * Assigns indices in the order vertices appear in the graph data.
     */
    public VertexIndexMapping(GraphData<V> GraphData) {
        indexToVertex = List.copyOf(Objects.requireNonNull(GraphData).vertexSet());

        Map<V, Integer> mapper = new HashMap<>();

        for (int index = 0; index < indexToVertex.size(); index++) {
            mapper.putIfAbsent(indexToVertex.get(index), index);
        }

        vertexToIndex = Collections.unmodifiableMap(mapper);
    }

    /**
     * Exposes the read-only lookup from vertices to their assigned indices.
     *
     * @return vertex to index map
     */
    public Map<V, Integer> getVertexToIndex() {
        return vertexToIndex;
    }

    /**
     * Resolves a graph vertex to its assigned integer index.
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
     * Resolves an integer index back to its graph vertex.
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
