package com.alrex.parcool.client.animation.system.math;

public record Vec3f(float x, float y, float z) {
    public static final Vec3f ZERO = new Vec3f(0f, 0f, 0f);
}
