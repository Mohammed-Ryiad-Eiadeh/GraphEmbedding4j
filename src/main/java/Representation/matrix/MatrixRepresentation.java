package Representation.matrix;

/**
 * Defines access to a graph represented by a matrix of values.
 *
 * @param <Mat> the matrix type
 */
public abstract class MatrixRepresentation<Mat> {
    /**
     * Exposes the matrix used to represent the graph.
     *
     * @return the graph matrix
     */
    public abstract Mat getMatrix();
}
