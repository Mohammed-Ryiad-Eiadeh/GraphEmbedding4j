package EmbeddingInitialization;

import Core.ImmutableGraphData;

import java.util.HashMap;
import java.util.Objects;

/**
 * Stores common graph and reproducibility settings for embedding initialization strategies.
 */
public sealed abstract class Initializer<V> permits EmbeddingInitializer {
    private final int numOfNode;
    private final int embeddingDimension;
    private final long seed;

    /**
     * Captures the graph size, vector dimension, and random seed for an initializer.
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
     * Reports how many node vectors this initializer must produce.
     *
     * @return number of graph nodes
     */
    public int getNumOfNode() {
        return numOfNode;
    }

    /**
     * Reports the number of coordinates in each node vector.
     *
     * @return embedding dimension
     */
    public int getEmbeddingDimension() {
        return embeddingDimension;
    }

    /**
     * Reports the seed that makes initialization reproducible.
     *
     * @return random seed
     */
    public long getSeed() {
        return seed;
    }

    /**
     * Produces a vector for each indexed graph node using the concrete strategy.
     *
     * @return map of node IDs to embedding vectors
     */
    public abstract HashMap<Integer, double[]> initializeEmbedding();
}
