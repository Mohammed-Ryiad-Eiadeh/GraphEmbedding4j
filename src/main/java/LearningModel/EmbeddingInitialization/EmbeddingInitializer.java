package LearningModel.EmbeddingInitialization;

import Core.ImmutableGraphData;

import java.util.HashMap;
import java.util.Objects;

/**
 * Abstract class for initializing node embedding vectors
 * used by embedding learning models such as Skip-Gram.
 */
public abstract class EmbeddingInitializer<V> {
    private final int numOfNode;
    private final int embeddingDimension;
    private final long seed;

    /**
     * Creates an embedding initializer.
     *
     * @param immutableGraphData graph data tells how many vectors to initialize
     * @param embeddingDimension embedding vector dimension
     * @param seed random seed
     */
    public EmbeddingInitializer(ImmutableGraphData<V> immutableGraphData, int embeddingDimension, long seed) {
        Objects.requireNonNull(immutableGraphData, "immutableGraphData cannot be null");
        this.numOfNode = immutableGraphData.vertexCount();

        if (embeddingDimension <= 0) {
            throw new IllegalArgumentException("Embedding size must be positive");
        }
        this.embeddingDimension = embeddingDimension;

        if (seed <= 0) {
            throw new IllegalArgumentException("Seed must be positive long");
        }
        this.seed = seed;
    }

    /**
     * Returns the total number of nodes.
     *
     * @return number of graph nodes
     */
    public int getNumOfNode() {
        return numOfNode;
    }

    /**
     * Returns the embedding vector dimension.
     *
     * @return embedding dimension
     */
    public int getEmbeddingDimension() {
        return embeddingDimension;
    }

    /**
     * Returns the random seed used for initialization.
     *
     * @return random seed
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Initializes embedding vectors.
     *
     * @return map of node IDs to embedding vectors
     */
    public abstract HashMap<Integer, double[]> initializeEmbedding();
}
