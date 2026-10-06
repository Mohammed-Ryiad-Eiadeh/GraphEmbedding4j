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

public non-sealed class MB2Vec<V> extends WalkStrategy<V> implements SamplingStrategy {
    private final Map<Integer, List<Neighbor<Integer>>> children;
    private final Map<Integer, List<Neighbor<Integer>>> parents;
    private final VertexIndexMapping<V> mapper;
    private final int numOfHops;
    private final double a1;
    private final double a2;
    private final double a3;
    private final double a4;
    private final double a5;
    private final Random random;
    private final Random randSample;
    private final Random mbSeed;

    public MB2Vec(ImmutableGraphData<V> immutableGraphData, VertexIndexMapping<V> mapping, int numOfHops, int walkPerNode, double a1, double a2, double a3, double a4, double a5, long randomSeed, long sampleSeed, long MBSeed) {
        super(immutableGraphData, mapping, numOfHops, walkPerNode);

        this.mapper = mapping;

        this.a1 = a1;
        this.a2 = a2;
        this.a3 = a3;
        this.a4 = a4;
        this.a5 = a5;

        this.children = new ImmutableAdjacentList<>(immutableGraphData, mapping).getAdjacentMap();
        this.parents = new ImmutableAdjacentList<>(immutableGraphData, mapping).getNodeToParentMap();

        this.numOfHops = numOfHops;

        this.random = new Random(randomSeed);
        this.randSample = new Random(sampleSeed);
        this.mbSeed = new Random(MBSeed);
    }

    /**
     * Returns the random walk, starting from a given source
     *
     * @param start the node to launch the walk
     * @return a biased walk starts from the given source node
     */
    @Override
    ArrayList<Integer> generateWalk(V start) {
        ArrayList<Integer> sequence = new ArrayList<>();

        int current = mapper.indexForVertex(start);
        int previous = current;

        sequence.add(current);

        List<Neighbor<Integer>> childrenList = children.getOrDefault(current, List.of());
        List<Neighbor<Integer>> parentList = parents.getOrDefault(current, List.of());
        List<Neighbor<Integer>> markovBlanket = getMarkovBlanket(current, childrenList, parentList);

        // First step: uniform random sampling.
        if (!markovBlanket.isEmpty()) {
            int uniformNeighbor = random.nextInt(markovBlanket.size());
            int next = markovBlanket.get(uniformNeighbor).destination();

            sequence.add(next);

            current = next;
        }

        for (int i = sequence.size(); i < numOfHops + 1; i++) {
            childrenList = children.getOrDefault(current, List.of());
            parentList = parents.getOrDefault(current, List.of());
            markovBlanket = getMarkovBlanket(current, childrenList, parentList);

            if (markovBlanket.isEmpty()) {
                break;
            }

            HashMap<Integer, Double> markovBlanketToBiasRatio = new HashMap<>();

            List<Neighbor<Integer>> previousChildren = children.getOrDefault(previous, List.of());
            List<Neighbor<Integer>> previousParents = parents.getOrDefault(previous, List.of());
            List<Neighbor<Integer>> previousMarkovBlanket = getMarkovBlanket(previous, previousChildren, previousParents);

            for (Neighbor<Integer> neighbor : markovBlanket) {

                int candidate = neighbor.destination();

                if (candidate == previous) {
                    // Return
                    markovBlanketToBiasRatio.put(candidate, a1);

                } else if (previousChildren.stream()
                        .anyMatch(v -> v.destination() == candidate)) {
                    // Candidate is a child of previous
                    markovBlanketToBiasRatio.put(candidate, a2);

                } else if (previousParents.stream()
                        .anyMatch(v -> v.destination() == candidate)) {
                    // Candidate is a parent of previous
                    markovBlanketToBiasRatio.put(candidate, a3);

                } else if (previousMarkovBlanket.stream()
                        .anyMatch(v -> v.destination() == candidate)) {
                    // Candidate is in MB(previous), e.g. co-parent
                    markovBlanketToBiasRatio.put(candidate, a4);

                } else {
                    // Outside MB(previous)
                    markovBlanketToBiasRatio.put(candidate, a5);
                }
            }

            int candidate = rouletteWheelSample(markovBlanketToBiasRatio, randSample);

            sequence.add(candidate);

            previous = current;
            current = candidate;
        }
        return sequence;
    }

    private List<Neighbor<Integer>> getMarkovBlanket (int current, List<Neighbor<Integer>> childrenList, List<Neighbor<Integer>> parentList) {
        Set<Neighbor<Integer>> coParentList;
        coParentList = childrenList.parallelStream()
                .flatMap(child -> parents.getOrDefault(child.destination(), List.of()).stream())
                .collect(Collectors.toSet());

        coParentList.removeIf(parent -> parent.destination() == current);

        List<Neighbor<Integer>> markovBlanket = new ArrayList<>();
        markovBlanket.addAll(parentList);
        markovBlanket.addAll(childrenList);
        markovBlanket.addAll(coParentList);

        markovBlanket = markovBlanket.stream().distinct().collect(Collectors.toList());

        Collections.shuffle(markovBlanket, mbSeed);

        return markovBlanket;
    }
}
