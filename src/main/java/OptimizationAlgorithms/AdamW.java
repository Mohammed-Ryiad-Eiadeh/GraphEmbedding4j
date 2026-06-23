package OptimizationAlgorithms;

import java.util.HashMap;

/**
 * AdamW optimizer.
 * <p>
 * This optimizer extends Adam by decoupling weight decay from the
 * gradient-based parameter update, improving regularization.
 */
public non-sealed class AdamW implements Optimizer {
    private final double beta1;
    private final double beta2;
    private final double learningRate;
    private final double epsilon;
    private final double lambda;
    private final HashMap<double[], double[]> m_tVector;
    private final HashMap<double[], double[]> v_tVector;
    private long timeStamp;

    /**
     * Constructs an AdamW optimizer.
     *
     * @param beta1        the exponential decay rate for the first moment
     * @param beta2        the exponential decay rate for the second moment
     * @param learningRate the learning rate used for updates
     * @param epsilon      a small value for numerical stability
     * @param lambda       the weight decay coefficient
     */
    public AdamW(double beta1, double beta2, double learningRate, double epsilon, double lambda) {
        if (beta1 < 0 || beta1 > 1) {
            throw new IllegalArgumentException("Beta1 must be between 0 and 1");
        }
        this.beta1 = beta1;

        if (beta2 < 0 || beta2 > 1) {
            throw new IllegalArgumentException("Beta2 must be between 0 and 1");
        }
        this.beta2 = beta2;

        if (learningRate < 0 || learningRate > 1) {
            throw new IllegalArgumentException("LearningModel rate must be between 0 and 1");
        }
        this.learningRate = learningRate;

        if (epsilon < 1e-12 || epsilon > 1e-6) {
            throw new IllegalArgumentException("Epsilon must be in [1e-12, 1e-6]");
        }
        this.epsilon = epsilon;

        if (lambda < 0 || lambda > 1) {
            throw new IllegalArgumentException("Lambda rate must be between 0 and 1");
        }
        this.lambda = lambda;

        this.m_tVector = new HashMap<>();
        this.v_tVector = new HashMap<>();
        this.timeStamp = 0L;
    }

    /**
     * Updates the model parameters using the computed gradient.
     *
     * @param weights  the parameter vector to be updated
     * @param gradient the computed gradient vector
     */
    @Override
    public void update(double[] weights, double[] gradient) {
        timeStamp++;

        double[] m_t = m_tVector.computeIfAbsent(weights,
                V -> new double[gradient.length]);

        double[] v_t = v_tVector.computeIfAbsent(weights,
                V -> new double[gradient.length]);

        for (int i = 0; i < weights.length; i++) {
            m_t[i] = beta1 * m_t[i] + (1 - beta1) * gradient[i];
            v_t[i] = beta2 * v_t[i] + (1 - beta2) * Math.pow(gradient[i], 2);
            double m_hat = m_t[i] / (1 - Math.pow(beta1, timeStamp));
            double v_hat = v_t[i] / (1 - Math.pow(beta2, timeStamp));
            weights[i] = weights[i] - learningRate * lambda * weights[i];
            weights[i] -= learningRate * (m_hat / (Math.sqrt(v_hat + epsilon)));
        }
    }
}
