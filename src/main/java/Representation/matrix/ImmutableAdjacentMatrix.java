package Representation.matrix;

import Core.Edge;
import Core.ImmutableGraphData;

import java.util.Map;
import java.util.Set;

/**
 * Converts a graph snapshot into a cached dense matrix of edge weights.
 *
 * @param <V> vertex type
 */
public class ImmutableAdjacentMatrix<V> extends MatrixRepresentation<float[][]> {
    private final ImmutableGraphData<V> immutableGraphData;
    private final Map<V, Integer> vertexToIndex;
    private volatile float[][] cachedMatrix;


    /**
     * Associates the graph snapshot with the index of each matrix row and column.
     *
     * @param immutableGraphData the immutable graph snapshot
     * @param vertexToIndex mapping from vertices to integer indices
     */
    public ImmutableAdjacentMatrix(ImmutableGraphData<V> immutableGraphData, Map<V, Integer> vertexToIndex) {
        this.immutableGraphData = immutableGraphData;

        this.vertexToIndex = Map.copyOf(vertexToIndex);
    }

    /**
     * Builds or returns the matrix whose entries are edge weights, with zero for absent edges.
     *
     * @return adjacency matrix
     */
    @Override
    public float[][] getMatrix() {
        float[][] local = cachedMatrix;
        if (local != null) {
            return local;
        }

        int vertexSize = immutableGraphData.vertexCount();
        float[][] matrix = new float[vertexSize][vertexSize];

        Set<Edge<V>> edgeSet = immutableGraphData.edgeSet();

        for (Edge<V> edge : edgeSet) {
            int source = this.vertexToIndex.get(edge.source());
            int destination = this.vertexToIndex.get(edge.destination());
            float weight = edge.weight();
            matrix[source][destination] = weight;
        }

        cachedMatrix = matrix;
        return matrix;
    }
}
