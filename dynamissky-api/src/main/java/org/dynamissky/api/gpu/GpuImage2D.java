package org.dynamissky.api.gpu;

/**
 * Abstract handle and dimensions for a GPU 2D texture resource.
 */
public record GpuImage2D(long handle, int width, int height) {
    public GpuImage2D {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
    }
}
