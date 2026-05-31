package com.alrex.parcool.client.animation.system.data;

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

    public Transform append(Transform after, float t) {
        var rot = after.rotation.copy();
        rot.mul(this.rotation);
        return new Transform(this.translation.add(after.translation.scale(t)), rot);
    }

    public void apply(ModelPart part, float blendingFactor) {
        var xyzAngle = rotation.toXYZ();
        part.xRot = MathUtil.rotLerp(blendingFactor, part.xRot, xyzAngle.x());
        part.yRot = MathUtil.rotLerp(blendingFactor, part.yRot, xyzAngle.y());
        part.zRot = MathUtil.rotLerp(blendingFactor, part.zRot, xyzAngle.z());
        part.x += blendingFactor * translation.x();
        part.y += blendingFactor * translation.y();
        part.z += blendingFactor * translation.z();
    }

    public void apply(ModelPart part) {
        apply(part, 1f);
    }
}
