package Representation.AdjacentList;

import Core.Edge;
import Core.ImmutableGraphData;
import Core.VertexIndexMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Provides cached, read-only outgoing and incoming neighbor maps for an indexed graph snapshot.
 *
 * @param <V> the original vertex type
 */
public class ImmutableAdjacentList<V> extends AdjacencyListData<Integer> {
    private final ImmutableGraphData<V> immutableGraphData;
    private final VertexIndexMapping<V> mapper;
    private volatile Map<Integer, List<Neighbor<Integer>>> cashedAdjacentList;
    private volatile Map<Integer, List<Neighbor<Integer>>> cashedNodeToParentList;

    /**
     * Connects a graph snapshot with the mapping needed to index its vertices.
     *
     * @param graphData the immutable graph snapshot
     * @param mapper mapping from vertices to integer indices and vice versa
     */
    public ImmutableAdjacentList (ImmutableGraphData<V> graphData, VertexIndexMapping<V> mapper) {
        this.immutableGraphData = Objects.requireNonNull(graphData, "GraphData");
        this.mapper = Objects.requireNonNull( mapper, "mapper");
    }

    /**
     * Builds or returns the cached map of each indexed vertex's outgoing neighbors.
     *
     * @return immutable adjacency map
     */
    @Override
    public Map<Integer, List<Neighbor<Integer>>> getAdjacentMap() {
        Map<Integer, List<Neighbor<Integer>>> local = cashedAdjacentList;
        if (local != null) {
            return local;
        }

        Map<Integer, List<Neighbor<Integer>>> adjacentMap = new HashMap<>();

        for (Edge<V> edge : this.immutableGraphData.edgeSet()) {
            int source = this.mapper.indexForVertex(edge.source());
            int destination = this.mapper.indexForVertex(edge.destination());

            adjacentMap.computeIfAbsent(source, NR-> new ArrayList<>())
                    .add(new Neighbor<>(destination, edge.weight()));
        }
        cashedAdjacentList = Collections.unmodifiableMap(adjacentMap);

        return cashedAdjacentList;
    }

    /**
     * Builds or returns the cached map of each indexed vertex's incoming neighbors.
     *
     * @return immutable parent adjacency map
     */
    public  Map<Integer, List<Neighbor<Integer>>> getNodeToParentMap() {
        Map<Integer, List<Neighbor<Integer>>> local = cashedNodeToParentList;
        if (local != null) {
            return local;
        }

        Map<Integer, List<Neighbor<Integer>>> parentMap = new HashMap<>();

        for (Edge<V> edge : this.immutableGraphData.edgeSet()) {
            int source = this.mapper.indexForVertex(edge.source());
            int destination = this.mapper.indexForVertex(edge.destination());

            // This would give node-{parents} if the graph is directed
            parentMap.computeIfAbsent(destination, NR-> new ArrayList<>())
                    .add(new Neighbor<>(source, edge.weight()));
        }
        cashedNodeToParentList = Collections.unmodifiableMap(parentMap);

        return cashedNodeToParentList;
    }
}