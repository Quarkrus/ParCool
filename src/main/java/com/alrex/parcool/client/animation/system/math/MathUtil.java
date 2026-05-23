package com.alrex.parcool.client.animation.system.math;

public class MathUtil {
    // normalize angle in [-pi,pi)
    public static float warpRadian(float angleRadian) {
        return (float) (angleRadian - 2 * Math.PI * Math.floor((angleRadian + Math.PI) / (2. * Math.PI)));
    }

    public static float rotLerp(float factor, float fromRadian, float toRadian) {
        return fromRadian + factor * warpRadian(toRadian - fromRadian);
    }
}
