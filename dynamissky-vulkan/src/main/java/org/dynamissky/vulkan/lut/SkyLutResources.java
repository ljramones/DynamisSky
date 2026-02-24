package org.dynamissky.vulkan.lut;

import org.dynamissky.api.gpu.GpuImage2D;
import org.dynamissky.api.gpu.GpuImage3D;

/**
 * Owns all precomputed sky LUT image allocations.
 */
public final class SkyLutResources {
    public static final int VK_FORMAT_R16G16B16A16_SFLOAT = 97;

    private final GpuMemoryOps memoryOps;

    private GpuImage2D transmittanceLut;
    private GpuImage2D multiScatteringLut;
    private GpuImage2D skyViewLut;
    private GpuImage3D aerialPerspectiveLut;

    private GpuImage2DAlloc transmittanceAlloc;
    private GpuImage2DAlloc multiScatteringAlloc;
    private GpuImage2DAlloc skyViewAlloc;
    private GpuImage3DAlloc aerialAlloc;

    private SkyLutResources(GpuMemoryOps memoryOps) {
        this.memoryOps = memoryOps;
    }

    public static SkyLutResources create(GpuMemoryOps memoryOps) {
        SkyLutResources resources = new SkyLutResources(memoryOps);
        resources.transmittanceAlloc = memoryOps.createImage2D(256, 64, VK_FORMAT_R16G16B16A16_SFLOAT);
        resources.multiScatteringAlloc = memoryOps.createImage2D(32, 32, VK_FORMAT_R16G16B16A16_SFLOAT);
        resources.skyViewAlloc = memoryOps.createImage2D(192, 108, VK_FORMAT_R16G16B16A16_SFLOAT);
        resources.aerialAlloc = memoryOps.createImage3D(32, 32, 32, VK_FORMAT_R16G16B16A16_SFLOAT);

        resources.transmittanceLut = resources.transmittanceAlloc.image();
        resources.multiScatteringLut = resources.multiScatteringAlloc.image();
        resources.skyViewLut = resources.skyViewAlloc.image();
        resources.aerialPerspectiveLut = resources.aerialAlloc.image();
        return resources;
    }

    public void destroy() {
        if (transmittanceAlloc != null) {
            memoryOps.destroyImage(transmittanceLut.handle(), transmittanceAlloc.memoryHandle());
            SkyLutReadbackRegistry.clear(transmittanceLut.handle());
        }
        if (multiScatteringAlloc != null) {
            memoryOps.destroyImage(multiScatteringLut.handle(), multiScatteringAlloc.memoryHandle());
            SkyLutReadbackRegistry.clear(multiScatteringLut.handle());
        }
        if (skyViewAlloc != null) {
            memoryOps.destroyImage(skyViewLut.handle(), skyViewAlloc.memoryHandle());
            SkyLutReadbackRegistry.clear(skyViewLut.handle());
        }
        if (aerialAlloc != null) {
            memoryOps.destroyImage(aerialPerspectiveLut.handle(), aerialAlloc.memoryHandle());
        }
    }

    public GpuImage2D transmittanceLut() {
        return transmittanceLut;
    }

    public GpuImage2D multiScatteringLut() {
        return multiScatteringLut;
    }

    public GpuImage2D skyViewLut() {
        return skyViewLut;
    }

    public GpuImage3D aerialPerspectiveLut() {
        return aerialPerspectiveLut;
    }
}
