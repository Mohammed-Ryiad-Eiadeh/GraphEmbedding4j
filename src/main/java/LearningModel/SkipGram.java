package LearningModel;

import SampleDataset.Sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.IntStream;

public class SkipGram {
    private final int numOfNode;
    private final ArrayList<Sample> dataSamples;
    private final int embeddingDimension;
    private final int numOfEpochs;
    private final double learningRate;
    private final long seed;
    private final HashMap<Integer, double[]> Embeddings;

    public SkipGram(int numOfNodes, ArrayList<Sample> dataSamples, int embeddingDimension, int numOfEpochs, double learningRate, long seed) {
        this.numOfNode = numOfNodes;
        this.dataSamples = new ArrayList<>(dataSamples);
        if (embeddingDimension < 1) {
            throw new IllegalArgumentException("The number of the embedding dimension size have to be positive integer");
        }
        this.embeddingDimension = embeddingDimension;
        if (numOfEpochs < 1) {
            throw new IllegalArgumentException("The number of the epochs have to be positive integer");
        }
        this.numOfEpochs = numOfEpochs;
        if (learningRate < Double.MIN_VALUE) {
            throw new IllegalArgumentException("The number of the embedding dimension size have to be positive");
        }
        if (seed < 1) {
            throw new IllegalArgumentException("The value seed have to be positive integer");
        }
        this.seed = seed;
        this.learningRate = learningRate;
        Embeddings = new HashMap<>(initializeEmbedding());
    }

    private HashMap<Integer, double[]> initializeEmbedding() {
        HashMap<Integer, double[]> embeddings = new HashMap<>();
        Random random = new Random(seed);
        for (int node = 0; node < numOfNode; node++) {
            double[] embeddingVec = new double[embeddingDimension];
            for (int embeddingElementIndex = 0; embeddingElementIndex < embeddingDimension; embeddingElementIndex++) {
                embeddingVec[embeddingElementIndex] = random.nextDouble() * 0.01;
            }
            embeddings.put(node + 1, embeddingVec);
        }
        return embeddings;
    }

    public void trainModel() {
        for (int iter = 0; iter < this.numOfEpochs; iter++) {
            for (Sample instance : this.dataSamples) {
                int targetNode = instance.targetNode();
                int contextNode = instance.contextNode();
                double[] targetNodeEmbedding = Embeddings.get(targetNode);
                double[] contextNodeEmbedding = Embeddings.get(contextNode);

                double[] targetNodeGradient = computeGradient(targetNodeEmbedding, contextNodeEmbedding, instance.label());
                double[] contextNodeGradient = computeGradient(contextNodeEmbedding, targetNodeEmbedding, instance.label());

                updateEmbeddings(instance.targetNode(), targetNodeGradient);
                updateEmbeddings(instance.contextNode(), contextNodeGradient);
            }
            System.out.println("Epoch " + iter + " completed.");
        }
    }

    private void updateEmbeddings(int nodeIndex, double[] gradient) {
        double[] newEmbedding = new double[embeddingDimension];
        for (int embeddingElementIndex = 0; embeddingElementIndex < embeddingDimension; embeddingElementIndex++) {
            newEmbedding[embeddingElementIndex] = Embeddings.get(nodeIndex)[embeddingElementIndex]
                    + this.learningRate
                    * gradient[embeddingElementIndex];
        }
        Embeddings.replace(nodeIndex, newEmbedding);
    }

    private double[] computeGradient(double[] embedding1, double[] embedding2, String label) {
        double[] gradient = new double[embeddingDimension];
        double dotProduct = IntStream.range(0, embeddingDimension)
                .mapToDouble(i -> embedding1[i] * embedding2[i])
                .sum();

        double prediction = sigmoid(dotProduct);
        double groundTruth = label.equals("positive") ? 1 : 0;
        double error = groundTruth - prediction;

        for (int embeddingElementIndex = 0; embeddingElementIndex < embeddingDimension; embeddingElementIndex++) {
            gradient[embeddingElementIndex] = error * embedding2[embeddingElementIndex];
        }
        return gradient;
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public HashMap<Integer, double[]> getEmbeddings() {
        return Embeddings;
    }
}
