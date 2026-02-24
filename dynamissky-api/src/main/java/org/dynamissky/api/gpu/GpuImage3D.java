package org.dynamissky.api.gpu;

/**
 * Abstract handle and dimensions for a GPU 3D texture resource.
 */
public record GpuImage3D(long handle, int width, int height, int depth) {
    public GpuImage3D {
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("width, height, and depth must be > 0");
        }
    }
}
