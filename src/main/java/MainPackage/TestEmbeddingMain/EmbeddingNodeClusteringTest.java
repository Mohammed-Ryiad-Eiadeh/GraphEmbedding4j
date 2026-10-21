package MainPackage.TestEmbeddingMain;

import org.tribuo.MutableDataset;
import org.tribuo.clustering.ClusteringFactory;
import org.tribuo.clustering.evaluation.ClusteringEvaluator;
import org.tribuo.clustering.kmeans.KMeansTrainer;
import org.tribuo.data.csv.CSVLoader;
import org.tribuo.evaluation.TrainTestSplitter;
import org.tribuo.math.distance.L2Distance;

import java.io.IOException;
import java.nio.file.Paths;

public class EmbeddingNodeClusteringTest {
    public static void main(String[] args) throws IOException {
        var modelName = "MB2Vec";
        var dataPath = "C:\\Users\\moham\\OneDrive\\Desktop\\" + modelName + "_cora_Edges.csv";
        var dataSource = new CSVLoader<>(new ClusteringFactory()).loadDataSource(Paths.get(dataPath), "Class");

        // Split data into two portions
        var data = new TrainTestSplitter<>(dataSource,
                0.70,
                KMeansTrainer.DEFAULT_SEED);

        var trainData = new MutableDataset<>(data.getTrain());
        var testData = new MutableDataset<>(data.getTest());

        // Define clustering algorithm (i.e., Kmeans++)
        var kMeans = new KMeansTrainer(7,
                100,
                new L2Distance(),
                KMeansTrainer.Initialisation.PLUSPLUS,
                Runtime.getRuntime().availableProcessors(),
                KMeansTrainer.DEFAULT_SEED);

        // Train the model
        var model = kMeans.train(trainData);

        // Define the evaluator
        var evaluator = new ClusteringEvaluator();

        // Evaluate against training data
        var trainEvaluation = evaluator.evaluate(model, trainData);
        System.out.printf("Evaluation against training data: %s\n", trainEvaluation.toString());

        System.out.println();

        // Evaluate against testing data
        var testEvaluation = evaluator.evaluate(model, testData);
        System.out.printf("Evaluation against test data: %s\n", testEvaluation.toString());
    }
}
