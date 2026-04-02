package WalkModel.Strategy;

import java.util.ArrayList;

/**
 * Interface for generating random walks given the graph structure.
 *
 * @param <V> the vertex type
 */
public interface WalkStrategy<V> {
    /**
     * Returns the random walk, starting from a given source..
     *
     * @param start the node to launch the walk
     * @param hops the max number of hops, the walk can go
     *
     * @return a random walk starts from the given source node
     */
    ArrayList<Integer> generateWalk(V start);
}
