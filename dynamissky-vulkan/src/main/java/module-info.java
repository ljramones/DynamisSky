module org.dynamissky.vulkan {
    requires org.dynamissky.api;
    requires org.dynamissky.core;
    requires org.vectrix;
    requires dynamis.gpu.vulkan;
    requires org.lwjgl;
    requires org.lwjgl.vulkan;

    exports org.dynamissky.vulkan;
    exports org.dynamissky.vulkan.descriptor;
    exports org.dynamissky.vulkan.lut;
    exports org.dynamissky.vulkan.pass;
    exports org.dynamissky.vulkan.stars;
    exports org.dynamissky.vulkan.moon;
    exports org.dynamissky.vulkan.hdri;
    exports org.dynamissky.vulkan.integration;
}
