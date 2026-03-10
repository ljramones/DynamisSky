package org.dynamissky.api.gpu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SkyGpuResourcesTypedHandleTest {
    @Test
    void typedStarFieldBufferRefMatchesLegacyHandle() {
        SkyGpuResources resources = new SkyGpuResources(
                new GpuImage2D(101L, 4, 4),
                new GpuImage2D(102L, 4, 4),
                new GpuImage2D(103L, 4, 4),
                new GpuImage3D(104L, 4, 4, 4),
                105L);

        assertEquals(105L, resources.starFieldVertexBufferHandle());
        assertEquals(105L, resources.starFieldVertexBufferRef().handle());
    }
}
