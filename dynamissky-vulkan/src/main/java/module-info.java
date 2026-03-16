module org.dynamisengine.sky.vulkan {
    requires org.dynamisengine.sky.api;
    requires org.dynamisengine.sky.core;
    requires org.dynamisengine.vectrix;
    requires dynamis.gpu.vulkan;
    requires org.lwjgl;
    requires org.lwjgl.vulkan;

    requires org.dynamisengine.light.impl.common;

    exports org.dynamisengine.sky.vulkan;
    exports org.dynamisengine.sky.vulkan.lut;
    exports org.dynamisengine.sky.vulkan.stars;
    exports org.dynamisengine.sky.vulkan.integration;

    provides org.dynamisengine.light.impl.common.sky.SkyRenderBridge
        with org.dynamisengine.sky.vulkan.integration.SkyRenderBridgeProvider;
}
