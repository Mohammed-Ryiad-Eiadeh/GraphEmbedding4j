package MainPackage.LearnEmbeddingMain;

import ActivationFunction.SigmoidFunction;
import ContextModel.SlidingWindow;
import ContextModel.WindowMode;
import Core.GraphBuilder;
import Core.GraphType;
import Core.VertexIndexMapping;
import EmbeddingInitialization.EmbeddingInitializer;
import EmbeddingInitialization.InitializerMode;
import GraphVisualization.GraphVisualizer;
import IO.CSVNodeEmbeddingExporter;
import LearningModel.SkipGram;
import OptimizationAlgorithms.SGD;
import PositiveNegativeSampling.PositiveNegativeSamples;
import PositiveNegativeSampling.UniformNegativeSampler;
import Representation.AdjacentList.ImmutableAdjacentList;
import Representation.matrix.ImmutableAdjacentMatrix;
import WalkModel.DeepWalk;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

public class LearnEmbeddingsToyGraph {
    public static void main(String[] args) throws IOException {
        var fileName = "ToyGraph";

        // Construct graph manually
        var graphBuilder = new GraphBuilder<String>(GraphType.Directed);
        graphBuilder.addConnection("v0", "v1", 1.0f);
        graphBuilder.addConnection("v0", "v2", 1.0f);
        graphBuilder.addConnection("v1", "v2", 1.0f);
        graphBuilder.addConnection("v1", "v3", 1.0f);
        graphBuilder.addConnection("v2", "v3", 1.0f);
        graphBuilder.addConnection("v3", "v4", 1.0f);
        graphBuilder.addConnection("v4", "v5", 1.0f);
        graphBuilder.addConnection("v4", "v6", 1.0f);
        graphBuilder.addConnection("v5", "v6", 1.0f);
        graphBuilder.addConnection("v6", "v7", 1.0f);

        // Build a directed graph using GraphBuilder.
        var builder = graphBuilder
                .ifNotEmpty()
                .build();

        // Visualize graph
        var graphVisualizer = new GraphVisualizer<>("ToyGraph",
                builder,
                true,
                true,
                true);
        graphVisualizer.displaySingleGraph();
        //graphVisualizer.saveGraphToImage(Paths.get("Type the path here" + fileName + ".png"));

        System.out.println("The Edge List of G : ");
        builder.edgeSet().forEach(System.out::println);
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

        var deepWalk = new DeepWalk<>(builder,
                mapper,
                10,
                3,
                12345L);

        System.out.println("The Generated Walks : ");
        deepWalk.getRandomWalks().forEach(System.out::println);
        System.out.println();

        // Create positive and negative samples using 1) symmetric sliding window and 2) uniform negative sampling
        var positiveNegativeSampler = new PositiveNegativeSamples<>(deepWalk,
                new SlidingWindow(WindowMode.Symmetric, 3),
                new UniformNegativeSampler<>(mapper),
                15,
                false,
                12345L);

        // Initialize node embeddings using random Gaussian initialization.
        var embeddingInitialization = new EmbeddingInitializer<>(builder,
                InitializerMode.Gaussian,
                256,
                12345L);

        // Define a Skip-Gram model
        var skipGram =  new SkipGram<>(embeddingInitialization,
                positiveNegativeSampler,
                new SGD(0.01),
                new SigmoidFunction(),
                100);

        // Train skip-gram
        skipGram.trainModel();

        var embeddings = new HashMap<>(skipGram.getEmbeddings());

        // Export the learned embeddings to Karate_embeddings.csv
        var path = Paths.get("C:\\Users\\moham\\OneDrive\\Desktop\\toyGraph_embeddings.csv");
        new CSVNodeEmbeddingExporter<String>().saveNodeEmbeddings(path, mapper, embeddings);
    }
}
