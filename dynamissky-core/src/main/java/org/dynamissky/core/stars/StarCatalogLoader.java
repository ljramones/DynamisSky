package org.dynamissky.core.stars;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads star catalog resources for rendering.
 */
public final class StarCatalogLoader {
    private static final int BYTES_PER_STAR = 32;

    private StarCatalogLoader() {
    }

    public static StarCatalog load(Path path) {
        if (path == null || !Files.exists(path)) {
            return StarCatalog.EMPTY;
        }

        try {
            long size = Files.size(path);
            if (size <= 0) {
                return StarCatalog.EMPTY;
            }
            int count = (int) java.lang.Math.max(0, size / BYTES_PER_STAR);
            return new StarCatalog(count);
        } catch (IOException ignored) {
            return StarCatalog.EMPTY;
        }
    }
}
