package LowRankApproximation;

import Representation.matrix.MatrixRepresentation;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

/**
 * Uses SVD components to represent a matrix with its leading dimensions.
 *
 * @param <Mat> the type of the underlying matrix representation element
 */
public class SimpleSVDApproximation<Mat> {
    private final int K_Dimension;
    private final SimpleSVD<SimpleMatrix> simpleSVD;

    /**
     * Computes the matrix's SVD and records how many leading dimensions to use for embeddings.
     *
     * @param matrix      the matrix to decompose
     * @param kDimension  target rank for low-rank approximation (0 ≤ k ≤ columns)
     * @throws IllegalArgumentException if kDimension is out of bounds
     */
    public SimpleSVDApproximation(MatrixRepresentation<Mat> matrix, int kDimension) {
        float[][] floatMatrix = (float[][]) matrix.getMatrix();

        if (kDimension < 0) {
            throw new IllegalArgumentException("kDimension cannot be negative");
        }

        if (kDimension > floatMatrix.length) {
            throw new IllegalArgumentException("kDimension cannot be greater than number of columns in matrix");
        }

        K_Dimension = kDimension;

        SimpleMatrix simpleMatrix = new SimpleMatrix(floatMatrix);

        simpleSVD = simpleMatrix.svd();
    }

    /**
     * Exposes the left singular vectors from the decomposition.
     *
     * @return the U matrix from the SVD decomposition
     */
    public SimpleMatrix getU() {
        return simpleSVD.getU();
    }

    /**
     * Exposes the right singular vectors from the decomposition.
     *
     * @return the V matrix from the SVD decomposition
     */
    public SimpleMatrix getV() {
        return simpleSVD.getV();
    }

    /**
     * Exposes the diagonal matrix of singular values.
     *
     * @return the W matrix from the SVD decomposition
     */
    public SimpleMatrix getW() {
        return simpleSVD.getW();
    }

    /**
     * Forms node embeddings from the leading left singular vectors scaled by their singular values.
     *
     * @return the low-rank embedding matrix of shape (rows × k)
     */
    public SimpleMatrix getEmbeddings() {
        SimpleMatrix u = getU();
        SimpleMatrix W = getW();

        SimpleMatrix uK = u.extractMatrix(0, u.getNumRows(), 0, K_Dimension);
        SimpleMatrix WK = W.extractMatrix(0, K_Dimension, 0, K_Dimension);

        return uK.mult(WK);
    }
}
