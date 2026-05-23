package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.math.MathUtil;
import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.mojang.math.Quaternion;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public record Transform(Vec3f translation, Quaternion rotation) {
    public static final Transform NO_TRANSFORMATION = new Transform(Vec3f.ZERO, Quaternion.ONE.copy());

    /// @param t : blending factor, in [0,1]
    public Transform morph(Transform to, float t) {
        return new Transform(
                new Vec3f(
                        Mth.lerp(t, translation.x(), to.translation.x()),
                        Mth.lerp(t, translation.y(), to.translation.y()),
                        Mth.lerp(t, translation.z(), to.translation.z())
                ),
                new Quaternion(
                        Mth.lerp(t, rotation.i(), to.rotation.i()),
                        Mth.lerp(t, rotation.j(), to.rotation.j()),
                        Mth.lerp(t, rotation.k(), to.rotation.k()),
                        Mth.lerp(t, rotation.r(), to.rotation.r())
                )
        );
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
        part.xRot += MathUtil.rotLerp(blendingFactor, part.xRot, xRot);
        part.yRot += MathUtil.rotLerp(blendingFactor, part.yRot, yRot);
        part.zRot += MathUtil.rotLerp(blendingFactor, part.zRot, zRot);
        part.x = Mth.lerp(blendingFactor, part.x, translation.x());
        part.y = Mth.lerp(blendingFactor, part.y, translation.y());
        part.z = Mth.lerp(blendingFactor, part.z, translation.z());
    }

    public void apply(ModelPart part) {
        apply(part, 1f);
    }
}
