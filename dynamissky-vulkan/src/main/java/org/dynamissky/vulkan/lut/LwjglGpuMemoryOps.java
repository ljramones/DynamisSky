package org.dynamissky.vulkan.lut;

import org.dynamisgpu.vulkan.memory.VulkanImageAlloc;
import org.dynamisgpu.vulkan.memory.VulkanMemoryOps;
import org.dynamissky.api.gpu.GpuImage2D;
import org.dynamissky.api.gpu.GpuImage3D;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDevice;

import static org.lwjgl.vulkan.VK10.VK_IMAGE_TILING_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_TYPE_2D;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_TYPE_3D;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_STORAGE_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT;

/**
 * DynamisGPU-backed image allocator for sky LUT resources.
 */
public final class LwjglGpuMemoryOps implements GpuMemoryOps {
    private final VkDevice device;
    private final VkPhysicalDevice physicalDevice;

    public LwjglGpuMemoryOps(VkDevice device, VkPhysicalDevice physicalDevice) {
        this.device = device;
        this.physicalDevice = physicalDevice;
    }

    @Override
    public GpuImage2DAlloc createImage2D(int width, int height, int format) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VulkanImageAlloc alloc = VulkanMemoryOps.createImage(
                    device,
                    physicalDevice,
                    stack,
                    width,
                    height,
                    format,
                    VK_IMAGE_TILING_OPTIMAL,
                    VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT,
                    VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
                    VK_IMAGE_TYPE_2D);
            return new GpuImage2DAlloc(new GpuImage2D(alloc.image(), width, height), alloc.memory(), format);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to allocate 2D LUT image", e);
        }
    }

    @Override
    public GpuImage3DAlloc createImage3D(int width, int height, int depth, int format) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VulkanImageAlloc alloc = VulkanMemoryOps.createImage(
                    device,
                    physicalDevice,
                    stack,
                    width,
                    height,
                    depth,
                    format,
                    VK_IMAGE_TILING_OPTIMAL,
                    VK_IMAGE_USAGE_SAMPLED_BIT | VK_IMAGE_USAGE_STORAGE_BIT,
                    VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
                    VK_IMAGE_TYPE_3D);
            return new GpuImage3DAlloc(new GpuImage3D(alloc.image(), width, height, depth), alloc.memory(), format);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to allocate 3D LUT image", e);
        }
    }

    @Override
    public void destroyImage(long imageHandle, long memoryHandle) {
        // Lifecycle integration with engine allocator comes in next step.
    }
}
