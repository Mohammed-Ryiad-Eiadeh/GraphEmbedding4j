package MainPackage.LearnEmbeddingMain;

import Core.GraphBuilder;
import Core.GraphType;
import Core.VertexIndexMapping;
import GraphVisualization.GraphVisualizer;
import IO.CSVGraphLoader;
import IO.CSVNodeEmbeddingExporter;
import LowRankApproximation.SimpleSVDApproximation;
import Representation.AdjacentList.ImmutableAdjacentList;
import Representation.matrix.ImmutableAdjacentMatrix;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

/**
 * End-to-end demo that learns node embeddings via Truncated SVD
 * on the Karate Club graph, then exports the results to CSV.
 */
public class LearnEmbeddingTruncatedSVD {
    public static void main(String[] args) throws IOException {
        // Load the graph from Graphs
        var fileName = "Karate_Edges";
        var graphDataFile = Paths.get(System.getProperty("user.dir"), "\\Graphs\\" + fileName + ".txt");

        // Load the graph into the passed builder (explicitly update in-place)
        var graphBuilder = new GraphBuilder<Integer>(GraphType.Directed);
        new CSVGraphLoader<>(graphBuilder).loadGraphIntoBuilder(graphDataFile,
                Integer::parseInt,
                1);       // Skip the header

        // Build a directed graph using GraphBuilder
        var builder = graphBuilder
                .ifNotEmpty()
                .build();

        // Visualize graph
        var graphVisualizer = new GraphVisualizer<>(fileName,
                builder,
                true,
                true,
                true);
        graphVisualizer.displaySingleGraph();
        //graphVisualizer.saveGraphToImage(Paths.get("Type the path here" + fileName + ".png"));

        var numOfEdges = builder.edgeCount();
        var numOfVertices = builder.vertexCount();
        System.out.printf("Number of nodes: %s, Number of edges: %s\n",
                numOfVertices,
                numOfEdges);

        System.out.println();

        // Map each vertex to an internal index using VertexIndexMapping.
        var mapper = new VertexIndexMapping<>(builder);

        // Represent the graph as adjacent list
        var representationAsAdjacentList = new ImmutableAdjacentList<>(builder, mapper);

        System.out.println("The Adjacent List of G : ");
        representationAsAdjacentList.getAdjacentMap().entrySet().forEach(System.out::println);
        System.out.println();

        // Represent the graph as a matrix
        var representationAsMatrix = new ImmutableAdjacentMatrix<>(builder, mapper.getVertexToIndex());

        System.out.println("The Adjacent Matrix of G : ");
        var matrix = representationAsMatrix.getMatrix();
        Arrays.stream(matrix).forEach(row -> System.out.println(Arrays.toString(row)));
        System.out.println();

        System.out.println("The Embedding of G : ");
        int k = 15;
        var lowApproximation = new SimpleSVDApproximation<>(representationAsMatrix, k);
        var embedding = lowApproximation.getEmbeddings();
        System.out.println(embedding);
        System.out.println();

        // Convert simpleMatrix to two-dimensional array
        var embeddingAsMatrix = new double[embedding.getNumRows()][embedding.getNumCols()];
        for (var i = 0; i < embedding.getNumRows(); i++) {
            for  (var j = 0; j < embedding.getNumCols(); j++) {
                embeddingAsMatrix[i][j] = embedding.get(i, j);
            }
        }

        // Store the values as id (since node) and the embedding value
        var map = new HashMap<Integer, double[]>();
        for (var i = 0; i < embeddingAsMatrix.length; i++) {
            map.put(i + 1, embeddingAsMatrix[i]);
        }

        // Export the learned embeddings to Karate_embeddings.csv
        var stringPath = "C:\\Users\\moham\\OneDrive\\Desktop\\TrunctedSVD_" + fileName + ".csv";
        new CSVNodeEmbeddingExporter<Integer>().saveNodeEmbeddings(Paths.get(stringPath), map);
    }
}
