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
import org.tribuo.MutableDataset;
import org.tribuo.Trainer;
import org.tribuo.classification.LabelFactory;
import org.tribuo.classification.evaluation.LabelEvaluator;
import org.tribuo.classification.sgd.fm.FMClassificationTrainer;
import org.tribuo.classification.sgd.objectives.Hinge;
import org.tribuo.data.csv.CSVLoader;
import org.tribuo.evaluation.CrossValidation;
import org.tribuo.math.optimisers.AdaGrad;
import org.tribuo.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        // Load the graph from Graphs/Karate.txt.
        var graphDataFile = Paths.get(System.getProperty("user.dir"), "Graphs", "Karate.txt");
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
                new UniformNegativeSample<>(mapper, 12345L),
                false,
                12345L);

        // Initialize node embeddings using random uniform initialization.
        var uniformEmbeddingInitializer = new RandomUniformInitializer<>(builder,
                256,
                12345L);

        // Define a Skip-Gram model
        var skipGramModel = new SkipGram<>(uniformEmbeddingInitializer,
                positiveNegativeSample,
                new SGD(0.01),
                new SigmoidFunction(),
                100);

        // Train the model
        skipGramModel.trainModel();

        var embeddings = new HashMap<>(skipGramModel.getEmbeddings());

        // Export the learned embeddings to Karate_embeddings.csv
        var stringPath = "C:\\Users\\moham\\OneDrive\\Desktop\\Karate_embeddings.csv";
        new EmbeddingExporter<Integer>().saveEmbeddings(Paths.get(stringPath),
                mapper,
                embeddings);
    }
}

class Test {
    public static void main(String[] args) throws IOException {
        // read the entire dataset after the FS process
        var dataPath = "C:\\Users\\moham\\OneDrive\\Desktop\\Karate_embeddings set.csv";
        var dataSource = new CSVLoader<>(new LabelFactory()).loadDataSource(Paths.get(dataPath), "Class");
        var Data = new MutableDataset<>(dataSource);

        // use FM classifier
        var FMTrainer = new FMClassificationTrainer(new Hinge(),
                new AdaGrad(0.01, 0.9),
                50,
                Trainer.DEFAULT_SEED,
                10,
                0.2D);

        // use cross validation
        var crossValidation = new CrossValidation<>(FMTrainer,
                Data,
                new LabelEvaluator(),
                7);

        // get outputs
        var avgAcc = 0D;
        var avgRecall = 0D;
        var avgF1 = 0D;
        var avgPrecision = 0D;
        var sTrain = System.currentTimeMillis();
        for (var performance : crossValidation.evaluate()) {
            avgAcc += performance.getA().accuracy();
            avgRecall += performance.getA().macroAveragedRecall();
            avgF1 += performance.getA().macroAveragedF1();
            avgPrecision += performance.getA().macroAveragedPrecision();
        }
        var eTrain = System.currentTimeMillis();

        System.out.println("The Training_Testing duration time is : " + Util.formatDuration(sTrain, eTrain));
        System.out.println("The average accuracy is : " + avgAcc / crossValidation.getK());
        System.out.println("The average recall is : " + avgRecall / crossValidation.getK());
        System.out.println("The average F1-Score is : " + avgF1 / crossValidation.getK());
        System.out.println("The average precision is : " +avgPrecision / crossValidation.getK());
    }
}

