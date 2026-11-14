package LowRankApproximation;

import Representation.matrix.MatrixRepresentation;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

/**
 * Low-rank matrix approximation via Singular Value Decomposition (SVD).
 * <p>
 * Decomposes matrix A into U × W × V^T, where U and V are orthogonal matrices
 * and W contains singular values in descending order.
 *
 * @param <Mat> the type of the underlying matrix representation element
 */
public class SimpleSVDApproximation<Mat> {
    private final int K_Dimension;
    private final SimpleSVD<SimpleMatrix> simpleSVD;

    /**
     * Decomposes the given matrix using full SVD with the specified target rank.
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
     * Returns the U matrix (left singular vectors).
     *
     * @return the U matrix from the SVD decomposition
     */
    public SimpleMatrix getU() {
        return simpleSVD.getU();
    }

    /**
     * Returns the V matrix (right singular vectors).
     *
     * @return the V matrix from the SVD decomposition
     */
    public SimpleMatrix getV() {
        return simpleSVD.getV();
    }

    /**
     * Returns the W matrix (singular values in descending order).
     *
     * @return the W matrix from the SVD decomposition
     */
    public SimpleMatrix getW() {
        return simpleSVD.getW();
    }

    /**
     * Returns the low-rank embedding matrix by multiplying the top-k left singular vectors
     * with the top-k singular values (U_K × W_K).
     *
     * @return the low-rank embedding matrix of shape (rows × k)
     */
    public SimpleMatrix getEmbeddings() {
        SimpleMatrix u = simpleSVD.getU();
        SimpleMatrix W = simpleSVD.getW();

        SimpleMatrix uK = u.extractMatrix(0, u.getNumRows(), 0, K_Dimension);
        SimpleMatrix WK = W.extractMatrix(0, K_Dimension, 0, K_Dimension);

        return uK.mult(WK);
    }
}
