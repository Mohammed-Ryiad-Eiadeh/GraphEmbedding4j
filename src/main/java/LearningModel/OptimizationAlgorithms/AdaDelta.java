package LearningModel.OptimizationAlgorithms;

import java.util.HashMap;

/**
 * AdaDelta optimizer.
 * <p>
 * Uses exponential averages of squared gradients (Eg2) and squared updates
 * (Edx2) to compute per-parameter step sizes without a global learning rate.
 */
public non-sealed class AdaDelta implements Optimizer {
    private final double rho;
    private final double epsilon;
    private final HashMap<double[], double[]> Eg2Vector;
    private final HashMap<double[], double[]> deltaW;
    private final HashMap<double[], double[]> Edx2Vector;

    /**
     * Constructs an AdaDelta optimizer.
     *
     * @param rho          the rho coefficient in the range [0, 1)
     * @param epsilon      the epsilon coefficient in the range
     * @throws IllegalArgumentException if the learning rate or momentum
     *                                  values are outside valid ranges
     */
    public AdaDelta(double rho, double epsilon) {
        if (rho < 0 || rho >= 1) {
            throw new IllegalArgumentException("Decay rate must be in [0, 1)");
        }
        this.rho = rho;

        if (epsilon < 1e-12 || epsilon > 1e-6) {
            throw new IllegalArgumentException("Epsilon must be in [10^-6, 10^-12]");
        }
        this.epsilon = epsilon;

        this.Eg2Vector = new HashMap<>();
        this.deltaW = new HashMap<>();
        this.Edx2Vector = new HashMap<>();
    }

    /**
     * Updates the model parameters using the computed gradient.
     *
     * @param weights  the parameter vector to be updated
     * @param gradient the computed gradient vector
     */
    @Override
    public void update(double[] weights, double[] gradient) {
        double[] eg2 = Eg2Vector.computeIfAbsent(weights,
                V -> new double[weights.length]);

        double[] delW = deltaW.computeIfAbsent(weights,
                V -> new double[gradient.length]);

        double[] edx2 = Edx2Vector.computeIfAbsent(weights,
                V -> new double[weights.length]);

        for (int i = 0; i < weights.length; i++) {
            eg2[i] = rho * eg2[i] + (1 - rho) * Math.pow(gradient[i], 2);
            delW[i] = -Math.sqrt(edx2[i] + epsilon) / Math.sqrt(eg2[i] + epsilon) * gradient[i];
            edx2[i] = rho * edx2[i] + (1 - rho) * Math.pow(delW[i], 2);
            weights[i] += delW[i];
        }
    }
}
