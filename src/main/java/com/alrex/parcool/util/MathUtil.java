package com.alrex.parcool.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MathUtil {
	public static float lerp(float start, float end, float factor) {
		return start + (end - start) * factor;
	}

    public static float normalizeRadian(float angle) {
        return (float) (angle - 2 * Math.PI * Math.floor((angle + Math.PI) / (2. * Math.PI)));
    }

    public static float normalizeDegree(float angle) {
        return (float) (angle - 360f * Math.floor((angle + 180f) / 360f));
    }

    public static float mapLinear(float value, float rangeMin, float rangeMax, float start, float end) {
        if (value <= rangeMin) return start;
        if (value >= rangeMax) return end;
        return Mth.lerp((value - rangeMin) / (rangeMax - rangeMin), start, end);
    }

    public static Vec3 lerp(double factor, Vec3 v1, Vec3 v2) {
        return new Vec3(
                Mth.lerp(factor, v1.x, v2.x),
                Mth.lerp(factor, v1.y, v2.y),
                Mth.lerp(factor, v1.z, v2.z)
        );
    }
}
