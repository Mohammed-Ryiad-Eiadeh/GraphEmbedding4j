package representation.AdjacentList;

import Core.Edge;
import Core.ImmutableGraphData;
import Core.VertexIndexMapping;
import representation.AdjacentList.AdjacentListModel.AdjacencyListData;
import representation.AdjacentList.AdjacentListModel.Neighbor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private volatile Map<Integer, List<Neighbor<Integer>>> cashedNodeToParentList;

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
     * <p>
     * This would give node-{children} if the graph is directed
     * O.W, would give first order neighbors
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
        cashedAdjacentList = Collections.unmodifiableMap(adjacentMap);

        return cashedAdjacentList;
    }

    /**
     * Returns the parent of node list indexed by integer vertex IDs.
     * <p>
     * This would give node-{parents} if the graph is directed
     *
     * <p>The list is constructed once from the edge set and cached
     * for subsequent calls.</p>
     *
     * @return immutable node-{parents}
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
