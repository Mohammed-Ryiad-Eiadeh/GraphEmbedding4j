package representation.matrix.AdjacentMatrixModel;

/**
 * Interface for graph representers that expose a Matrix representation.
 *
 * @param <Mat> the matrix type
 */
public abstract class MatrixData<Mat> {
    /**
     * Returns the matrix representation of the graph.
     *
     * @return the graph matrix
     */
    public abstract Mat getMatrix();
}
