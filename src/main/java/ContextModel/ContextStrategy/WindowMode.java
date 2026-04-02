package ContextModel.ContextStrategy;

/**
 * Defines the direction used when extracting context around a center element.
 */
public enum WindowMode {
    Symmetric,  // Uses both left and right neighbors.
    Left,       // Uses only neighbors before the center element.
    Right,      // Uses only neighbors after the center element.
}
