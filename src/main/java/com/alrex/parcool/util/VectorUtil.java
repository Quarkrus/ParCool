package com.alrex.parcool.util;

import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class VectorUtil {
	public static Vec3 fromYawDegree(double degree) {
		return new Vec3(-Math.sin(Math.toRadians(degree)), 0, Math.cos(Math.toRadians(degree)));
	}

    public static boolean isZero(Vec3 vector) {
        return vector.x == 0 && vector.y == 0 && vector.z == 0;
    }

    public static boolean isZero(Vec2 vector) {
        return vector.x == 0 && vector.y == 0;
    }

    public static Vec3 calculateReflectVector(Vec3 reflectingVec, Vec3 reflectionNormal) {
        var t = -reflectingVec.dot(reflectionNormal) / reflectionNormal.lengthSqr();
        return reflectingVec.add(reflectionNormal.scale(2 * t));
    }

    public static Vec3 calculateViewVector(float xRotInDegrees, float yRotInDegrees) {
        var xRotRad = Math.toRadians(xRotInDegrees);
        var yRotRad = -Math.toRadians(yRotInDegrees);
        return new Vec3(Math.sin(yRotRad) * Math.cos(xRotRad), -Math.sin(xRotRad), Math.cos(yRotRad) * Math.cos(xRotRad));
    }
}
