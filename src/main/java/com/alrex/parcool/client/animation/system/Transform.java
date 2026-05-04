package com.alrex.parcool.client.animation.system;

import com.alrex.parcool.client.animation.system.math.Vec3f;
import com.mojang.math.Quaternion;
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

}
