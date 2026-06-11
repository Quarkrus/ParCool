package com.alrex.parcool.client.animation.system.math;

import com.mojang.math.Quaternion;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MathUtil {
    // normalize angle in [-pi,pi)
    public static float warpRadian(float angleRadian) {
        return (float) (angleRadian - 2 * Math.PI * Math.floor((angleRadian + Math.PI) / (2. * Math.PI)));
    }

    public static float rotLerp(float factor, float fromRadian, float toRadian) {
        return fromRadian + factor * warpRadian(toRadian - fromRadian);
    }

    public static Quaternion slerp(float factor, Quaternion from, Quaternion to, boolean useShortestPath) {
        from = from.copy();
        from.normalize();
        to = to.copy();
        to.normalize();
        var dot = MathUtil.dot(from, to);
        if (useShortestPath) {
            if (dot < 0) {
                to = new Quaternion(-to.i(), -to.j(), -to.k(), -to.r());
                dot = -dot;
            }
        }
        var diffAngle = (float) Math.acos(Mth.clamp(dot, 0, 0.999999f));
        var sinDiffAngle = Mth.sin(diffAngle);
        var fromScale = Mth.sin((1 - factor) * diffAngle) / sinDiffAngle;
        var toScale = Mth.sin(factor * diffAngle) / sinDiffAngle;
        return new Quaternion(
                fromScale * from.i() + toScale * to.i(),
                fromScale * from.j() + toScale * to.j(),
                fromScale * from.k() + toScale * to.k(),
                fromScale * from.r() + toScale * to.r()
        );
    }

    public static Vec3f rotate(Vec3f point, Quaternion rotation) {
        var conjRot = rotation.copy();
        var rot = rotation.copy();
        conjRot.conj();
        var pointQ = new Quaternion(point.x(), point.y(), point.z(), 0);
        rot.mul(pointQ);
        rot.mul(conjRot);
        return new Vec3f(rot.i(), rot.j(), rot.k());
    }

    public static float dot(Quaternion q1, Quaternion q2) {
        return q1.i() * q2.i() + q1.j() * q2.j() + q1.k() * q2.k() + q1.r() * q2.r();
    }

    public static double toYawRadian(Vec3 vec) {
        return (Math.atan2(vec.x(), vec.z()));
    }
}
