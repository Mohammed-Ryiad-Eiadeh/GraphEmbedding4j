package LearningModel;

import ActivationFunction.Activation;
import EmbeddingInitialization.Initializer;
import OptimizationAlgorithms.Optimizer;
import PositiveNegativeSampling.Sampler;
import PositiveNegativeSampling.PositiveNegativeSamples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Implements a Skip-Gram model for learning node embeddings from positive
 * and negative node-context samples generated from random walks.
 *
 * @param <V> the vertex type used in the input graph
 */
public class SkipGram<V> {
    private final int embeddingDimension;
    private final ArrayList<Sampler> dataSamplers;
    private final Optimizer optimizer;
    private final Activation activationFunction;
    private final int numOfEpochs;
    private final HashMap<Integer, double[]> Embeddings;

    /**
     * Constructs a Skip-Gram model using initialized embeddings, training samples,
     * an optimizer, an activation function, and the number of training epochs.
     *
     * @param embeddingInitializer initializes the node embedding vectors
     * @param positiveNegativeSamples generates positive and negative training samples
     * @param optimizer the optimization algorithm used to update embeddings
     * @param activationFunction the activation function applied to dot-product scores
     * @param numOfEpochs the number of training epochs
     */
    public SkipGram(Initializer<V> embeddingInitializer, PositiveNegativeSamples<V> positiveNegativeSamples, Optimizer optimizer, Activation activationFunction, int numOfEpochs) {
        Objects.requireNonNull(positiveNegativeSamples, "positiveNegativeSamples cannot be null");
        this.dataSamplers = new ArrayList<>(positiveNegativeSamples.generatePositiveNegativeSampleDataset());

        Objects.requireNonNull(optimizer, "optimizer cannot be null");
        this.optimizer = optimizer;

        Objects.requireNonNull(activationFunction, "activationFunction cannot be null");
        this.activationFunction = activationFunction;

        if (numOfEpochs < 1) {
            throw new IllegalArgumentException("The number of the epochs have to be positive integer");
        }
        this.numOfEpochs = numOfEpochs;

        Objects.requireNonNull(embeddingInitializer, "embeddingInitializer cannot be null");
        Embeddings = new HashMap<>(embeddingInitializer.initializeEmbedding());

        this.embeddingDimension = embeddingInitializer.getEmbeddingDimension();
    }

    /**
     * Trains the embedding model by iterating over all training samples for a
     * fixed number of epochs. For each target-context pair, gradients are computed and applied to update both node embeddings.
     */
    public void trainModel() {
        for (int iter = 0; iter < this.numOfEpochs; iter++) {
            for (Sampler instance : this.dataSamplers) {
                int contextNode = instance.contextNode();
                int targetNode = instance.targetNode();

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

    /**
     * Updates the embedding vector of a given node using the configured optimizer.
     *
     * @param nodeIndex the index of the node whose embedding is updated
     * @param gradient the gradient vector used to update the node embedding
     */
    private void updateEmbeddings(int nodeIndex, double[] gradient) {
        double[] embeddings = Embeddings.get(nodeIndex);
        optimizer.update(embeddings, gradient);
        Embeddings.replace(nodeIndex, embeddings);
    }

    /**
     * Computes the gradient for an embedding vector based on the prediction
     * error of a target-context pair.
     *
     * @param embeddings1 the embedding vector to be updated
     * @param embeddings2 the neighboring/context embedding vector
     * @param label the sample label indicating a positive or negative pair
     * @return the computed gradient vector for the embedding update
     */
    private double[] computeGradient(double[] embeddings1, double[] embeddings2, String label) {
        double[] gradient = new double[embeddingDimension];

        double dotProduct = IntStream.range(0, embeddingDimension)
                .mapToDouble(i -> embeddings1[i] * embeddings2[i])
                .sum();

        double prediction = activationFunction.applyAsDouble(dotProduct);
        double groundTruth = label.equals("Positive PositiveNegativeSampling") ? 1 : 0;

        //double error = groundTruth - prediction;      // this version enforces the use of Ascent based approaches
        double error = prediction - groundTruth;        // this version enforces the use of Descent based approaches

        for (int i = 0; i < embeddingDimension; i++) {
            gradient[i] = error * embeddings2[i];
        }

        return gradient;
    }

    public HashMap<Integer, double[]> getEmbeddings() {
        return Embeddings;
    }
}
