package WalkModel;

import Core.ImmutableGraphData;
import WalkModel.Strategy.WalkStrategy;
import representation.AdjacentList.AdjacentListModel.Neighbor;
import Core.VertexIndexMapping;
import representation.AdjacentList.ImmutableAdjacentList;

import java.util.*;

/**
 * Implements the DeepWalk uniform random walk strategy over an immutable adjacency list.
 * Each step selects one outgoing neighbor with equal probability to form a walk sequence.
 */
public class DeepWalk<V> implements WalkStrategy<V> {
    private final ImmutableGraphData<V> immutableGraphDataObj;
    private final Map<Integer, List<Neighbor<Integer>>> adjacentList;
    private final VertexIndexMapping<V> mapper;
    private final int numOfHops;
    private final int walkPerNode;
    private final Random random;
    private volatile HashSet<ArrayList<Integer>> cashedSetWalks;

    /**
     * Constructs a DeepWalk strategy by preprocessing the input graph into an
     * immutable adjacency list and initializing the vertex-to-index mapping.
     *
     * @param immutableGraphData immutable graph structure containing vertices and edges
     * @param mapping            mapping from generic vertices to internal integer node IDs
     * @param randomSeed seed for controlling randomness and ensuring reproducible sampling
     */
    public DeepWalk(ImmutableGraphData<V> immutableGraphData, VertexIndexMapping<V> mapping, int numOfHops, int walkPerNode, long randomSeed) {
        this.immutableGraphDataObj = Objects.requireNonNull(immutableGraphData, "immutableGraphData can not be null");
        this.mapper = Objects.requireNonNull(mapping, "mapping can not be null");

        if (numOfHops <= 0) {
            throw new IllegalArgumentException("numOfHops must be greater than 0");
        }
        if (walkPerNode <= 0) {
            throw new IllegalArgumentException("walkPerNode must be greater than 0");
        }

        this.adjacentList = new ImmutableAdjacentList<>(immutableGraphDataObj, mapping)
                .getAdjacentMap();
        this.numOfHops = numOfHops;
        this.walkPerNode = walkPerNode;
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

    /**
     * Returns the generated random walks.
     * <p>
     * If the walks were generated before, the cached set is returned.
     * Otherwise, random walks are generated for each vertex and stored for reuse.
     * Only walks of length at least {@code 2} are included.
     *
     * @return a set of generated random walks
     */
    public HashSet<ArrayList<Integer>> getRandomWalks() {
        HashSet<ArrayList<Integer>> randomWalks = cashedSetWalks;
        if (randomWalks != null) {
            return randomWalks;
        }

        HashSet<ArrayList<Integer>> RWs = new HashSet<>();
        for (var i = 0; i < immutableGraphDataObj.vertexCount(); i++) {
            for (var j = 0; j < walkPerNode; j++) {
                var walk = generateWalk(mapper.getVertex(i));
                if (walk.size() >= 2) {
                    RWs.add(walk);
                }
            }
        }
        cashedSetWalks = RWs;
        return RWs;
    }
}
