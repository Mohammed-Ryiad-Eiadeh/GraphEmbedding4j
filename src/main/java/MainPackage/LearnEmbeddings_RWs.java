package MainPackage;

import ActivationFunction.SigmoidFunction;
import ContextModel.SlidingWindow;
import ContextModel.WindowMode;
import Core.GraphBuilder;
import Core.GraphType;
import Core.VertexIndexMapping;
import EmbeddingInitialization.EmbeddingInitializer;
import EmbeddingInitialization.InitializerMode;
import IO.CSVGraphLoader;
import IO.CSVNodeEmbeddingExporter;
import LearningModel.SkipGram;
import OptimizationAlgorithms.SGD;
import PositiveNegativeSampling.PositiveNegativeSamples;
import PositiveNegativeSampling.UniformNegativeSampler;
import WalkModel.DeepWalk;
import org.tribuo.util.Util;

import java.nio.file.Paths;
import java.util.HashMap;

public class LearnEmbeddings_RWs {
    public static void main(String[] args) throws Exception {
        // Load the graph from Graphs
        var fileName = "polBooks_Edges";
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

        var numOfEdges = builder.edgeCount();
        var numOfVertices = builder.vertexCount();
        System.out.printf("Number of nodes: %s, Number of edges: %s\n",
                numOfVertices,
                numOfEdges);

        System.out.println();

        // Map each vertex to an internal index using VertexIndexMapping.
        var mapper = new VertexIndexMapping<>(builder);

        var walk = new DeepWalk<>(builder,
                mapper,
                20,
                10,
                12345L);

        // Create positive and negative samples using 1) right sliding window and 2) uniform negative sampling
        var positiveNegativeSample = new PositiveNegativeSamples<>(walk,
                new SlidingWindow(WindowMode.Right, 4),
                new UniformNegativeSampler<>(mapper),
                20,
                true,
                12345L);

        // Initialize node embeddings using random Gaussian initialization.
        var embeddingInitializer = new EmbeddingInitializer<>(builder,
                InitializerMode.Uniform,
                256,
                12345L);

        // Define a Skip-Gram model
        var skipGramModel = new SkipGram<>(embeddingInitializer,
                positiveNegativeSample,
                new SGD(0.001),
                new SigmoidFunction(),
                100);

        // Train the model
        long startTime = System.currentTimeMillis();
        skipGramModel.trainModel();
        long endTime = System.currentTimeMillis();

        System.out.println("Training time: " + Util.formatDuration(startTime, endTime) + " ms");

        // Export the learned embeddings to Karate_embeddings.csv
        var embeddings = new HashMap<>(skipGramModel.getEmbeddings());
        var stringPath = "C:\\Users\\moham\\OneDrive\\Desktop\\RWs_" + fileName + ".csv";
        new CSVNodeEmbeddingExporter<Integer>().saveNodeEmbeddings(Paths.get(stringPath),
                mapper,
                embeddings);
    }
}
