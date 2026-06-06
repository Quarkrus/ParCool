package com.alrex.parcool.client.animation.system.math;

import com.mojang.math.Quaternion;

public class MathUtil {
    // normalize angle in [-pi,pi)
    public static float warpRadian(float angleRadian) {
        return (float) (angleRadian - 2 * Math.PI * Math.floor((angleRadian + Math.PI) / (2. * Math.PI)));
    }

    public static float rotLerp(float factor, float fromRadian, float toRadian) {
        return fromRadian + factor * warpRadian(toRadian - fromRadian);
    }

    public static float dot(Quaternion q1, Quaternion q2) {
        return q1.i() * q2.i() + q1.j() * q2.j() + q1.k() * q2.k() + q1.r() * q2.r();
    }
}
