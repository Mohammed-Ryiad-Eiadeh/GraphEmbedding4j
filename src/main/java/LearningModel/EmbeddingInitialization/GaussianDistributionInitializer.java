package LearningModel.EmbeddingInitialization;

import Core.ImmutableGraphData;

import java.util.HashMap;
import java.util.Random;

/**
 * Initializes node embeddings using a Gaussian distribution.
 *
 * @param <V> node identifier type
 */
public non-sealed class GaussianDistributionInitializer<V> extends EmbeddingInitializer<V> {
    private final int numOfNode;
    private final int embeddingDimension;
    private final long seed;

    /**
     * Creates an embedding initializer.
     *
     * @param immutableGraphData graph data tells how many vectors to initialize
     * @param embeddingDimension embedding vector dimension
     * @param seed               random seed
     */
    public GaussianDistributionInitializer(ImmutableGraphData<V> immutableGraphData, int embeddingDimension, long seed) {
        super(immutableGraphData, embeddingDimension, seed);
        this.numOfNode = immutableGraphData.vertexCount();
        this.embeddingDimension = embeddingDimension;
        this.seed = seed;
    }

    /**
     * Initializes embedding vectors.
     *
     * @return map of node IDs to embedding vectors
     */
    @Override
    public HashMap<Integer, double[]> initializeEmbedding() {
        HashMap<Integer, double[]> embeddings = new HashMap<>();
        Random random = new Random(seed);

        for (int i = 0; i < numOfNode; i++) {
            double[] embeddingVec = new double[embeddingDimension];

            for (int j = 0; j < embeddingDimension; j++) {
                embeddingVec[j] = random.nextGaussian(0, 1 / Math.sqrt(embeddingDimension));
            }

            embeddings.put(i, embeddingVec);
        }
        return embeddings;
    }
}
