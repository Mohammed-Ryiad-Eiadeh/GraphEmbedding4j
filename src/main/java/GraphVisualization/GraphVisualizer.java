package GraphVisualization;

import Core.Edge;
import Core.ImmutableGraphData;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.images.Resolutions;
import org.graphstream.ui.swing.util.SwingFileSinkImages;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;

/**
 * Displays an immutable graph using GraphStream with styled nodes and edges.
 *
 * @param <V> the type of vertices in the graph
 */
public class GraphVisualizer<V> {
    private final ImmutableGraphData<V> immutableGraphDataBuilder;
    private final Graph graph;
    private final boolean isDirected;
    private final boolean visualizeNodes;
    private final boolean visualizeEdges;

    /**
     * Constructs a graph visualizer with the given graph name, immutable graph data,
     * and options for directed edges, node IDs, and edge weights.
     *
     * @param graphName the name of the graph
     * @param immutableGraphData the immutable graph data to visualize
     * @param isDirected true if edges are directed
     * @param visualizeNodes true to display node IDs; false to hide them
     * @param visualizeEdges true to display edge weights; false to hide them
     * @throws NullPointerException if immutableGraphData is null
     */
    public GraphVisualizer(String graphName, ImmutableGraphData<V> immutableGraphData, boolean isDirected, boolean visualizeNodes, boolean visualizeEdges) {
        this.immutableGraphDataBuilder = Objects.requireNonNull(immutableGraphData, "immutableGraphData");

        this.isDirected = isDirected;

        System.setProperty("org.graphstream.ui", "swing");

        this.graph = new SingleGraph(graphName);

        this.visualizeNodes = visualizeNodes;

        // By default, don't show nodes Ids
        for (Node node : graph) {
            node.removeAttribute("ui.label");
        }

        // By default, don't show edge weights
        for (org.graphstream.graph.Edge edge : graph.edges().toList()) {
            edge.removeAttribute("ui.label");
        }

        this.visualizeEdges = visualizeEdges;
    }

    /**
     * Renders the graph using GraphStream with styled nodes, edges,
     * labels, and enhanced rendering quality.
     */
    public void displaySingleGraph() {
        Set<V> verticesSet = immutableGraphDataBuilder.vertexSet();
        Set<Edge<V>>  edgesSet = immutableGraphDataBuilder.edgeSet();

        // This check avoids rendering exceptions for large graphs
        if (verticesSet.size() > 5000 || edgesSet.size() > 10000) {
            throw new RuntimeException("Graph has more than 5000 vertices or 10000 edges!");
        }

        for (V node : verticesSet) {
            graph.addNode(node.toString());
        }

        int counter = 0;
        for (Edge<V> edge : edgesSet) {
            String edgeID = "edge : " + counter;

            graph.addEdge(edgeID,
                    edge.source().toString(),
                    edge.destination().toString(),
                    isDirected)
                    .setAttribute("weight", edge.weight());

            counter++;
        }

        graph.setAttribute("ui.stylesheet",
                "graph {" +
                        "   fill-color: #FFFFFF;" +
                        "   padding: 30px;" +
                        "}" +

                        "node {" +
                        "   shape: circle;" +
                        "   size: 10px;" +
                        "   fill-mode: gradient-radial;" +
                        "   fill-color: brown;" +
                        "   stroke-mode: plain;" +
                        "   stroke-color: brown;" +
                        "   stroke-width: 1.5px;" +
                        "   shadow-mode: none;" +

                        "   text-size: 20;" +
                        "   text-style: bold;" +
                        "   text-font: Arial;" +
                        "   text-color: #000000;" +
                        "   text-alignment: at-right;" +
                        "   text-offset: 8px, 0px;" +
                        "}" +

                        "edge {" +
                        "   shape: line;" +
                        "   fill-color: #000000;" +
                        "   size: 1.5px;" +

                        "   arrow-shape: arrow;" +
                        "   arrow-size: 5px, 5px;" +

                        "   text-size: 20;" +
                        "   text-style: normal;" +
                        "   text-font: Arial;" +
                        "   text-color: #000000;" +
                        "   text-alignment: above;" +
                        "   text-offset: 0px, -6px;" +
                        "   text-background-mode: plain;" +
                        "   text-background-color: #FFFFFF;" +
                        "   text-padding: 10px;" +
                        "}"
        );

        // Improve visualization quality
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        if  (visualizeNodes) {
            graph.nodes().forEach(node -> node.setAttribute("ui.label", node.getId()));
        }

        if (visualizeEdges) {
            graph.edges()
                    .filter((edge) -> edge.hasAttribute("weight"))
                    .forEach(edge -> edge.setAttribute("ui.label", edge.getAttribute("weight")));
        }

        graph.display();
    }

    /**
     * Saves the graph as a PNG image at HD720 resolution.
     *
     * @param path the output file path
     */
    public void saveGraphToImage(Path path) {
        SwingFileSinkImages image = new SwingFileSinkImages(FileSinkImages.OutputType.PNG, Resolutions.HD720);
        image.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
        try {
            image.writeAll(graph, path.toString());
            image.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}