package org.dynamissky.core.model;

import org.vectrix.core.Vector3f;

/**
 * Analytical sky model contract.
 */
public interface AnalyticalSkyModel {
    /**
     * @return CIE xyY packed into (x, y, Y)
     */
    Vector3f evaluate(Vector3f viewDir, Vector3f sunDir);

    default Vector3f evaluateLinearSrgb(Vector3f viewDir, Vector3f sunDir, Vector3f dest) {
        return SkyColorConversions.xyYToLinearSrgb(evaluate(viewDir, sunDir), dest);
    }
}
