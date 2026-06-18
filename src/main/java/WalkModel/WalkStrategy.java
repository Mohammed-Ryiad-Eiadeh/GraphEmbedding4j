package WalkModel;

import java.util.ArrayList;

/**
 * Interface for generating random walks given the graph structure.
 *
 * @param <V> the vertex type
 */
public sealed interface WalkStrategy<V> permits DeepWalk {
    /**
     * Returns the random walk, starting from a given source
     *
     * @param start the node to launch the walk
     *
     * @return a random walk starts from the given source node
     */
    ArrayList<Integer> generateWalk(V start);
}
