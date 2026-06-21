package WalkModel;

import Core.ImmutableGraphData;
import representation.AdjacentList.AdjacentListModel.Neighbor;
import Core.VertexIndexMapping;
import representation.AdjacentList.ImmutableAdjacentList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Implements the DeepWalk uniform random walk strategy over an immutable adjacency list.
 * Each step selects one outgoing neighbor with equal probability to form a walk sequence.
 */
public non-sealed class DeepWalk<V> extends WalkStrategy<V> {
    private final Map<Integer, List<Neighbor<Integer>>> adjacentList;
    private final VertexIndexMapping<V> mapper;
    private final int numOfHops;
    private final Random random;

    /**
     * Constructs a DeepWalk strategy by preprocessing the input graph into an
     * immutable adjacency list and initializing the vertex-to-index mapping.
     *
     * @param immutableGraphDataObj immutable graph data containing vertices and edges
     * @param mapping            mapping between vertices and internal node IDs
     * @param numOfHops          number of steps in each random walk
     * @param walkPerNode        number of walks generated per node
     * @param randomSeed         seed used for reproducible sampling
     */
    public DeepWalk(ImmutableGraphData<V> immutableGraphDataObj, VertexIndexMapping<V> mapping, int numOfHops, int walkPerNode, long randomSeed) {
        super(immutableGraphDataObj, mapping, numOfHops, walkPerNode, randomSeed);

        this.mapper = mapping;
        this.adjacentList = new ImmutableAdjacentList<>(immutableGraphDataObj, mapping).getAdjacentMap();
        this.numOfHops = numOfHops;
        this.random = new Random(randomSeed);
    }

    /**
     * Generates a random walk starting from the given vertex for a fixed number of hops.
     *
     * @param start starting vertex of the walk
     * @return list of node indices representing the walk path
     */
    @Override
    public ArrayList<Integer> generateWalk(V start) {
        ArrayList<Integer> sequence = new ArrayList<>();
        int current = mapper.indexForVertex(start);
        sequence.add(current);

        for (int i = 0; i < numOfHops; i++) {
            List<Neighbor<Integer>> neighbors = adjacentList.get(current);

            if (neighbors == null || neighbors.isEmpty()) {
                break;
            }

            int uniformNeighbor = this.random.nextInt(neighbors.size());
            int next = neighbors.get(uniformNeighbor).destination();
            sequence.add(next);
            current = next;
        }

        return sequence;
    }
}
