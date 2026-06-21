package OptimizationAlgorithms;

import java.util.HashMap;

/**
 * AdaMax optimizer.
 * <p>
 * This optimizer is a variant of Adam that uses the infinity norm
 * to scale parameter updates.
 */
public non-sealed class AdaMax implements Optimizer {
    private final double beta1;
    private final double beta2;
    private final double learningRate;
    private final double epsilon;
    private final HashMap<double[], double[]> m_tVector;
    private final HashMap<double[], double[]> u_tVector;
    private long timeStamp;

    /**
     * Constructs an AdaMax optimizer.
     *
     * @param beta1        the exponential decay rate for the first moment
     * @param beta2        the exponential decay rate for the infinity norm
     * @param learningRate the learning rate used for updates
     * @param epsilon      a small value for numerical stability
     */
    public AdaMax(double beta1, double beta2, double learningRate, double epsilon) {
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

        this.m_tVector = new HashMap<>();
        this.u_tVector = new HashMap<>();

        this.timeStamp = 0L;
    }

    /**
     * Updates the parameter vector using AdaMax.
     *
     * @param weights  the parameter vector to update
     * @param gradient the gradient vector
     */
    @Override
    public void update(double[] weights, double[] gradient) {
        timeStamp++;

        double[] m_t = m_tVector.computeIfAbsent(weights
                , V -> new double[gradient.length]);

        double[] u_t = u_tVector.computeIfAbsent(weights,
                V -> new double[gradient.length]);

        for (int i = 0; i < weights.length; i++) {
            m_t[i] = beta1 * m_t[i] + (1 - beta1) * gradient[i];
            u_t[i] = Math.max(beta2 * u_t[i], Math.abs(gradient[i]));
            double m_hat = m_t[i] / (1 - Math.pow(beta1, timeStamp));
            weights[i] -= learningRate * (m_hat / (u_t[i] + epsilon));
        }
    }
}
