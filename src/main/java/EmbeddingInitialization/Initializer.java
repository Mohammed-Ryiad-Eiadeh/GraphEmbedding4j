package EmbeddingInitialization;

import Core.ImmutableGraphData;

import java.util.HashMap;
import java.util.Objects;

/**
 * Abstract base for initializing node embedding vectors
 * before training begins.
 */
public sealed abstract class Initializer<V> permits EmbeddingInitializer {
    private final int numOfNode;
    private final int embeddingDimension;
    private final long seed;

    /**
     * Creates an embedding initializer.
     *
     * @param immutableGraphData the graph data used to determine the number of vectors
     * @param embeddingDimension the embedding vector dimension
     * @param seed the random seed for reproducibility
     */
    public Initializer(ImmutableGraphData<V> immutableGraphData, int embeddingDimension, long seed) {
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
