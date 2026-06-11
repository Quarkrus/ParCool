package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.*;
import com.alrex.parcool.util.MathUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Collections;

public class HangOn extends ContinuableAction {
    private static final double REACH_SCALE = 0.25;
    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<HangDirection> property_direction;
    private final SynchronizedProperty<Boolean> property_fullWall;

    @Nullable
    private HangDirection oldDirection = null;
    // Only for Local Client
    @Nullable
    private HangState currentHangState;
    @Nullable
    private HangState startingHangState;

    public HangOn(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry);
        var builder = new SynchronizedDataHolder.Builder((byte) 2);
        property_direction = builder.register(() -> SynchronizedProperty.newEnum(HangDirection.class, (newV, oldV) -> oldDirection = oldV));
        property_fullWall = builder.register(SynchronizedProperty::newBoolean);
        dataHolder = builder.build(entry);
    }

    @Override
    public boolean canStart() {
        return KeyBindings.getKeyGrabWall().isDown() && (startingHangState = getHangState()) != null;
    }

    @Override
    public boolean canContinue() {
        return KeyBindings.getKeyGrabWall().isDown() && currentHangState != null;
    }

    @Override
    public void onStartInLocalClient() {
        parkourability.getBehaviorEnforcer().setMarkerEnforceMovePoint(
                this::isDoing, () -> {
                    if (currentHangState == null) return null;
                    if (!(parkourability.player() instanceof LocalPlayer player)) return null;
                    var speed = currentHangState.fullWall
                            ? player.getSpeed() * MathUtil.mapLinear((float) currentHangState.direction.asNormalizedVec().dot(player.getLookAngle().multiply(1, 0, 1).normalize()), -0.7071f, 1f, 0f, 1f)
                            : player.getSpeed();
                    var moveVec = player.input.getMoveVector().scale(speed);
                    var actualMoveVec = new Vec3(moveVec.x, 0, moveVec.y).yRot((float) Math.toRadians(-player.getYRot()));
                    if (currentHangState.onProtrusion) {
                        return parkourability.player().position()
                                .add(new Vec3(
                                        currentHangState.direction.signX > 0 ? Math.max(0, actualMoveVec.x) : Math.min(0, actualMoveVec.x), 0,
                                        currentHangState.direction.signZ > 0 ? Math.max(0, actualMoveVec.z) : Math.min(0, actualMoveVec.z)
                                ));
                    } else if (currentHangState.direction.oblique) {
                        var directionVec = currentHangState.direction.asVec().yRot(Mth.HALF_PI);
                        return parkourability.player().position()
                                .add(directionVec.scale(directionVec.dot(actualMoveVec)));
                    } else {
                        return parkourability.player().position()
                                .add(currentHangState.direction.signX * 0.2, currentHangState.yCollisionDistance, currentHangState.direction.signZ * 0.2)
                                .add(actualMoveVec);
                    }
                }
        );
    }

    @Override
    public void onStartInClient() {
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.HANG_ON);
    }

    @Override
    public void onWorkingTickInLocalClient() {
        if (startingHangState != null) {
            currentHangState = startingHangState;
            startingHangState = null;
        } else {
            currentHangState = getHangState();
        }
        property_direction.set(currentHangState != null ? currentHangState.direction : null);
        property_fullWall.set(currentHangState != null ? currentHangState.fullWall : null);
    }

    @Nullable
    public Vec3 getWallVec(float partial) {
        var direction = property_direction.get();
        if (oldDirection == null) {
            if (direction == null) {
                return null;
            }
            return direction.asNormalizedVec();
        }
        if (direction == null) return oldDirection.asNormalizedVec();
        return MathUtil.lerp(partial, oldDirection.asVec(), direction.asVec()).normalize();
    }

    public float getBlendFactorLeftToWall(float partial) {
        var direction = property_direction.get();
        if (direction == null) return 0;
        var fullWall = property_fullWall.get();
        if (fullWall == null || !fullWall) return 0;
        var lookVec = parkourability.player().getLookAngle().multiply(1, 0, 1).normalize();
        var wallVec = direction.asNormalizedVec();
        if (wallVec.yRot(Mth.HALF_PI).dot(lookVec) < 0) return 0;
        return MathUtil.mapLinear(
                (float) wallVec.reverse().dot(lookVec), 0, 0.7071f /*cos(pi/4)*/, 0, 1
        );
    }

    public float getBlendFactorRightToWall(float partial) {
        var direction = property_direction.get();
        if (direction == null) return 0;
        var fullWall = property_fullWall.get();
        if (fullWall == null || !fullWall) return 0;
        var lookVec = parkourability.player().getLookAngle().multiply(1, 0, 1).normalize();
        var wallVec = direction.asNormalizedVec();
        if (wallVec.yRot(Mth.HALF_PI).dot(lookVec) > 0) return 0;
        return MathUtil.mapLinear(
                (float) wallVec.reverse().dot(lookVec), 0, 0.7071f /*cos(pi/4)*/, 0, 1
        );
    }

    public float getBlendFactorBackToWall(float partial) {
        var direction = property_direction.get();
        if (direction == null) return 0;
        var fullWall = property_fullWall.get();
        if (fullWall == null || !fullWall) return 0;
        var lookVec = parkourability.player().getLookAngle().multiply(1, 0, 1).normalize();
        var wallVec = direction.asNormalizedVec();
        return MathUtil.mapLinear(
                (float) wallVec.reverse().dot(lookVec), 0.7071f /*cos(pi/4)*/, 1, 0, 1
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    private enum HangDirection {
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

        HangDirection(int signX, int signZ, boolean oblique) {
            this.signX = (short) signX;
            this.signZ = (short) signZ;
            this.oblique = oblique;
        }

        Vec3 asVec() {
            return new Vec3(signX, 0, signZ);
        }

        Vec3 asNormalizedVec() {
            if (oblique) return new Vec3(signX, 0, signZ).scale(1 / Mth.sqrt(2));
            return new Vec3(signX, 0, signZ);
        }

        @Nullable
        static HangDirection get(int signX, int signZ) {
            for (var direction : HangDirection.values()) {
                if (direction.signX == signX && direction.signZ == signZ) {
                    return direction;
                }
            }
            return null;
        }
    }

    private record HangState(
            HangDirection direction,
            AABB handBoundingBox,
            double yCollisionDistance,
            boolean onProtrusion,
            boolean fullWall
    ) {
    }

    @Nullable
    private HangState getHangState() {
        var player = parkourability.player();
        var level = player.level;
        var playerBB = player.getBoundingBox();
        short signX = 0, signZ = 0;
        double xRange = playerBB.getXsize() * 0.25, zRange = playerBB.getZsize() * 0.25;

        boolean protrusion = false;
        if (!level.noCollision(playerBB.expandTowards(xRange, 0, 0))) {
            signX++;
        }
        if (!level.noCollision(playerBB.expandTowards(-xRange, 0, 0))) {
            signX--;
        }
        if (!level.noCollision(playerBB.expandTowards(0, 0, zRange))) {
            signZ++;
        }
        if (!level.noCollision(playerBB.expandTowards(0, 0, -zRange))) {
            signZ--;
        }

        var direction = HangDirection.get(signX, signZ);
        if (direction == null) {
            protrusion = true;
            signX = 0;
            signZ = 0;
            if (!level.noCollision(playerBB.expandTowards(xRange, 0, zRange))) {
                signX++;
                signZ++;
            }
            if (!level.noCollision(playerBB.expandTowards(-xRange, 0, -zRange))) {
                signX--;
                signZ--;
            }
            direction = HangDirection.get(signX, signZ);
            if (direction == null) {
                signX = 0;
                signZ = 0;
                if (!level.noCollision(playerBB.expandTowards(xRange, 0, -zRange))) {
                    signX++;
                    signZ--;
                }
                if (!level.noCollision(playerBB.expandTowards(-xRange, 0, zRange))) {
                    signX--;
                    signZ++;
                }
                direction = HangDirection.get(signX, signZ);
            }
        }


        if (direction == null) return null;

        var bb = playerBB.expandTowards(
                direction.signX * REACH_SCALE * playerBB.getXsize(), 0,
                direction.signZ * REACH_SCALE * playerBB.getZsize()
        );
        if (level.noCollision(player, bb)) return null;
        var grabbingBB = getGrabbingHandAABB(direction);
        if (!level.noCollision(player, grabbingBB)) return null;
        var downReach = -playerBB.getYsize() * 0.2;
        var collision = Entity.collideBoundingBox(player, new Vec3(0, downReach, 0), grabbingBB, level, Collections.emptyList());
        if (collision.y > downReach) {
            var legBB = new AABB(playerBB.minX, playerBB.minY, playerBB.minZ, playerBB.maxX, playerBB.minY + playerBB.getYsize() / 3, playerBB.maxZ).expandTowards(signX * xRange, 0, signZ * zRange);
            return new HangState(direction, grabbingBB, collision.y, protrusion, !level.noCollision(legBB));
        }
        return null;
    }

    private AABB getGrabbingHandAABB(HangDirection direction) {
        var player = parkourability.player();
        var playerBB = player.getBoundingBox();
        var center = playerBB.getCenter();
        double x1, x2;
        if (direction.signX != 0) {
            x1 = center.x;
            x2 = x1 + direction.signX * playerBB.getXsize() * (0.5 + REACH_SCALE);
        } else {
            x1 = playerBB.minX;
            x2 = playerBB.maxX;
        }
        double z1, z2;
        if (direction.signZ != 0) {
            z1 = center.z;
            z2 = z1 + direction.signZ * playerBB.getZsize() * (0.5 + REACH_SCALE);
        } else {
            z1 = playerBB.minZ;
            z2 = playerBB.maxZ;
        }

        return new AABB(x1, playerBB.maxY + playerBB.getYsize() * 0.125, z1, x2, playerBB.maxY + playerBB.getYsize() * 0.25, z2);
    }
}
