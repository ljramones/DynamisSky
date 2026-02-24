package org.dynamissky.vulkan.integration;

/**
 * Enforces frame-ordering for sky integration calls.
 */
public enum SkyRenderPhase {
    NOT_STARTED,
    UPDATE_COMPLETE,
    BACKGROUND_COMPLETE,
    CELESTIAL_COMPLETE,
}
