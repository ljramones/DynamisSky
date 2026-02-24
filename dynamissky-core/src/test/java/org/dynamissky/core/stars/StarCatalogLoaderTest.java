package org.dynamissky.core.stars;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertSame;

class StarCatalogLoaderTest {
    @Test
    void missingFileReturnsEmptyCatalog() {
        StarCatalog catalog = StarCatalogLoader.load(Path.of("/definitely/missing/athyg_v3.bin"));
        assertSame(StarCatalog.EMPTY, catalog);
    }
}
