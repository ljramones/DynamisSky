module org.dynamisengine.sky.bench {
    requires org.dynamisengine.sky.api;
    requires org.dynamisengine.sky.core;
    requires org.dynamisengine.sky.vulkan;
    requires org.dynamisengine.sky.test;
    requires org.vectrix;
    requires jmh.core;

    exports org.dynamisengine.sky.bench;
    exports org.dynamisengine.sky.bench.core;
    exports org.dynamisengine.sky.bench.vulkan;
}
