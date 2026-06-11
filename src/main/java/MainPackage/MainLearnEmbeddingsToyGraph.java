package MainPackage;

import ContextModel.ContextStrategy.WindowMode;
import ContextModel.SlidingWindow;
import Core.GraphBuilder;
import Core.GraphType;
import Core.VertexIndexMapping;
import LearningModel.ActivationFunction.SigmoidFunction;
import LearningModel.EmbeddingInitialization.RandomUniformInitializer;
import LearningModel.OptimizationAlgorithms.SGD;
import LearningModel.Output.EmbeddingExporter;
import LearningModel.SkipGram;
import NegativeSamplingModel.UniformNegativeSample;
import SampleDataset.PositiveAndNegativeSamples;
import WalkModel.DeepWalk;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

public class MainLearnEmbeddingsToyGraph {
    public static void main(String[] args) throws IOException {
        // Construct graph manually
        var graphBuilder = new GraphBuilder<String>(GraphType.Directed);
        graphBuilder.addConnection("v1", "v2", 1.0f);
        graphBuilder.addConnection("v2", "vm4", 1.0f);
        graphBuilder.addConnection("v2", "vm5", 1.0f);
        graphBuilder.addConnection("v3", "v1", 1.0f);
        graphBuilder.addConnection("v3", "vm4", 1.0f);
        graphBuilder.addConnection("vm4", "vm5", 1.0f);

        // Build a directed graph using GraphBuilder.
        var builder = graphBuilder
                .ifNotEmpty()
                .build();

        // Map each vertex to an internal index using VertexIndexMapping.
        var mapper = new VertexIndexMapping<>(builder);

        var deepWalk = new DeepWalk<>(builder,
                mapper,
                10,
                3,
                12345L);

        deepWalk.getRandomWalks().forEach(System.out::println);

        // Create positive and negative samples using 1) symmetric sliding window and 2) uniform negative sampling
        var positiveNegativeSampling = new PositiveAndNegativeSamples<>(deepWalk,
                new SlidingWindow(WindowMode.Symmetric, 3),
                new UniformNegativeSample<>(mapper),
                15,
                false,
                12345L);

        // Initialize node embeddings using random uniform initialization.
        var embeddingInitializer = new RandomUniformInitializer<>(builder,
                256,
                12345L);

        // Define a Skip-Gram model
        var skipGram =  new SkipGram<>(embeddingInitializer,
                positiveNegativeSampling,
                new SGD(0.01),
                new SigmoidFunction(),
                100);

        // Train skip-gram
        skipGram.trainModel();

        var embeddings = new HashMap<>(skipGram.getEmbeddings());

        // Export the learned embeddings to Karate_embeddings.csv
        var path = Paths.get("C:\\Users\\moham\\OneDrive\\Desktop\\toyGraph_embeddings.csv");
        new EmbeddingExporter<String>().saveEmbeddings(path, mapper, embeddings);
    }
}
