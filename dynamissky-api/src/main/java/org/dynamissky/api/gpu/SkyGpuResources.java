package org.dynamissky.api.gpu;

/**
 * GPU resources produced by the active sky implementation.
 */
public record SkyGpuResources(
        GpuImage2D transmittanceLut,
        GpuImage2D multiScatteringLut,
        GpuImage2D skyViewLut,
        GpuImage3D aerialPerspectiveLut,
        long starFieldVertexBufferHandle) {

    public SkyGpuResources {
        if (transmittanceLut == null || multiScatteringLut == null || skyViewLut == null || aerialPerspectiveLut == null) {
            throw new IllegalArgumentException("all LUT resources are required");
        }
    }
}
