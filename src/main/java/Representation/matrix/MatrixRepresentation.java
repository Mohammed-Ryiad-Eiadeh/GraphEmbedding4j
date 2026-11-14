package Representation.matrix;

/**
 * Base class for graph representations backed by a matrix.
 *
 * @param <Mat> the matrix type
 */
public abstract class MatrixRepresentation<Mat> {
    /**
     * Returns the matrix representation of the graph.
     *
     * @return the graph matrix
     */
    public abstract Mat getMatrix();
}
