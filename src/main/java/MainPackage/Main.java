package MainPackage;

import ContextModel.ContextStrategy.WindowMode;
import ContextModel.SlidingWindow;
import Core.GraphBuilder;
import Core.GraphType;
import Core.VertexIndexMapping;
import LearningModel.ActivationFunction.TanhFunction;
import LearningModel.EmbeddingInitialization.RandomUniformInitializer;
import LearningModel.OptimizationAlgorithms.MomentumSGD;
import LearningModel.SkipGram;
import NegativeSamplingModel.UniformNegativeSample;
import SampleDataset.PositiveAndNegativeSamples;
import WalkModel.DeepWalk;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        var graphDataFile = Paths.get(System.getProperty("user.dir"), "Graphs", "DER.txt");
        var graphReader = Files.newBufferedReader(graphDataFile);

        var graphBuilder = new GraphBuilder<Integer>(GraphType.Directed);
        var headerLineId = 1;

        graphReader.lines().skip(headerLineId).forEach(line -> {
            String[] currentLine = line.trim().split(" ");
            var source = Integer.parseInt(currentLine[0]);
            var destination = Integer.parseInt(currentLine[1]);
            var weight = currentLine.length >= 3 ? Float.parseFloat(currentLine[2]) : 1.0f;
            graphBuilder.addConnection(source, destination, weight);
        });
        graphReader.close();

        var builder = graphBuilder
                .ifNotEmpty()
                .build();

        var numOfEdges = builder.edgeCount();
        var numOfVertices = builder.vertexCount();
        System.out.printf("Number of nodes: %s, Number of edges: %s\n",
                numOfVertices,
                numOfEdges);

        var mapper = new VertexIndexMapping<>(builder);

        var deepWalk = new DeepWalk<>(builder,
                mapper,
                100,
                4,
                12345L);

        var positiveNegativeSample = new PositiveAndNegativeSamples<>(deepWalk,
                new SlidingWindow(WindowMode.Symmetric, 3),
                new UniformNegativeSample<>(mapper, 12345L),
                false,
                12345L);

        var uniformEmbeddingInitializer = new RandomUniformInitializer<>(builder,
                256,
                12345L);

        var skipGramModel = new SkipGram<>(uniformEmbeddingInitializer,
                positiveNegativeSample,
                new MomentumSGD(0.05, 0.9),
                new TanhFunction(),
                20);

        skipGramModel.trainModel();

        var embeddings = new HashMap<>(skipGramModel.getEmbeddings());

        embeddings.forEach((key, value) -> System.out.println("Embedding: " + mapper.getVertex(key)
                + "\t" + Arrays.toString(value)));
    }
}

