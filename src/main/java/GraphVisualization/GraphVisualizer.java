package GraphVisualization;

import Core.Edge;
import Core.ImmutableGraphData;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.Objects;
import java.util.Set;

/**
 * A generic graph visualizer that displays immutable graph data
 * using GraphStream with a polished graphical interface.
 *
 * @param <V> the type of vertices in the graph
 */
public class GraphVisualizer<V> {

    private final ImmutableGraphData<V> immutableGraphDataBuilder;
    private final Graph graph;
    private final boolean visualizeNodes;
    private final boolean visualizeEdges;

    /**
     * Constructs a graph visualizer with the given graph name, immutable graph data,
     * and options for displaying node IDs and edge weights.
     *
     * @param graphName the name of the graph
     * @param immutableGraphData the immutable graph data to visualize
     * @param visualizeNodes true to display node IDs; false to hide them
     * @param visualizeEdges true to display edge weights; false to hide them
     * @throws NullPointerException if immutableGraphData is null
     */
    public GraphVisualizer(String graphName, ImmutableGraphData<V> immutableGraphData, boolean visualizeNodes, boolean visualizeEdges) {
        this.immutableGraphDataBuilder = Objects.requireNonNull(immutableGraphData, "immutableGraphData");

        System.setProperty("org.graphstream.ui", "swing");

        this.graph = new SingleGraph(graphName);

        this.visualizeNodes = visualizeNodes;

        // By default, don't show nodes Ids
        for (Node node : graph) {
            node.removeAttribute("ui.label");
        }

        this.visualizeEdges = visualizeEdges;
    }

    /**
     * Constructs and displays a directed graph using GraphStream,
     * with styled nodes, edges, labels, and enhanced rendering quality.
     */
    public void displaySingleGraph() {
        Set<V> verticesSet = immutableGraphDataBuilder.vertexSet();
        Set<Edge<V>>  edgesSet = immutableGraphDataBuilder.edgeSet();

        if (verticesSet.size() > 5000 || edgesSet.size() > 10000) {
            throw new RuntimeException("Graph has more than 5000 vertices or 10000 edges!");
        }

        for (V node : verticesSet) {
            graph.addNode(node.toString());
        }

        int counter = 0;
        for (Edge<V> edge : edgesSet) {
            String edgeID = "edge : " + counter;

            org.graphstream.graph.Edge edges = graph.addEdge(edgeID,
                    edge.source().toString(),
                    edge.destination().toString(),
                    true);

            if (visualizeEdges) {
                edges.setAttribute("ui.label", edge.weight());
            }
            counter++;
        }

        graph.setAttribute("ui.stylesheet",
                "graph {" +
                        "   fill-color: #FFFFFF;" +
                        "   padding: 65px;" +
                        "}" +
                        "node {" +
                        "   shape: circle;" +
                        "   size: 20px;" +
                        "   fill-color: #800020;" +
                        "   stroke-mode: plain;" +
                        "   stroke-color: #580016;" +
                        "   stroke-width: 1px;" +
                        "   shadow-mode: none;" +
                        "   text-size: 15;" +
                        "   text-style: bold;" +
                        "   text-color: #333333;" +
                        "   text-alignment: at-right;" +
                        "   text-offset: 7px, 0px;" +
                        "}" +
                        "edge {" +
                        "   shape: line;" +
                        "   fill-color: #000000;" +
                        "   size: 2px;" +
                        "   arrow-shape: arrow;" +
                        "   arrow-size: 10px, 5px;" +
                        "   text-size: 13;" +
                        "   text-style: normal;" +
                        "   text-color: #000000;" +
                        "   text-alignment: above;" +
                        "   text-offset: 0px, -7px;" +
                        "   text-background-mode: plain;" +
                        "   text-background-color: #FFFFFF;" +
                        "   text-padding: 2px;" +
                        "}"
        );

        // Improve visualization quality
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        if  (visualizeNodes) {
            for (Node node : graph) {
                node.setAttribute("ui.label", node.getId());
            }
        }

        graph.display();
    }
}