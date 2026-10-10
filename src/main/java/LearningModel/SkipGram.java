
package LearningModel;

import ActivationFunction.Activation;
import EmbeddingInitialization.Initializer;
import OptimizationAlgorithms.Optimizer;
import PositiveNegativeSampling.Sampler;
import PositiveNegativeSampling.PositiveNegativeSamples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Implements Skip-Gram with Negative Sampling (SGNS)
 * for learning node embeddings from random walks.
 *
 * Maintains separate target and context embedding matrices
 * and optimizes binary cross-entropy using SGD.
 *
 * @param <V> the vertex type used in the input graph
 */
public class SkipGram<V> {
    private static final String POSITIVE_LABEL = "Positive PositiveNegativeSampling";
    private static final String NEGATIVE_LABEL = "Negative PositiveNegativeSampling";
    private final int embeddingDimension;
    private final ArrayList<Sampler> dataSamplers;
    private final Optimizer optimizer;
    private final Activation activationFunction;
    private final int numOfEpochs;
    // Input embeddings: target-node representations.
    private final HashMap<Integer, double[]> targetEmbeddings;
    // Output embeddings: context-node representations.
    private final HashMap<Integer, double[]> contextEmbeddings;

    /**
     * Constructs the Skip-Gram model.
     *
     * @param embeddingInitializer embedding initialization strategy
     * @param positiveNegativeSamples training sample generator
     * @param optimizer gradient descent optimizer
     * @param activationFunction sigmoid activation function
     * @param numOfEpochs number of training epochs
     */
    public SkipGram(Initializer<V> embeddingInitializer, PositiveNegativeSamples<V> positiveNegativeSamples, Optimizer optimizer, Activation activationFunction, int numOfEpochs) {
        Objects.requireNonNull(embeddingInitializer, "embeddingInitializer cannot be null");
        Objects.requireNonNull(positiveNegativeSamples, "positiveNegativeSamples cannot be null");

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

        this.dataSamplers = new ArrayList<>(positiveNegativeSamples.generatePositiveNegativeSampleDataset());

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
    }

    /**
     * Trains Skip-Gram using positive and negative pairs.
     *
     * For each pair:
     * 1. Calculate target-context dot product.
     * 2. Apply sigmoid.
     * 3. Compute binary cross-entropy gradients.
     * 4. Update target and context embeddings.
     */
    public void trainModel() {
        for (int epoch = 0; epoch < numOfEpochs; epoch++) {
            double totalLoss = 0.0;
            for (Sampler sample : dataSamplers) {
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

                // Compute dot product.
                double dotProduct = IntStream.range(0, embeddingDimension).mapToDouble(i -> target[i] * context[i]).sum();

                // Apply sigmoid.
                double prediction = activationFunction.applyAsDouble(dotProduct);

                // Gradient of BCE with sigmoid.
                double error = prediction - groundTruth;

                // Calculate gradients using the OLD embeddings.
                double[] targetGradient = new double[embeddingDimension];

                double[] contextGradient = new double[embeddingDimension];

                for (int i = 0; i < embeddingDimension; i++) {
                    targetGradient[i] = error * context[i];
                    contextGradient[i] = error * target[i];
                }

                // Apply gradient descent.
                optimizer.update(target, targetGradient);
                optimizer.update(context, contextGradient);

                // Numerically stable BCE loss from the logit.
                // L = max(z,0) - (y * z) + log(1 + exp(-abs(z)))
                totalLoss += Math.max(dotProduct, 0.0)
                        - groundTruth * dotProduct
                        + Math.log1p(Math.exp(-Math.abs(dotProduct)));
            }

            double averageLoss = dataSamplers.isEmpty() ? 0.0 : totalLoss / dataSamplers.size();

            System.out.printf("Epoch %d/%d - Average Loss: %.6f%n", epoch + 1, numOfEpochs, averageLoss);
        }
    }

    /**
     * Returns the learned target-node embeddings.
     *
     * These embeddings can be used for node classification,
     * community detection, and other downstream tasks.
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
     * Returns the learned context-node embeddings.
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
