package representation.AdjacentList;

import Core.Edge;
import Core.ImmutableGraphData;
import Core.VertexIndexMapping;
import representation.AdjacentList.AdjacentListModel.AdjacencyListData;
import representation.AdjacentList.AdjacentListModel.Neighbor;

import java.util.*;

/**
 * Immutable adjacency list representation backed by indexed vertices.
 *
 * <p>The adjacency list is lazily constructed from the underlying
 * immutable graph edge set and cached for fast reuse.</p>
 *
 * @param <V> the original vertex type
 */
public class ImmutableAdjacentList<V> extends AdjacencyListData<Integer> {
    private final ImmutableGraphData<V> immutableGraphData;
    private final VertexIndexMapping<V> mapper;
    private volatile Map<Integer, List<Neighbor<Integer>>> cashedAdjacentList;

    /**
     * Constructs an immutable adjacency list representation.
     *
     * @param graphData the immutable graph snapshot
     * @param mapper mapping from vertices to integer indices and vice versa
     */
    public ImmutableAdjacentList (ImmutableGraphData<V> graphData, VertexIndexMapping<V> mapper) {
        this.immutableGraphData = Objects.requireNonNull(graphData, "GraphData");
        this.mapper = Objects.requireNonNull( mapper, "mapper");
    }

    /**
     * Returns the adjacency list indexed by integer vertex IDs.
     *
     * <p>The list is constructed once from the edge set and cached
     * for subsequent calls.</p>
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
        cashedAdjacentList = adjacentMap;

        return Collections.unmodifiableMap(cashedAdjacentList);
    }
}
