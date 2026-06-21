package EmbeddingInitializer;

import Core.ImmutableGraphData;

import java.util.HashMap;
import java.util.Random;

/**
 * Initializes node embeddings using a random uniform distribution.
 *
 * @param <V> node identifier type
 */
public non-sealed class EmbeddingInitializerOpt<V> extends InitializerOpt<V> {
    private final int numOfNode;
    private final int embeddingDimension;
    private final InitializerMode initializerMode;
    private final Random random;

    /**
     * Creates a random embedding initializer.
     * <p>
     * The initializer generates one embedding vector per node using
     * the selected initialization mode and random seed.
     *
     * @param immutableGraphData graph data used to determine the number of nodes
     * @param initializerMode initialization strategy to use
     * @param embeddingDimension embedding vector dimension
     * @param seed random seed for reproducible initialization
     */
    public EmbeddingInitializerOpt(ImmutableGraphData<V> immutableGraphData, InitializerMode initializerMode, int embeddingDimension, long seed) {
        super(immutableGraphData, embeddingDimension, seed);
        this.numOfNode = immutableGraphData.vertexCount();
        this.initializerMode = initializerMode;
        this.embeddingDimension = embeddingDimension;
        this.random = new Random(seed);
    }

    /**
     * Initializes embedding vectors for all nodes.
     * <p>
     * Each node receives an embedding vector generated according to
     * the selected initialization mode.
     *
     * @return map of node IDs to embedding vectors
     */
    @Override
    public HashMap<Integer, double[]> initializeEmbedding() {
        HashMap<Integer, double[]> embeddings = new HashMap<>();

        for (int i = 0; i < numOfNode; i++) {
            switch (initializerMode) {
                case Uniform -> embeddings.put(i, getUniformEmbedding());
                case Gaussian -> embeddings.put(i, getGaussianEmbedding());
                case Exponential -> embeddings.put(i, getExponentialEmbedding());
            }
        }

        return embeddings;
    }

    /**
     * Creates a uniformly initialized embedding vector.
     *
     * @return embedding vector sampled from [-1/d, 1/d]
     */
    private double[] getUniformEmbedding() {
        double[] embeddingVec = new double[embeddingDimension];
        double bound = 1.0 / embeddingDimension;

        for (int i = 0; i < embeddingDimension; i++) {
            embeddingVec[i] = (random.nextDouble() * 2 - 1) *  bound;
        }

        return embeddingVec;
    }

    /**
     * Creates a Gaussian-initialized embedding vector.
     *
     * @return embedding vector sampled from N(0, 1/sqrt(d))
     */
    private double[] getGaussianEmbedding() {
        double[] embeddingVec = new double[embeddingDimension];

        for (int i = 0; i < embeddingDimension; i++) {
            embeddingVec[i] = random.nextGaussian(0, 1 / Math.sqrt(embeddingDimension));
        }

        return embeddingVec;
    }

    /**
     * Creates an exponential-initialized embedding vector.
     *
     * @return embedding vector sampled from exponential term
     */
    private double[] getExponentialEmbedding() {
        double[] embeddingVec = new double[embeddingDimension];

        for (int i = 0; i < embeddingDimension; i++) {
            embeddingVec[i] = random.nextExponential();
        }

        return embeddingVec;
    }
}
