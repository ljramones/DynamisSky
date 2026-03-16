package org.dynamisengine.sky.core.color;

import org.junit.jupiter.api.Test;
import org.dynamisengine.vectrix.color.ColorSciencef;
import org.dynamisengine.vectrix.core.Vector3f;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KelvinToRgbTest {
    @Test
    void producesWarmColorAt1800K() {
        Vector3f rgb = ColorSciencef.kelvinToLinearRgb(1800f, new Vector3f());
        assertTrue(rgb.x > rgb.z);
    }

    @Test
    void producesNearNeutralAt5500K() {
        Vector3f rgb = ColorSciencef.kelvinToLinearRgb(5500f, new Vector3f());
        assertTrue(java.lang.Math.abs(rgb.x - rgb.y) < 0.25f);
    }

    @Test
    void producesCoolerBlueAt6500K() {
        Vector3f cool = ColorSciencef.kelvinToLinearRgb(6500f, new Vector3f());
        Vector3f warm = ColorSciencef.kelvinToLinearRgb(1800f, new Vector3f());
        assertTrue(cool.z > warm.z);
    }
}
