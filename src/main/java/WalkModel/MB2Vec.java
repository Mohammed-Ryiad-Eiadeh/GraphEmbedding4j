
package WalkModel;

import Core.ImmutableGraphData;
import Core.VertexIndexMapping;
import representation.AdjacentList.AdjacentListModel.Neighbor;
import representation.AdjacentList.ImmutableAdjacentList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements MB2Vec, a Markov Blanket-based biased random walk strategy
 * for learning node representations in directed graphs.
 *
 * The Markov Blanket of a node consists of its parents, children,
 * and co-parents. MB2Vec explores these relationships using five
 * transition bias parameters (a1, a2, a3, a4, a5).
 *
 * The first transition is sampled uniformly, while subsequent
 * transitions use context-dependent weights and roulette-wheel sampling.
 *
 * @param <V> the vertex type
 */
public non-sealed class MB2Vec<V> extends WalkStrategy<V>
        implements SamplingStrategy {

    // Graph structure and vertex indexing
    private final Map<Integer, List<Neighbor<Integer>>> children;
    private final Map<Integer, List<Neighbor<Integer>>> parents;
    private final VertexIndexMapping<V> mapper;

    // Walk configuration and transition bias parameters
    private final int numOfHops;
    private final double a1;
    private final double a2;
    private final double a3;
    private final double a4;

    // Random generators for walk initialization, sampling, and shuffling
    private final Random random;
    private final Random randSample;
    private final Random mbSeed;

    /**
     * Initializes MB2Vec with the graph structure, walk configuration,
     * transition bias parameters, and random seeds.
     *
     * @param immutableGraphData the immutable graph representation
     * @param mapping vertex-to-index mapping
     * @param numOfHops number of transitions per walk
     * @param walkPerNode number of walks generated per node
     * @param a1 return transition weight
     * @param a2 weight for children of the previous node
     * @param a3 weight for parents of the previous node
     * @param a4 weight for remaining nodes in the previous Markov Blanket
     * @param randomSeed seed for initial uniform sampling
     * @param sampleSeed seed for roulette-wheel sampling
     * @param MBSeed seed for Markov Blanket shuffling
     */
    public MB2Vec(ImmutableGraphData<V> immutableGraphData, VertexIndexMapping<V> mapping, int numOfHops, int walkPerNode, double a1, double a2, double a3, double a4, long randomSeed, long sampleSeed, long MBSeed) {

        super(immutableGraphData, mapping, numOfHops, walkPerNode);

        // Initialize graph indexing and transition bias parameters
        this.mapper = mapping;

        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.a4 = a4;

        // Construct outgoing and incoming adjacency mappings
        this.children = new ImmutableAdjacentList<>(
                immutableGraphData, mapping).getAdjacentMap();

        this.parents = new ImmutableAdjacentList<>(
                immutableGraphData, mapping).getNodeToParentMap();

        // Initialize walk configuration and random generators
        this.numOfHops = numOfHops;

        this.random = new Random(randomSeed);
        this.randSample = new Random(sampleSeed);
        this.mbSeed = new Random(MBSeed);
    }

    /**
     * Generates a Markov Blanket-based biased random walk
     * starting from the specified source node.
     *
     * The first transition uses uniform sampling. Subsequent
     * transitions assign weights according to the relationship
     * between each candidate and the previous node.
     *
     * @param start the source node
     * @return the generated sequence of vertex indices
     */
    @Override
    ArrayList<Integer> generateWalk(V start) {

        // Initialize the walk sequence and starting node
        ArrayList<Integer> sequence = new ArrayList<>();

        int current = mapper.indexForVertex(start);
        int previous = current;

        sequence.add(current);

        // Construct the initial Markov Blanket
        List<Neighbor<Integer>> childrenList =
                children.getOrDefault(current, List.of());

        List<Neighbor<Integer>> parentList =
                parents.getOrDefault(current, List.of());

        List<Neighbor<Integer>> markovBlanket =
                getMarkovBlanket(current, childrenList, parentList);

        // First transition: uniform sampling from the Markov Blanket
        if (!markovBlanket.isEmpty()) {

            int uniformNeighbor = random.nextInt(markovBlanket.size());

            int next = markovBlanket.get(uniformNeighbor).destination();

            sequence.add(next);
            current = next;
        }

        // Generate subsequent transitions using biased sampling
        for (int i = sequence.size(); i < numOfHops + 1; i++) {

            // Construct the Markov Blanket of the current node
            childrenList = children.getOrDefault(current, List.of());
            parentList = parents.getOrDefault(current, List.of());

            markovBlanket =
                    getMarkovBlanket(current, childrenList, parentList);

            // Terminate when no candidate neighbors are available
            if (markovBlanket.isEmpty()) {
                break;
            }

            // Initialize candidate-to-transition-weight mapping
            HashMap<Integer, Double> markovBlanketToBiasRatio =
                    new HashMap<>();

            // Retrieve the previous node's structural neighborhood
            List<Neighbor<Integer>> previousChildren =
                    children.getOrDefault(previous, List.of());

            List<Neighbor<Integer>> previousParents =
                    parents.getOrDefault(previous, List.of());

            List<Neighbor<Integer>> previousMarkovBlanket =
                    getMarkovBlanket(
                            previous,
                            previousChildren,
                            previousParents
                    );

            // Assign transition weights based on structural relationships
            for (Neighbor<Integer> neighbor : markovBlanket) {

                int candidate = neighbor.destination();

                if (candidate == previous) {

                    // a1: Return to the previous node
                    markovBlanketToBiasRatio.put(candidate, a1);

                } else if (previousChildren.stream()
                        .anyMatch(v -> v.destination() == candidate)) {

                    // a2: Candidate is a child of the previous node
                    markovBlanketToBiasRatio.put(candidate, a2);

                } else if (previousParents.stream()
                        .anyMatch(v -> v.destination() == candidate)) {

                    // a3: Candidate is a parent of the previous node
                    markovBlanketToBiasRatio.put(candidate, a3);

                } else if (previousMarkovBlanket.stream()
                        .anyMatch(v -> v.destination() == candidate)) {

                    // a4: Candidate belongs to the previous Markov Blanket
                    // after excluding the preceding transition categories
                    markovBlanketToBiasRatio.put(candidate, a4);

                } else {

                    // a5: Candidate lies outside the previous Markov Blanket
                    markovBlanketToBiasRatio.put(candidate, 0.2);
                }
            }

            // Select the next node using roulette-wheel sampling
            int candidate = rouletteWheelSample(
                    markovBlanketToBiasRatio,
                    randSample
            );

            // Update the walk sequence and traversal state
            sequence.add(candidate);

            previous = current;
            current = candidate;
        }

        return sequence;
    }

    /**
     * Constructs the Markov Blanket of the specified node.
     *
     * The Markov Blanket combines:
     * - Parents: nodes with incoming edges to the current node.
     * - Children: nodes reachable through outgoing edges.
     * - Co-parents: nodes sharing at least one child with the current node.
     *
     * The resulting collection is deduplicated and shuffled before
     * being returned.
     *
     * @param current the current vertex index
     * @param childrenList outgoing neighbors of the current node
     * @param parentList incoming neighbors of the current node
     * @return the shuffled Markov Blanket neighbor list
     */
    private List<Neighbor<Integer>> getMarkovBlanket(
            int current,
            List<Neighbor<Integer>> childrenList,
            List<Neighbor<Integer>> parentList) {

        // Identify co-parents through the parents of shared children
        Set<Neighbor<Integer>> coParentList;

        coParentList = childrenList.parallelStream()
                .flatMap(child ->
                        parents.getOrDefault(
                                child.destination(),
                                List.of()
                        ).stream()
                )
                .collect(Collectors.toSet());

        // Exclude the current node from the co-parent collection
        coParentList.removeIf(
                parent -> parent.destination() == current
        );

        // Combine parents, children, and co-parents
        List<Neighbor<Integer>> markovBlanket = new ArrayList<>();

        markovBlanket.addAll(parentList);
        markovBlanket.addAll(childrenList);
        markovBlanket.addAll(coParentList);

        // Remove duplicate neighbors
        markovBlanket = markovBlanket.stream()
                .distinct()
                .collect(Collectors.toList());

        // Shuffle the Markov Blanket using the configured random seed
        Collections.shuffle(markovBlanket, mbSeed);

        return markovBlanket;
    }
}
