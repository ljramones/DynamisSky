module org.dynamisengine.sky.vulkan {
    requires org.dynamisengine.sky.api;
    requires org.dynamisengine.sky.core;
    requires org.vectrix;
    requires dynamis.gpu.vulkan;
    requires org.lwjgl;
    requires org.lwjgl.vulkan;

    exports org.dynamisengine.sky.vulkan;
    exports org.dynamisengine.sky.vulkan.lut;
    exports org.dynamisengine.sky.vulkan.stars;
    exports org.dynamisengine.sky.vulkan.integration;
}
