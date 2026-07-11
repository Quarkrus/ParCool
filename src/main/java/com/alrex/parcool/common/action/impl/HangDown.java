package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.action.*;
import com.alrex.parcool.client.animation.ParCoolAnimations;
import com.alrex.parcool.client.animation.system.PlayerAnimator;
import com.alrex.parcool.client.input.ParCoolKeyBinds;
import com.alrex.parcool.common.Parkourability;
import com.alrex.parcool.common.action.ParCoolActions;
import com.alrex.parcool.util.EntityUtil;
import com.alrex.parcool.util.MathUtil;
import com.alrex.parcool.util.VectorUtil;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;

public class HangDown extends ContinuableAction {
    enum BarAxis {
        X(new Vec3(1, 0, 0), new Vec3(0, 0, 1)),
        Z(new Vec3(0, 0, 1), new Vec3(1, 0, 0));
        private final Vec3 positiveVec;
        private final Vec3 orthogonalVec;

        BarAxis(Vec3 positiveVec, Vec3 orthogonalVec) {
            this.positiveVec = positiveVec;
            this.orthogonalVec = orthogonalVec;
        }

        public Vec3 getOrthogonalVec() {
            return orthogonalVec;
        }

        public Vec3 getPositiveVec() {
            return positiveVec;
        }
    }

    private final SynchronizedDataHolder dataHolder;
    private final SynchronizedProperty<BarAxis> propertyHangingBarAxis;
    private final SynchronizedProperty<Float> propertyBodySwingAngleInRad;
    private final SynchronizedProperty<Float> propertyBodyAngularSpeedInRad;
    private final SynchronizedProperty<Boolean> propertyJumpOff;

    // Only for local client
    @Nullable
    private BlockPos hangingPos;
    private boolean barNotFound;
    // Only for client
    private float oldAngle;
    private float oldAngularSpeed;

    public HangDown(Parkourability parkourability, ActionEntry<? extends Action> entry) {
        super(parkourability, entry, List.of(ParCoolActions.CLIMB_UP, ParCoolActions.DIVE, ParCoolActions.HANG_ON));
        dataHolder = SynchronizedDataHolder.create(entry,
                propertyHangingBarAxis = SynchronizedProperty.newEnum(BarAxis.class),
                propertyBodySwingAngleInRad = SynchronizedProperty.newFloat(),
                propertyBodyAngularSpeedInRad = SynchronizedProperty.newFloat(),
                propertyJumpOff = SynchronizedProperty.newBoolean()
        );
    }

    @Override
    public SynchronizedDataHolder getSynchronizedData() {
        return dataHolder;
    }

    @Override
    public boolean canContinue() {
        var continuing = !barNotFound && ParCoolKeyBinds.HANG.key().isDown() && !ParCoolKeyBinds.JUMP.state().isJustPressed();
        if (continuing) {
            if (Math.abs(propertyBodyAngularSpeedInRad.getOrDefaultIfNull(0f)) > Mth.PI / 20f) {
                propertyJumpOff.set(true);
            }
        }
        return continuing;
    }

    @Override
    public boolean canStart() {
        var player = parkourability.player();
        if (Math.abs(player.getDeltaMovement().y) > 0.2 || !ParCoolKeyBinds.HANG.key().isDown()) return false;
        var hangingBar = getHangAbleBars(player);
        if (hangingBar == null) return false;

        var lookingDirection = EntityUtil.getHorizontalLookAngle(player);
        var barOrthogonalVec = hangingBar.getB().getOrthogonalVec().dot(lookingDirection) > 0
                ? hangingBar.getB().getOrthogonalVec()
                : hangingBar.getB().getOrthogonalVec().reverse();
        var pos = player.position();
        var speed = barOrthogonalVec.x * (pos.x - player.xo) + barOrthogonalVec.z * (pos.z - player.zo);
        var playerHeight = player.getBbHeight();

        propertyBodyAngularSpeedInRad.set((float) (speed / playerHeight));
        propertyBodySwingAngleInRad.set(0f);
        propertyHangingBarAxis.set(hangingBar.getB());
        propertyJumpOff.set(false);
        barNotFound = false;
        hangingPos = hangingBar.getA();

        return true;
    }

    @Override
    public void onStartInLocalClient() {
        if (!(parkourability.player() instanceof LocalPlayer player)) return;
        parkourability.getBehaviorEnforcer().setMarkerEnforcingMovePoint(
                this::isDoing, () -> {
                    var currentBarAxis = propertyHangingBarAxis.get();
                    if (currentBarAxis == null) return null;
                    if (hangingPos == null) return null;
                    var speed = player.getSpeed();
                    var moveVec = player.input.getMoveVector().scale(speed);
                    var actualMoveVec = new Vec3(moveVec.x, 0, moveVec.y).yRot((float) Math.toRadians(-player.getYRot()));
                    var barAxisVec = currentBarAxis.getPositiveVec();
                    var movementLength = actualMoveVec.dot(barAxisVec);
                    var currentPos = player.position();
                    var currentHangingPos = new Vec3(hangingPos.getX() + 0.5, currentPos.y, hangingPos.getZ() + 0.5);
                    var t = (currentPos.subtract(currentHangingPos).dot(barAxisVec)) / barAxisVec.lengthSqr();
                    var alignedPos = currentHangingPos.add(barAxisVec.scale(t));
                    return alignedPos.add(barAxisVec.scale(movementLength)).add(0, 0.01, 0);
                }
        );
    }

