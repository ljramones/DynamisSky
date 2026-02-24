package org.dynamissky.core.stars;

/**
 * Minimal star catalog metadata for scaffolding and tests.
 */
public record StarCatalog(int count) {
    public static final StarCatalog EMPTY = new StarCatalog(0);

    public StarCatalog {
        if (count < 0) {
            throw new IllegalArgumentException("count must be >= 0");
        }
    }
}
