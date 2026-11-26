package SamplingModel;

import Core.ImmutableGraphData;
import Core.VertexIndexMapping;
import Representation.AdjacentList.ImmutableAdjacentList;
import Representation.AdjacentList.Neighbor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Generates walks by choosing uniformly among each current node's outgoing neighbors.
 */
public non-sealed class DeepWalk<V> extends AbstractWalkingStrategy<V> {
    private final Map<Integer, List<Neighbor<Integer>>> adjacentList;
    private final VertexIndexMapping<V> mapper;
    private final int numOfHops;
    private final Random random;

    /**
     * Prepares indexed graph neighbors and a seeded random generator for uniform walks.
     *
     * @param immutableGraphDataObj immutable graph data containing vertices and edges
     * @param mapping            mapping between vertices and internal node IDs
     * @param numOfHops          number of steps in each random walk
     * @param walkPerNode        number of walks generated per node
     * @param randomSeed         seed used for reproducible sampling
     */
    public DeepWalk(ImmutableGraphData<V> immutableGraphDataObj, VertexIndexMapping<V> mapping, int numOfHops, int walkPerNode, long randomSeed) {
        super(immutableGraphDataObj, mapping, numOfHops, walkPerNode);

        this.mapper = mapping;
        this.adjacentList = new ImmutableAdjacentList<>(immutableGraphDataObj, mapping).getAdjacentMap();
        this.numOfHops = numOfHops;
        this.random = new Random(randomSeed);
    }

    /**
     * Walks from the starting vertex by repeatedly selecting a uniform outgoing neighbor.
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