    @Override
    public void onStartInClient() {
        oldAngularSpeed = 0;
        oldAngle = 0;
        PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.HANG_DOWN);
    }

    @Override
    public void onStopInClient() {
        if (propertyJumpOff.getOrDefaultIfNull(false)) {
            if (propertyBodyAngularSpeedInRad.getOrDefaultIfNull(1f) > 0f) {
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.HANG_DOWN_JUMP_FORWARD);
            } else {
                PlayerAnimator.get((AbstractClientPlayer) parkourability.player()).start(ParCoolAnimations.HANG_DOWN_JUMP_BACKWARD);
            }
        }

    }

    @Override
    public void onStopInLocalClient() {
        var player = parkourability.player();
        var barAxis = propertyHangingBarAxis.get();
        if (barAxis == null) return;
        var orthogonalVec = VectorUtil.reverseIfInReverseDirection(EntityUtil.getHorizontalLookAngle(player), barAxis.getOrthogonalVec());
        float angle = propertyBodySwingAngleInRad.getOrDefaultIfNull(0f);
        var sinAngle = Math.sin(angle);
        var cosAngle = Math.cos(angle);
        var finishedPos = player.position();
        var playerHeight = player.getBbHeight();
        var swingingPos = finishedPos.add(
                orthogonalVec.x * sinAngle,
                0.5 * playerHeight * (1. - cosAngle),
                orthogonalVec.z * sinAngle
        );
        if (propertyJumpOff.getOrDefaultIfNull(false)) {
            var movement = new Vec3(
                    orthogonalVec.x * cosAngle,
                    Math.abs(sinAngle),
                    orthogonalVec.z * cosAngle
            ).scale(2. * playerHeight * propertyBodyAngularSpeedInRad.getOrDefaultIfNull(0f));
            parkourability.getBehaviorEnforcer().setMarkerEnforcingMovePoint(
                    () -> getNotDoingTick() <= 5,
                    () -> swingingPos.add(movement.scale((getNotDoingTick() + 1.) / 6.))
            );
        } else {
            parkourability.getBehaviorEnforcer().setMarkerEnforcingPosition(
                    () -> getNotDoingTick() <= 1,
                    () -> VectorUtil.lerp((getNotDoingTick() + 1.) / 2., finishedPos, swingingPos)
            );
        }
    }

    @Override
    public void onWorkingTickInLocalClient() {
        var player = parkourability.player();
        if (!(player instanceof LocalPlayer localPlayer)) return;
        var gravityAttr = localPlayer.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
        if (gravityAttr == null) return;
        var barAxis = propertyHangingBarAxis.get();
        if (barAxis == null) return;
        float angularSpeedInRad = propertyBodyAngularSpeedInRad.getOrDefaultIfNull(0f);
        float angle = propertyBodySwingAngleInRad.getOrDefaultIfNull(0f);
        var lookVec = EntityUtil.getHorizontalLookAngle(localPlayer);
        var barOrthogonalVec = barAxis.getOrthogonalVec();
        var dotOfOrthogonalAndLook = (float) barOrthogonalVec.dot(lookVec);
        if (dotOfOrthogonalAndLook < 0) barOrthogonalVec = barOrthogonalVec.reverse();
        var absDotOfOrthogonalAndLook = Mth.abs(dotOfOrthogonalAndLook);
        var input = localPlayer.input;
        var movementInput = lookVec.scale(input.forwardImpulse).add(lookVec.yRot(Mth.HALF_PI).scale(input.leftImpulse));
        var inputAcceleration = 0.02f * (float) barOrthogonalVec.dot(movementInput);
        var cosAngle = Mth.cos(angle);
        var sinAngle = Mth.sin(angle);
        var acceleration = new Vec2( // x is for horizontal, y is for vertical
                cosAngle * inputAcceleration,
                sinAngle * inputAcceleration + (float) -gravityAttr.getValue()
        ).dot(new Vec2(cosAngle, sinAngle));

        angularSpeedInRad += acceleration / player.getBbHeight();
        angularSpeedInRad *= absDotOfOrthogonalAndLook * 0.98f;
        var newAngle = absDotOfOrthogonalAndLook * (angle + angularSpeedInRad);
        if (newAngle > Mth.PI * 0.75f) {
            newAngle = Mth.PI * 0.75f;
            angularSpeedInRad = 0;
        } else if (newAngle < Mth.PI * -0.75f) {
            newAngle = Mth.PI * -0.75f;
            angularSpeedInRad = 0;
        }

        propertyBodySwingAngleInRad.set(newAngle);
        propertyBodyAngularSpeedInRad.set(angularSpeedInRad);
        var hangingBar = getHangAbleBars(player);
        if (hangingBar != null) {
            propertyHangingBarAxis.set(hangingBar.getB());
        } else {
            barNotFound = true;
        }
        hangingPos = hangingBar != null ? hangingBar.getA() : null;
    }

    @Override
    public void onWorkingTickInClient() {
        oldAngle = propertyBodySwingAngleInRad.getOrDefaultIfNull(0f);
        oldAngularSpeed = propertyBodyAngularSpeedInRad.getOrDefaultIfNull(0f);
    }

    public float getRotationAngle(float partial) {
        return Mth.lerp(partial, oldAngle, propertyBodySwingAngleInRad.getOrDefaultIfNull(0f));
    }

    public float getAngularSpeed(float partial) {
        return Mth.lerp(partial, oldAngularSpeed, propertyBodyAngularSpeedInRad.getOrDefaultIfNull(0f));
    }

    public float getBlendFactorOrthogonalToBar(float partial) {
        var currentBarAxis = propertyHangingBarAxis.get();
        if (currentBarAxis == null) {
            return 0;
        }
        var barOrthogonalVec = currentBarAxis.getOrthogonalVec();
        var player = parkourability.player();
        var lookingDirection = VectorUtil.fromYawDegree(Mth.lerp(partial, player.yRotO, player.getYRot()));
        return MathUtil.mapLinear((float) Math.abs(lookingDirection.dot(barOrthogonalVec)), 0.5736f /*cos(55 degrees)*/, 0.8129f /*cos(35 degrees)*/, 0, 1);
    }

    @Nullable
    public Vec3 getBodyDirection(float partial) {
        var barAxis = propertyHangingBarAxis.get();
        if (barAxis == null) return null;
        var lookVec = EntityUtil.getHorizontalLookAngle(parkourability.player());
        return VectorUtil.lerp(
                getBlendFactorOrthogonalToBar(partial),
                VectorUtil.reverseIfInReverseDirection(lookVec, barAxis.getPositiveVec()),
                VectorUtil.reverseIfInReverseDirection(lookVec, barAxis.getOrthogonalVec())
        );
    }

    @Nullable
    private static Tuple<BlockPos, HangDown.BarAxis> getHangAbleBars(LivingEntity entity) {
        final double bbWidth = entity.getBbWidth() / 4;
        final double bbHeight = 0.35;
        var level = entity.level;
        var bb = new AABB(
                entity.getX() - bbWidth,
                entity.getY() + entity.getBbHeight(),
                entity.getZ() - bbWidth,
                entity.getX() + bbWidth,
                entity.getY() + entity.getBbHeight() + bbHeight,
                entity.getZ() + bbWidth
        );
        if (level.noCollision(entity, bb)) return null;
        var pos = new BlockPos(
                Mth.floor(entity.getX()),
                Mth.floor(entity.getY() + entity.getBbHeight() + 0.4),
                Mth.floor(entity.getZ())
        );
        if (!level.isLoaded(pos)) return null;
        var state = level.getBlockState(pos);
        var block = state.getBlock();
        HangDown.BarAxis axis = null;
        if (block instanceof RotatedPillarBlock) {
            if (state.isCollisionShapeFullBlock(level, pos)) {
                return null;
            }
            var pillarAxis = state.getValue(RotatedPillarBlock.AXIS);
            axis = switch (pillarAxis) {
                case X -> BarAxis.X;
                case Z -> BarAxis.Z;
                default -> axis;
            };
        } else if (block instanceof EndRodBlock) {
            if (state.isCollisionShapeFullBlock(entity.level, pos)) {
                return null;
            }
            var direction = state.getValue(DirectionalBlock.FACING);
            axis = switch (direction) {
                case EAST, WEST -> BarAxis.X;
                case NORTH, SOUTH -> BarAxis.Z;
                default -> axis;
            };
        } else if (block instanceof CrossCollisionBlock) {
            int zCount = 0;
            int xCount = 0;
            if (state.getValue(CrossCollisionBlock.NORTH)) zCount++;
            if (state.getValue(CrossCollisionBlock.SOUTH)) zCount++;
            if (state.getValue(CrossCollisionBlock.EAST)) xCount++;
            if (state.getValue(CrossCollisionBlock.WEST)) xCount++;
            if (zCount > 0 && xCount == 0) axis = HangDown.BarAxis.Z;
            if (xCount > 0 && zCount == 0) axis = HangDown.BarAxis.X;
        } else if (block instanceof WallBlock) {
            int zCount = 0;
            int xCount = 0;
            if (state.getValue(WallBlock.NORTH_WALL) != WallSide.NONE) zCount++;
            if (state.getValue(WallBlock.SOUTH_WALL) != WallSide.NONE) zCount++;
            if (state.getValue(WallBlock.EAST_WALL) != WallSide.NONE) xCount++;
            if (state.getValue(WallBlock.WEST_WALL) != WallSide.NONE) xCount++;
            if (zCount > 0 && xCount == 0) axis = HangDown.BarAxis.Z;
            if (xCount > 0 && zCount == 0) axis = HangDown.BarAxis.X;
        }

        return axis != null ? new Tuple<>(pos, axis) : null;
    }
}
