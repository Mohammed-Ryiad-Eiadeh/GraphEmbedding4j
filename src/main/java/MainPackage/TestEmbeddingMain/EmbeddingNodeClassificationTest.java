package MainPackage.TestEmbeddingMain;

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
import java.nio.file.Paths;
import java.util.ArrayList;

public class EmbeddingNodeClassificationTest {
    public static void main(String[] args) throws IOException {
        // read the entire dataset after the FS process
        var modelName = "Node2Vec";
        var dataPath = "C:\\Users\\moham\\OneDrive\\Desktop\\" + modelName + "_Wiki_Edges.csv";
        var dataSource = new CSVLoader<>(new LabelFactory()).loadDataSource(Paths.get(dataPath), "Class");
        var Data = new MutableDataset<>(dataSource);

        // use FM classifier
        var FMTrainer = new FMClassificationTrainer(new Hinge(),
                new AdaGrad(0.01, 0.9),
                100,
                Trainer.DEFAULT_SEED,
                10,
                0.2D);

        // use cross validation
        var crossValidation = new CrossValidation<>(FMTrainer,
                Data,
                new LabelEvaluator(),
                3);

        // get outputs
        var sumAcc = 0D;
        var sumRecall = 0D;
        var sumF1 = 0D;
        var sumPrecision = 0D;
        var listAcc = new ArrayList<Double>();
        var sTrain = System.currentTimeMillis();
        for (var performance : crossValidation.evaluate()) {
            sumAcc += performance.getA().accuracy();
            listAcc.add(performance.getA().accuracy());
            sumRecall += performance.getA().macroAveragedRecall();
            sumF1 += performance.getA().macroAveragedF1();
            sumPrecision += performance.getA().macroAveragedPrecision();
        }
        var eTrain = System.currentTimeMillis();

        var avgAcc = sumAcc / crossValidation.getK();
        var avgRecall = sumRecall / crossValidation.getK();
        var avgF1 = sumF1 / crossValidation.getK();
        var avgPrecision = sumPrecision / crossValidation.getK();

        //Standard deviation: Measures how much classification performance varies across those folds.
        var std = Math.sqrt(listAcc.stream()
                .mapToDouble(value -> Math.pow(value - avgAcc, 2))
                .sum() / (listAcc.size() - 1));

        System.out.println("Performance of : " + modelName);
        System.out.println("The Training_Testing duration time is : " + Util.formatDuration(sTrain, eTrain));
        System.out.println("The average accuracy is : " + avgAcc);
        System.out.println("The average Std-accuracy is : " + std);
        System.out.println("The average recall is : " + avgRecall);
        System.out.println("The average F1-Score is : " + avgF1);
        System.out.println("The average precision is : " + avgPrecision);
    }
}
