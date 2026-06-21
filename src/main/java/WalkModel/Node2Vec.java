package WalkModel;

import Core.ImmutableGraphData;
import Core.VertexIndexMapping;
import representation.AdjacentList.AdjacentListModel.Neighbor;
import representation.AdjacentList.ImmutableAdjacentList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Node2Vec biased random-walk strategy.
 * <p>
 * This strategy generates second-order random walks where the next node
 * depends on both the current node and the previously visited node. The
 * parameters {@code p} and {@code q} control return behavior and exploration.
 *
 * @param <V> vertex type used by the input graph
 */
public non-sealed class Node2Vec<V> extends WalkStrategy<V> {
    private final Map<Integer, List<Neighbor<Integer>>> adjacentList;
    private final VertexIndexMapping<V> mapper;
    private final int numOfHops;
    private final double p;
    private final double q;
    private final Random random;
    private final Random randSample;

    /**
     * Constructs a Node2Vec walk strategy.
     * <p>
     * Builds an immutable adjacency list from the input graph and initializes
     * the parameters used for second-order biased random walks.
     *
     * @param immutableGraphData immutable graph data containing vertices and edges
     * @param mapping            mapping between vertices and internal node IDs
     * @param numOfHops          number of steps in each random walk
     * @param walkPerNode        number of walks generated per node
     * @param p                  return parameter; larger values discourage backtracking
     * @param q                  in-out parameter; smaller values encourage outward exploration
     * @param randomSeed         seed used for reproducible sampling
     */
    public Node2Vec(ImmutableGraphData<V> immutableGraphData, VertexIndexMapping<V> mapping, int numOfHops, int walkPerNode, double p, double q, long randomSeed) {
        super(immutableGraphData, mapping, numOfHops, walkPerNode, randomSeed);

        this.mapper = mapping;

        this.p = p;
        this.q = q;

        this.adjacentList = new ImmutableAdjacentList<>(immutableGraphData, mapping).getAdjacentMap();
        this.numOfHops = numOfHops;
        this.random = new Random(randomSeed);

        this.randSample = new Random(12345L);
    }

    /**
     * Returns the random walk, starting from a given source
     *
     * @param start the node to launch the walk
     * @return a random walk starts from the given source node
     */
    @Override
    public ArrayList<Integer> generateWalk(V start) {
        ArrayList<Integer> sequence = new ArrayList<>();

        int current = mapper.indexForVertex(start);
        int previous = current;

        sequence.add(current);

        List<Neighbor<Integer>> neighbors = adjacentList.get(current);

        // First step: uniform random sampling.
        if (neighbors != null && !neighbors.isEmpty()) {
            int uniformNeighbor = random.nextInt(neighbors.size());
            int next = neighbors.get(uniformNeighbor).destination();

            sequence.add(next);

            current = next;
        }

        for (int i = sequence.size(); i < numOfHops + 1; i++) {
            neighbors = adjacentList.get(current);

            if (neighbors == null || neighbors.isEmpty()) {
                break;
            }

            HashMap<Integer, Double> neighborsToBiasRatio = new HashMap<>();

            List<Neighbor<Integer>> previousNeighbors =
                    adjacentList.getOrDefault(previous, List.of());

            for (Neighbor<Integer> neighbor : neighbors) {
                int candidate = neighbor.destination();

                if (candidate == previous) {
                    neighborsToBiasRatio.put(candidate, 1.0 / p);
                } else if (previousNeighbors.stream().anyMatch(v -> v.destination() == candidate)) {
                    neighborsToBiasRatio.put(candidate, 1.0);
                } else {
                    neighborsToBiasRatio.put(candidate, 1.0 / q);
                }
            }

            int candidate = rouletteWheelSample(neighborsToBiasRatio);

            sequence.add(candidate);

            previous = current;
            current = candidate;
        }

        return sequence;
    }

    /**
     * Selects a node using roulette-wheel (weighted random) sampling.
     * <p>
     * Each candidate node is chosen with probability proportional
     * to its associated bias weight.
     *
     * @param neighborsToBiasRatio mapping from candidate nodes to their bias weights
     * @return the sampled node ID
     * @throws IllegalStateException if no candidate can be selected
     */
    private int rouletteWheelSample(HashMap<Integer, Double> neighborsToBiasRatio) {
        double total = neighborsToBiasRatio.values().stream().mapToDouble(Double::doubleValue).sum();

        double rand = randSample.nextDouble() * total;

        double commutative = 0.0;

        for (Map.Entry<Integer, Double> entry : neighborsToBiasRatio.entrySet()) {
            commutative += entry.getValue();

            if (rand <= commutative) {
                return entry.getKey();
            }
        }

        throw  new IllegalStateException("no more roulette wheel sample");
    }
}
