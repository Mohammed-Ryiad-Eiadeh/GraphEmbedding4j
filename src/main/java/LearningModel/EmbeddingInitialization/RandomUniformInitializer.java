package LearningModel.EmbeddingInitialization;

import Core.ImmutableGraphData;

import java.util.HashMap;
import java.util.Random;

/**
 * Initializes node embeddings using a random uniform distribution.
 *
 * @param <V> node identifier type
 */
public non-sealed class RandomUniformInitializer<V> extends EmbeddingInitializer<V> {
    private final int numOfNode;
    private final int embeddingDimension;
    private final long seed;

    /**
     * Creates an embedding initializer.
     *
     * @param immutableGraphData graph data tells how many vectors to initialize
     * @param embeddingDimension embedding vector dimension
     * @param seed          random seed
     */
    public RandomUniformInitializer(ImmutableGraphData<V> immutableGraphData, int embeddingDimension, long seed) {
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
        double bound = 1.0 / embeddingDimension;

        for (int node = 0; node < numOfNode; node++) {
            double[] embeddingVec = new double[embeddingDimension];

            for (int embeddingElementIndex = 0; embeddingElementIndex < embeddingDimension; embeddingElementIndex++) {
                embeddingVec[embeddingElementIndex] = (random.nextDouble() * 2 - 1) *  bound;
            }
            embeddings.put(node, embeddingVec);
        }
        return embeddings;
    }
}
