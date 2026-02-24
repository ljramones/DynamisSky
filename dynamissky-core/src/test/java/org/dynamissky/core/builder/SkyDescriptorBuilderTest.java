package org.dynamissky.core.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SkyDescriptorBuilderTest {
    @Test
    void missingModelThrows() {
        assertThrows(IllegalStateException.class, () -> SkyDescriptorBuilder.create().build());
    }
}
