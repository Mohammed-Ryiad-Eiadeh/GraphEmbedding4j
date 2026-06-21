package PositiveNegativeSampling;

/**
 * Immutable value object representing a pair of vertices,
 * used to model (target, context) relationships.
 */
public record TrainingPair(int v1, int v2) { }
