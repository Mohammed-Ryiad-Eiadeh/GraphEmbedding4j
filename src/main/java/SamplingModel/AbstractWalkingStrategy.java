package SamplingModel;

import Core.ImmutableGraphData;
import Core.VertexIndexMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Manages walk limits and generates a cached collection of indexed walks for each graph node.
 *
 * @param <V> vertex type used by the input graph
 */
public abstract sealed class AbstractWalkingStrategy<V> extends AbstractSampling<V> permits DeepWalk, MB2Vec, Node2Vec {
    private final ImmutableGraphData<V> immutableGraphDataObj;
    private final VertexIndexMapping<V> mapper;
    private final int walkPerNode;
    private volatile ArrayList<ArrayList<Integer>> cachedSetWalks;

    /**
     * Sets the graph, hop limit, and number of walks generated per vertex.
     *
     * @param immutableGraphData immutable graph data containing vertices and edges
     * @param mapping            mapping between vertices and internal node IDs
     * @param numOfHops          number of steps in each random walk
     * @param walkPerNode        number of walks generated per node
     */
    public AbstractWalkingStrategy(ImmutableGraphData<V> immutableGraphData, VertexIndexMapping<V> mapping, int numOfHops, int walkPerNode) {
        super(immutableGraphData, mapping);

        this.immutableGraphDataObj = immutableGraphData;

        this.mapper = mapping;

        if (numOfHops <= 0) {
            throw new IllegalArgumentException("numOfHops must be greater than 0");
        }

        if (walkPerNode <= 0) {
            throw new IllegalArgumentException("walkPerNode must be greater than 0");
        }
        this.walkPerNode = walkPerNode;
    }

    /**
     * Defines how a concrete strategy creates one walk from a starting vertex.
     *
     * @param start the node to launch the walk from
     * @return a random walk starting from the given source node
     */
    abstract ArrayList<Integer> generateWalk(V start);

    /**
     * Generates walks for every vertex once and reuses the cached collection on later calls.
     *
     * @return a set of generated random walks
     */
    public ArrayList<ArrayList<Integer>> getRandomWalks() {
        ArrayList<ArrayList<Integer>> randomWalks = cachedSetWalks;
        if (randomWalks != null) {
            return randomWalks;
        }

        ArrayList<ArrayList<Integer>> RWs = new ArrayList<>();

        for (int i = 0; i < immutableGraphDataObj.vertexCount(); i++) {
            for (int j = 0; j < walkPerNode; j++) {
                ArrayList<Integer> walk = generateWalk(mapper.getVertex(i));
                if (walk.size() >= 2) {
                    RWs.add(walk);
                }
            }
        }

        Collections.shuffle(RWs, new Random(12345));
        cachedSetWalks = RWs;

        return RWs;
    }
}
