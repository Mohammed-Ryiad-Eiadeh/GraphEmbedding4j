package Representation.matrix;

/**
 * Interface for graph representers that expose a Matrix Representation.
 *
 * @param <Mat> the matrix type
 */
public abstract class MatrixData<Mat> {
    /**
     * Returns the matrix Representation of the graph.
     *
     * @return the graph matrix
     */
    public abstract Mat getMatrix();
}
