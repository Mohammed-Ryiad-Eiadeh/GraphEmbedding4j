package SamplingModel;

import Core.ImmutableGraphData;
import Core.VertexIndexMapping;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Abstract base for all sampling strategies that generate random walks.
 *
 * @param <V> the vertex type
 */
public abstract sealed class AbstractSampling<V> permits AbstractWalkingStrategy {
    private final ImmutableGraphData<V> immutableGraphDataObj;
    private final VertexIndexMapping<V> mapper;

    /**
     * Initializes the walk model.
     *
     * @param immutableGraphDataObj the input graph
     * @param mapper the vertex-to-index mapping
     */
    public AbstractSampling(ImmutableGraphData<V> immutableGraphDataObj, VertexIndexMapping<V> mapper) {
        this.immutableGraphDataObj = Objects.requireNonNull(immutableGraphDataObj, "immutableGraphData can not be null");
        this.mapper = Objects.requireNonNull(mapper, "mapper can not be null");
    }

    /**
     * Abstract method for generating a random walk from a given source vertex.
     * Must be implemented by subclasses.
     *
     * @param start the starting vertex
     * @return the sequence of visited vertex indices
     */
    abstract ArrayList<Integer> generateWalk(V start);
}