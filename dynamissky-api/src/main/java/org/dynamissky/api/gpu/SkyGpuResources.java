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

    public static final SkyGpuResources NULL = new SkyGpuResources(
            new GpuImage2D(0L, 1, 1),
            new GpuImage2D(0L, 1, 1),
            new GpuImage2D(0L, 1, 1),
            new GpuImage3D(0L, 1, 1, 1),
            0L);

    public SkyGpuResources {
        if (transmittanceLut == null || multiScatteringLut == null || skyViewLut == null || aerialPerspectiveLut == null) {
            throw new IllegalArgumentException("all LUT resources are required");
        }
    }
}
