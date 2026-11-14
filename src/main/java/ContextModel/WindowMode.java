package ContextModel;

/**
 * Direction of the sliding window relative to the center node in a walk.
 */
public enum WindowMode {
    Symmetric, // Uses both left and right neighbors.
    Left,      // Uses only neighbors before the center element.
    Right      // Uses only neighbors after the center element.
}
