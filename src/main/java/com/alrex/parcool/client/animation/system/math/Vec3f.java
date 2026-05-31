package com.alrex.parcool.client.animation.system.math;

import com.mojang.math.Vector3f;

public record Vec3f(float x, float y, float z) {
    public static final Vec3f ZERO = new Vec3f(0f, 0f, 0f);

    public Vec3f scale(float v) {
        return new Vec3f(x * v, y * v, z * v);
    }

    public Vec3f add(Vec3f v) {
        return new Vec3f(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    public Vector3f toVector3f() {
        return new Vector3f(x, y, z);
    }
}
