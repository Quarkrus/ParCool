package com.alrex.parcool.client.animation.system.data;

import com.alrex.parcool.client.animation.system.math.MathUtil;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public record Transform(Vec3f translation, Quaternion rotation) {
    public static final Transform NO_TRANSFORMATION = new Transform(Vec3f.ZERO, Quaternion.ONE.copy());
    public static final Transform NO_TRANSFORMATION_INV = NO_TRANSFORMATION.getInverseRotated();

    public static Transform fromRotationParams(float xRot, float yRot, float zRot) {
        var rot = Quaternion.ONE.copy();
        if (zRot != 0f) {
            rot.mul(Vector3f.ZP.rotation(zRot));
        }
        if (yRot != 0f) {
            rot.mul(Vector3f.YP.rotation(yRot));
        }
        if (xRot != 0f) {
            rot.mul(Vector3f.XP.rotation(xRot));
        }
        return new Transform(Vec3f.ZERO, rot);
    }

    public Transform getInverseRotated() {
        return new Transform(translation, new Quaternion(-rotation.i(), -rotation.j(), -rotation.k(), -rotation.r()));
    }

    /// @param t : blending factor, in [0,1]
    public Transform morph(Transform to, float t, boolean useShortestPath) {
        return new Transform(
                new Vec3f(
                        Mth.lerp(t, translation.x(), to.translation.x()),
                        Mth.lerp(t, translation.y(), to.translation.y()),
                        Mth.lerp(t, translation.z(), to.translation.z())
                ),
                MathUtil.slerp(t, this.rotation, to.rotation, useShortestPath)
        );
    }

    public Transform mirror() {
        return new Transform(
                new Vec3f(-translation.x(), translation().y(), translation().z()),
                new Quaternion(rotation.i(), -rotation.j(), -rotation.k(), rotation.r())
        );
    }

    public Transform append(Transform after, float t, boolean useShortestPath) {
        var rot = MathUtil.slerp(t, Quaternion.ONE, after.rotation, useShortestPath);
        var translation = MathUtil.rotate(this.translation, rot);
        rot.mul(this.rotation);
        return new Transform(translation.add(after.translation.scale(t)), rot);
    }

    public void apply(ModelPart part, float blendingFactor) {
        var q = rotation;
        float xRot, zRot, yRot = (float) Math.asin(2 * (-q.i() * q.k() + q.j() * q.r()));
        double cosY = Math.cos(yRot);
        if (Math.abs(cosY) > 1e-4) {
            xRot = (float) Math.atan2(
                    q.j() * q.k() + q.i() * q.r(),
                    q.r() * q.r() + q.k() * q.k() - 0.5
            );
            zRot = (float) Math.atan2(
                    q.i() * q.j() + q.k() * q.r(),
                    q.r() * q.r() + q.i() * q.i() - 0.5
            );
        } else {
            xRot = 0;
            zRot = (float) Math.atan2(
                    -q.i() * q.j() + q.k() * q.r(),
                    q.r() * q.r() + q.j() * q.j() - 0.5
            );
        }
        part.xRot = MathUtil.rotLerp(blendingFactor, part.xRot, -xRot);
        part.yRot = MathUtil.rotLerp(blendingFactor, part.yRot, -yRot);
        part.zRot = MathUtil.rotLerp(blendingFactor, part.zRot, zRot);
        part.x += blendingFactor * -translation.x() * 16f;
        part.y += blendingFactor * -translation.y() * 16f;
        part.z += blendingFactor * translation.z() * 16f;
    }

    public void apply(ModelPart part) {
        apply(part, 1f);
    }
}
