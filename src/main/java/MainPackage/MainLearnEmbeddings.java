package MainPackage;

import ContextModel.ContextStrategy.WindowMode;
import ContextModel.SlidingWindow;
import Core.GraphBuilder;
import Core.GraphType;
import Core.VertexIndexMapping;
import LearningModel.ActivationFunction.SigmoidFunction;
import LearningModel.EmbeddingInitialization.RandomUniformInitializer;
import LearningModel.OptimizationAlgorithms.MomentumSGD;
import LearningModel.Output.EmbeddingExporter;
import LearningModel.SkipGram;
import NegativeSamplingModel.UniformNegativeSample;
import SampleDataset.PositiveAndNegativeSamples;
import WalkModel.DeepWalk;
import org.tribuo.util.Util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class MainLearnEmbeddings {
    public static void main(String[] args) throws Exception {
        // Load the graph from Graphs
        var graphDataFile = Paths.get(System.getProperty("user.dir"), "Graphs", "karate.txt");
        var graphReader = Files.newBufferedReader(graphDataFile);

        var graphBuilder = new GraphBuilder<Integer>(GraphType.Directed);
        var headerLineId = 1;

        graphReader.lines().skip(headerLineId).forEach(line -> {
            String[] currentLine = line.trim().split("\\s+");
            var source = Integer.parseInt(currentLine[0]);
            var destination = Integer.parseInt(currentLine[1]);
            var weight = currentLine.length >= 3 ? Float.parseFloat(currentLine[2]) : 1.0f;
            graphBuilder.addConnection(source, destination, weight);
        });
        graphReader.close();

        // Build a directed graph using GraphBuilder.
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

        // Generate random walks using DeepWalk.
        var deepWalk = new DeepWalk<>(builder,
                mapper,
                10,
                4,
                12345L);

        // Create positive and negative samples using 1) symmetric sliding window and 2) uniform negative sampling
        var positiveNegativeSample = new PositiveAndNegativeSamples<>(deepWalk,
                new SlidingWindow(WindowMode.Symmetric, 3),
                new UniformNegativeSample<>(mapper),
                20,
                false,
                12345L);

        // Initialize node embeddings using random uniform initialization.
        var uniformEmbeddingInitializer = new RandomUniformInitializer<>(builder,
                256,
                12345L);

        // Define a Skip-Gram model
        var skipGramModel = new SkipGram<>(uniformEmbeddingInitializer,
                positiveNegativeSample,
                new MomentumSGD(0.01, 0.9),
                new SigmoidFunction(),
                100);

        // Train the model
        long startTime = System.currentTimeMillis();
        skipGramModel.trainModel();
        long endTime = System.currentTimeMillis();

        System.out.println("Training time: " + Util.formatDuration(startTime, endTime) + " ms");

        // Export the learned embeddings to Karate_embeddings.csv
        var embeddings = new HashMap<>(skipGramModel.getEmbeddings());
        var stringPath = "C:\\Users\\moham\\OneDrive\\Desktop\\karkar.csv";
        new EmbeddingExporter<Integer>().saveEmbeddings(Paths.get(stringPath),
                mapper,
                embeddings);
    }
}

