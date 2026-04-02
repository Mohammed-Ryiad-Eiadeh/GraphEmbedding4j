package representation.matrix;

import Core.Edge;
import Core.ImmutableGraphData;
import representation.matrix.AdjacentMatrixModel.MatrixData;

import java.util.Map;
import java.util.Set;

/**
 * Immutable adjacency matrix representation built from a graph snapshot.
 *
 * @param <V> vertex type
 */
public class ImmutableAdjacentMatrix<V> extends MatrixData<float[][]> {
    private final ImmutableGraphData<V> immutableGraphData;
    private final Map<V, Integer> vertexToIndex;
    private volatile float[][] cachedMatrix;


    public ImmutableAdjacentMatrix(ImmutableGraphData<V> immutableGraphData, Map<V, Integer> vertexToIndex) {
        this.immutableGraphData = immutableGraphData;
        this.vertexToIndex = Map.copyOf(vertexToIndex);
    }

    /**
     * Returns the dense adjacency matrix.
     * A[i][j] = weight of edge (vi → vj), or 0 if absent.
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
