package SamplingModel;

import Core.ImmutableGraphData;
import Core.VertexIndexMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Base class for random-walk strategies that generate sequences of vertex indices.
 * <p>
 * Manages walk configuration and caches the generated walks for reuse.
 * </p>
 *
 * @param <V> vertex type used by the input graph
 */
public abstract sealed class AbstractWalkingStrategy<V> extends AbstractSampling<V> permits DeepWalk, MB2Vec, Node2Vec {
    private final ImmutableGraphData<V> immutableGraphDataObj;
    private final VertexIndexMapping<V> mapper;
    private final int walkPerNode;
    private volatile ArrayList<ArrayList<Integer>> cachedSetWalks;

    /**
     * Constructs a walk strategy with shared graph and sampling settings.
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
     * Returns a random walk starting from a given source node.
     *
     * @param start the node to launch the walk from
     * @return a random walk starting from the given source node
     */
    abstract ArrayList<Integer> generateWalk(V start);

    /**
     * Returns the generated random walks.
     * <p>
     * If the walks were generated before, the cached set is returned.
     * Otherwise, random walks are generated for each vertex and stored for reuse.
     * Only walks of length at least {@code 2} are included.
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
