package ContextModel;

/**
 * Selects which side of a target contributes context nodes from a walk.
 */
public enum WindowMode {
    Symmetric, // Uses both left and right neighbors.
    Left,      // Uses only neighbors before the center element.
    Right      // Uses only neighbors after the center element.
}
