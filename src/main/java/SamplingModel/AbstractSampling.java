package SamplingModel;

import Core.ImmutableGraphData;
import Core.VertexIndexMapping;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Shares graph data and vertex indexing across walk-generation strategies.
 *
 * @param <V> the vertex type
 */
public abstract sealed class AbstractSampling<V> permits AbstractWalkingStrategy {
    private final ImmutableGraphData<V> immutableGraphDataObj;
    private final VertexIndexMapping<V> mapper;

    /**
     * Stores the graph and its vertex-to-index mapping for a sampling strategy.
     *
     * @param immutableGraphDataObj the input graph
     * @param mapper the vertex-to-index mapping
     */
    public AbstractSampling(ImmutableGraphData<V> immutableGraphDataObj, VertexIndexMapping<V> mapper) {
        this.immutableGraphDataObj = Objects.requireNonNull(immutableGraphDataObj, "immutableGraphData can not be null");
        this.mapper = Objects.requireNonNull(mapper, "mapper can not be null");
    }

    /**
     * Generates an indexed walk beginning at the requested graph vertex.
     *
     * @param start the starting vertex
     * @return the sequence of visited vertex indices
     */
    abstract ArrayList<Integer> generateWalk(V start);
}