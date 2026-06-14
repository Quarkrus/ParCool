package com.alrex.parcool.common.action;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public enum InteractingWallDirection {
    XP(1, 0, false),
    XN(-1, 0, false),
    ZP(0, 1, false),
    ZN(0, -1, false),
    XP_ZP(1, 1, true),
    XP_ZN(1, -1, true),
    XN_ZP(-1, 1, true),
    XN_ZN(-1, -1, true);
    private final short signX;
    private final short signZ;
    private final boolean oblique;
    private final Vec3 normalizedVec;

    InteractingWallDirection(int signX, int signZ, boolean oblique) {
        this.signX = (short) signX;
        this.signZ = (short) signZ;
        this.oblique = oblique;
        this.normalizedVec = oblique
                ? new Vec3(signX, 0, signZ).scale(1 / Mth.sqrt(2))
                : new Vec3(signX, 0, signZ);
    }

    public Vec3 asVec() {
        return normalizedVec;
    }

    public short getSignX() {
        return signX;
    }

    public short getSignZ() {
        return signZ;
    }

    public boolean isOblique() {
        return oblique;
    }

    @Nullable
    public static InteractingWallDirection get(int signX, int signZ) {
        for (var direction : InteractingWallDirection.values()) {
            if (direction.signX == signX && direction.signZ == signZ) {
                return direction;
            }
        }
        return null;
    }
}
