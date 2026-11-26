package LearningModel;

import ActivationFunction.Activation;
import EmbeddingInitialization.Initializer;
import OptimizationAlgorithms.Optimizer;
import PositiveNegativeSampling.Sample;
import PositiveNegativeSampling.PositiveNegativeSample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Learns separate target and context vectors by classifying positive and negative node pairs.
 *
 * @param <V> the vertex type used in the input graph
 */
public class SkipGram<V> {
    private static final String POSITIVE_LABEL = "Positive PositiveNegativeSampling";
    private static final String NEGATIVE_LABEL = "Negative PositiveNegativeSampling";
    private final int embeddingDimension;
    private final ArrayList<Sample> dataSamples;
    private final Optimizer optimizer;
    private final Activation activationFunction;
    private final int numOfEpochs;
    private final HashMap<Integer, double[]> targetEmbeddings;
    private final HashMap<Integer, double[]> contextEmbeddings;

    /**
     * Initializes embeddings and training samples for the SGNS learning process.
     *
     * @param embeddingInitializer embedding initialization strategy
     * @param positiveNegativeSample training sample generator
     * @param optimizer gradient descent optimizer
     * @param activationFunction sigmoid activation function
     * @param numOfEpochs number of training epochs
     */
    public SkipGram(Initializer<V> embeddingInitializer, PositiveNegativeSample<V> positiveNegativeSample, Optimizer optimizer, Activation activationFunction, int numOfEpochs) {
        Objects.requireNonNull(embeddingInitializer, "embeddingInitializer cannot be null");

        Objects.requireNonNull(positiveNegativeSample, "positiveNegativeSample cannot be null");

        this.optimizer = Objects.requireNonNull(optimizer, "optimizer cannot be null");

        this.activationFunction = Objects.requireNonNull(activationFunction, "activationFunction cannot be null");

        if (numOfEpochs < 1) {
            throw new IllegalArgumentException("Number of epochs must be positive");
        }

        this.numOfEpochs = numOfEpochs;

        this.embeddingDimension = embeddingInitializer.getEmbeddingDimension();

        if (embeddingDimension < 1) {
            throw new IllegalArgumentException("Embedding dimension must be positive");
        }

        this.dataSamples = new ArrayList<>(positiveNegativeSample.generatePositiveNegativeSampleDataset());

        // Initialize target embeddings.
        this.targetEmbeddings = new HashMap<>();

        Map<Integer, double[]> initialized = embeddingInitializer.initializeEmbedding();

        for (Map.Entry<Integer, double[]> entry : initialized.entrySet()) {
            if (entry.getValue() == null || entry.getValue().length != embeddingDimension) {
                throw new IllegalArgumentException("Invalid embedding dimension for node " + entry.getKey());
            }
            targetEmbeddings.put(entry.getKey(), entry.getValue().clone());
        }

        // Initialize context embeddings to zero.
        // They are trained independently of target embeddings.
        this.contextEmbeddings = new HashMap<>();

        for (Integer node : targetEmbeddings.keySet()) {
            contextEmbeddings.put(node, new double[embeddingDimension]);
        }

        long numOfPositiveSamples = dataSamples.stream().filter(sample -> sample.label().equals(POSITIVE_LABEL)).count();
        long numOfNegativeSamples = dataSamples.stream().filter(sample -> sample.label().equals(NEGATIVE_LABEL)).count();

        // Display num of walks / Samples / Positive Samples / Negative Samples
        System.out.printf("Num of Samples, %d%n", dataSamples.size());
        System.out.printf("Num of Positive Samples, %d%n", numOfPositiveSamples);
        System.out.printf("Num of Negative Samples, %d%n", numOfNegativeSamples);
    }

    /**
     * Updates target and context vectors over the configured epochs using pair labels as supervision.
     */
    public void trainModel() {
        for (int epoch = 0; epoch < numOfEpochs; epoch++) {
            double totalLoss = 0.0;
            for (Sample sample : dataSamples) {
                int targetNode = sample.targetNode();
                int contextNode = sample.contextNode();

                double[] target = targetEmbeddings.get(targetNode);
                double[] context = contextEmbeddings.get(contextNode);

                if (target == null || context == null) {
                    throw new IllegalArgumentException("Unknown target or context node: " + targetNode + ", " + contextNode);
                }

                // Convert sample label to ground truth.
                double groundTruth;
                if (POSITIVE_LABEL.equals(sample.label())) {
                    groundTruth = 1.0;
                } else if (NEGATIVE_LABEL.equals(sample.label())) {
                    groundTruth = 0.0;
                } else {
                    throw new IllegalArgumentException("Unknown sample label: " + sample.label());
                }

                // Compute the raw score s as the dot product of the target and context vectors.
                double dotProduct = IntStream.range(0, embeddingDimension).mapToDouble(i -> target[i] * context[i]).sum();

                // Apply the activation function to map the score to a prediction p = f(s).
                double prediction = activationFunction.applyAsDouble(dotProduct);

                // Compute the activation derivative dp/ds = f'(s).
                double derivative = activationFunction.derivative(dotProduct);

                // Compute the BCE error term (p − y), the numerator of dL/dp.
                double error = prediction - groundTruth;

                // Apply the chain rule: dL/ds = dL/dp · dp/ds = (p − y) / (p(1 − p)) · f'(s).
                double delta = error * derivative / (prediction * (1.0 - prediction));

                // Allocate the gradient of the loss with respect to the target vector.
                double[] targetGradient = new double[embeddingDimension];

                // Allocate the gradient of the loss with respect to the context vector.
                double[] contextGradient = new double[embeddingDimension];

                // Compute both gradients from the OLD vectors: dL/d(target) = delta · context, dL/d(context) = delta · target.
                for (int i = 0; i < embeddingDimension; i++) {
                    targetGradient[i] = delta * context[i];
                    contextGradient[i] = delta * target[i];
                }

                // Update the target vector using the optimizer.
                optimizer.update(target, targetGradient);

                // Update the context vector using the optimizer.
                optimizer.update(context, contextGradient);

                // Numerically stable BCE loss from the logit.
                // L = max(z,0) - (y * z) + log(1 + exp(-abs(z)))
                totalLoss += Math.max(dotProduct, 0.0)
                        - groundTruth * dotProduct
                        + Math.log1p(Math.exp(-Math.abs(dotProduct)));
            }

            double averageLoss = dataSamples.isEmpty() ? 0.0 : totalLoss / dataSamples.size();

            System.out.printf("Epoch %d/%d - Average Loss: %.6f%n", epoch + 1, numOfEpochs, averageLoss);
        }
    }

    /**
     * Returns defensive copies of the learned node representations used as targets.
     *
     * @return defensive copy of target embeddings
     */
    public HashMap<Integer, double[]> getEmbeddings() {
        HashMap<Integer, double[]> result = new HashMap<>();

        for (Map.Entry<Integer, double[]> entry : targetEmbeddings.entrySet()) {
            result.put(entry.getKey(), entry.getValue().clone());
        }
        return result;
    }

    /**
     * Returns defensive copies of the learned context-side representations.
     *
     * @return defensive copy of context embeddings
     */
    public HashMap<Integer, double[]> getContextEmbeddings() {
        HashMap<Integer, double[]> result = new HashMap<>();

        for (Map.Entry<Integer, double[]> entry : contextEmbeddings.entrySet()) {
            result.put(entry.getKey(), entry.getValue().clone());
        }
        return result;
    }
}
